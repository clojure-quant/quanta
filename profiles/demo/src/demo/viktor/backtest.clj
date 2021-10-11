(ns demo.viktor.backtest
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [tick.alpha.api :as t]
   [tech.v3.dataset.print :as print]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.datetime :as datetime]
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tablecloth]
   [ta.dataset.helper :as helper]
   [ta.series.indicator :as ind]
   [ta.series.ta4j :as ta4j]
   [ta.warehouse :as wh]
   [demo.env.warehouse :refer [w]]
   [demo.studies.helper.experiments-helpers :as experiments-helpers]))

(defn make-filename [frequency symbol]
  (str symbol "-" frequency))

;; CALCULATE STRATEGY

(defn study-bollinger-indicator [ds {:keys [sma-length stddev-length mult-up mult-down] :as options}]
  "adds bollinger indicator to dataset
   * Middle Band = 20-day simple moving average (SMA)
   * Upper Band = 20-day SMA + (20-day standard deviation of price x 2) 
   * Lower Band = 20-day SMA - (20-day standard deviation of price x 2)"
  (let [; input data needed for ta4j indicators
        bars (ta4j/ds->ta4j-ohlcv ds)
        close (ta4j/ds->ta4j-close ds)
        ; setup the ta4j indicators
        sma (ta4j/ind :SMA close sma-length)
        stddev (ta4j/ind :statistics/StandardDeviation close stddev-length)
        bb-middle (ta4j/ind :bollinger/BollingerBandsMiddle sma)
        bb-upper (ta4j/ind :bollinger/BollingerBandsUpper bb-middle stddev (ta4j/num-decimal mult-up))
        bb-lower (ta4j/ind :bollinger/BollingerBandsLower bb-middle stddev (ta4j/num-decimal mult-down))
        ; calculate the indicators
        bb-upper-values  (ta4j/ind-values bb-upper)
        bb-lower-values  (ta4j/ind-values bb-lower)]
    (-> ds
        (tablecloth/add-column :bb-lower bb-lower-values)
        (tablecloth/add-column :bb-upper bb-upper-values))))

(defn calc-is-above [{:keys [close bb-upper] :as row}]
  (> close bb-upper))

(defn calc-is-below [{:keys [close bb-lower] :as row}]
  (< close bb-lower))

(defn add-above-below [ds]
  (tablecloth/add-columns
   ds
   {:above (map calc-is-above (tds/mapseq-reader ds))
    :below (map calc-is-below (tds/mapseq-reader ds))}))

(defn is-above-or-below [row]
  (or (:above row) (:below row)))

(defn drop-beginning [ds {:keys [sma-length stddev-length mult-up mult-down] :as options}]
  (tablecloth/select-rows ds (range sma-length (tablecloth/row-count ds))))

(defn xf-trailing-true-counter [xf]
  (let [past-up-count (atom 0)]
    (fn
      ;; SET-UP
      ([]
       (xf)
       (reset! past-up-count 0))
     	;; PROCESS
      ([result input]
       (if input
         (swap! past-up-count inc)
         (reset! past-up-count 0))
       (xf result @past-up-count))
      ;; TEAR-DOWN
      ([result]
       (xf result)))))

(defn calc-trailing-true-counter [df column]
  (into [] xf-trailing-true-counter (get df column)))

(defn add-trailing-count [ds]
  (tablecloth/add-columns
   ds
   {:above-count (calc-trailing-true-counter ds :above)
    :below-count (calc-trailing-true-counter ds :below)}))

(defn filter-count-1 [ds]
  (tablecloth/select-rows
   ds
   (fn [{:keys [above-count below-count]}]
     (or (= 1 above-count) (= 1 below-count)))))

(defn add-running-index [ds]
  (tablecloth/add-column ds :index (range 1 (inc (tablecloth/row-count ds)))))

(defn study-bollinger [ds {:keys [sma-length stddev-length mult-up mult-down] :as options}]
  (let [ds-study (study-bollinger-indicator ds options)]
    (-> ds-study
        add-running-index
        add-above-below
        add-trailing-count)))

(defn study-bollinger-filter-events [ds-study options]
  (-> ds-study
      (drop-beginning options)
      (tablecloth/select-rows is-above-or-below)
      filter-count-1))

; study runner (will go to ta library)

(defn make-study-filename [study-name frequency symbol]
  (str "study-" study-name "-" symbol "-" frequency))

(defn run-study [symbol frequency algo options]
  (let [ds (wh/load-ts w (make-filename frequency symbol))
        ds-study (algo ds options)]
    ds-study))

(defn save-study [ds-study symbol frequency study-name]
  (wh/save-ts w ds-study (make-study-filename study-name frequency symbol))
  (tablecloth/write! ds-study (str "../db/study-" study-name "-" symbol "-" frequency ".csv"))
  ds-study ; important to be here, as save-study is used often in a threading macro
  )

(defn load-study [symbol frequency study-name]
  (let [ds (wh/load-ts w (make-study-filename study-name frequency symbol))]
    ds))

; forward statistics

(defn p-row-index-in-range [index-begin index-end]
  (fn [{:keys [index]}]
    (and (>= index index-begin)
         (<= index index-end))))

(defn get-forward-window
  "Takes a look-forward window out of a dataframe.
  Returns nil if full window not possible
  Start is after the event"
  [df-study idx forward-size]
  ;(assert (and (df? df) (int? Idx) (int? forward-size))
  (info "get-forward-window " idx forward-size)
  (let [index-end (+ idx forward-size)]
    (when (< index-end (tablecloth/row-count df-study))
      (tablecloth/select-rows df-study (p-row-index-in-range (inc idx) index-end))
  ;          (select-cols df idx (+ idx forward-size))))
      )))

(defn calc-forward-window-stats
  [ds-study idx forward-size] ;label
  ;(assert (df-has-cols df #{:close :high :low :chan-up :chan-down})
  (when-let [forward-window (get-forward-window ds-study idx forward-size)] ; at end of series window might be nil.
    (let [event-row (tablecloth/first (tablecloth/select-rows  ds-study (dec idx)))
           ;{:keys [close bb-upper bb-lower]} 
          close (first (:close event-row))
          bb-upper (first (:bb-upper event-row))
          bb-lower (first (:bb-lower event-row))
          above (first (:above event-row))
          below (first (:below event-row))
          ; calculated
          event-type (cond
                       above :bb-up
                       below :bb-down
                       :else :bb-unknown)
          bb-range (- bb-upper bb-lower)
          forward-high (apply fun/max (:high forward-window))
          forward-low (apply fun/min (:low forward-window))
          max-forward-up (- forward-high close)
          max-forward-down (- close forward-low)]
      (info "event range:"  bb-range event-row)
      {:idx idx
       :bb-range bb-range
       :close close
       :forward-high forward-high
       :forward-low forward-low
       :max-forward-up   max-forward-up
       :max-forward-down max-forward-down
       :forward-skew (-  max-forward-up max-forward-down)
       :bb-event-type event-type})))


(defn backtest-bollinger [symbol frequency options]
  (let [ds-study (run-study symbol frequency  study-bollinger options)
        ds-events (study-bollinger-filter-events ds-study options)
        calc-event (fn [{:keys [index] :as row}]
                     (calc-forward-window-stats ds-study index (:forward-size options)))]
    (as-> (map calc-event (tds/mapseq-reader ds-events)) v
      (remove nil? v)
      (tablecloth/dataset v)
      (tablecloth/select-columns v [:idx
                                    :close
                                    :bb-event-type
                                    :forward-skew]))))


(defn backtest-grouper [ds-backtest]
  (-> ds-backtest
      (tablecloth/group-by :bb-event-type)
      (tablecloth/aggregate {:min (fn [ds]
                                    (->> ds
                                         :forward-skew
                                         (apply min)))
                             :max (fn [ds]
                                    (->> ds
                                         :forward-skew
                                         (apply max)))

                             :avg (fn [ds]
                                    (->> ds
                                         :forward-skew
                                         fun/mean))

                             :count (fn [ds]
                                      (tablecloth/row-count ds))})))


(defn bollinger-goodness
  [df-result ]
   (let [row-bb-up (tablecloth/select-rows df-result  (fn [r] (= (:$group-name r) :bb-up)))
         breakout-up-result (first (:avg row-bb-up))
         row-bb-down (tablecloth/select-rows df-result  (fn [r] (= (:$group-name r) :bb-down)))
         breakout-down-result (first (:avg row-bb-down))
         ]
     (info "up:" breakout-up-result "down: " breakout-down-result)
     {:goodness (- breakout-up-result breakout-down-result)}
  ))

(defn event-stats [dst-event options]
  (merge options
         {:evt-count (tablecloth/row-count dst-event)
         ;:shape (tablecloth/shape ds-event)
          }))

(defn goodness-event-count [ds]
  (tablecloth/row-count ds)
  ;(tablecloth/shape ds)
  )

(defn pipeline-bollinger-goodness 
  [symbol frequency options]
  (-> (backtest-bollinger symbol frequency options)
      (backtest-grouper)
      bollinger-goodness
      (merge options)
   ))


(comment

  ; calculate study only
  (run-study "ETHUSD" "D"
             study-bollinger
             {:sma-length 20
              :stddev-length 20
              :mult-up 1.5
              :mult-down 1.5
              :forward-size 20})

  ; backtest
   (pipeline-bollinger-goodness "ETHUSD" "D"  {:sma-length 20
                                      :stddev-length 20
                                      :mult-up 1.5
                                      :mult-down 1.5
                                      :forward-size 20})

   )
  ;
  )







; *** REPL EXPERIMENTS ********************************************************

(comment

   ; test our indicators
  (ind/sma 2 [1 1 2 2 3 3 4 4 5 5])

    ; test calculating simple indicators
  (let [ds (wh/load-ts w (make-filename "D" "ETHUSD"))
        bars (ta4j/ds->ta4j-ohlcv2 ds)
        close (ta4j/ds->ta4j-close ds)]
    (-> (ta4j/ind :ATR bars 14) (ta4j/ind-values))
    (-> (ta4j/ind :SMA close 14) (ta4j/ind-values)))

  ; calculate bollinger strategy
  (let [ds (wh/load-ts w (make-filename "D" "ETHUSD"))]
    (study-bollinger ds {:sma-length 20
                         :stddev-length 20
                         :mult-up 1.5
                         :mult-down 1.5}))

; study interface
  (run-study "ETHUSD" "D" study-bollinger-indicator {:sma-length 20
                                                     :stddev-length 20
                                                     :mult-up 1.5
                                                     :mult-down 1.5} "bollinger")

  (defn calc-above [ds]
    (map is-above (tds/mapseq-reader ds)))

  (defn add-calc-above [ds]
    (tablecloth/add-column  ds
                            :above (map is-above (tds/mapseq-reader ds))))

  (->> (load-study  "ETHUSD" "D" "bollinger")
      ;calc-above
       add-calc-above2)

  (-> (run-study "ETHUSD" "D" study-bollinger {:sma-length 20
                                               :stddev-length 20
                                               :mult-up 1.5
                                               :mult-down 1.5} "bollinger-upcross")
      (tablecloth/select-rows is-above-or-below)
      (tablecloth/select-columns [:date :close
                                  :bb-lower :bb-upper
                                  :above :below
                                  :above-count :below-count])
      (helper/pprint-all)
      ;(helper/pprint-dataset)
      )

  (into [] xf-trailing-true-counter
        [true false false true true true false false])

  (defn bad-add [a b]
    (info "bad add params:" a b)
    (+ a b 1000))

  (defn bad-add2 [[a b]]
    ;(info "bad add params:" a b)
    (+ a b 1000))

  (defn bad-add-vec [a b]
    (info "bad add-vec params:" a b)
    (map bad-add a b))

  (->> (load-study  "ETHUSD" "D" "bollinger")
       ((juxt :close :bb-upper))
       (apply bad-add-vec)
       ;(apply fun/>)
       )

  (-> (load-study  "ETHUSD" "D" "bollinger")
      (tablecloth/map-columns :test [:close :bb-upper] bad-add)
       ;(apply bad-add-vec)
       ;(apply fun/>)
      )

  ((juxt + -) 1 2 3)

  (->> (load-study  "ETHUSD" "D" "bollinger")

       (apply bad-add-vec)
       ;(apply fun/>)
       )

  ; Forward-Window Selector - TODO: make this a unit test
  ((p-row-index-in-range 100 200) {:index 150})
  ((p-row-index-in-range 100 200) {:index 100})
  ((p-row-index-in-range 100 200) {:index 200})
  ((p-row-index-in-range 100 200) {:index 50})
  ((p-row-index-in-range 100 200) {:index 250})

  (-> (run-study "ETHUSD" "D" study-bollinger {:sma-length 20
                                               :stddev-length 20
                                               :mult-up 1.5
                                               :mult-down 1.5})

         ;24 | 2019-02-17T00:00:00Z
     ;(get-forward-window 24 5) ; idx 24 , forward-size 24
      (calc-forward-window-stats 24 5)
      ;    (helper/pprint-all)
      )

;  
  )

