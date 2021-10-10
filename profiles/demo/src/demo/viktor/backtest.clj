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

(defn study-bollinger [ds {:keys [sma-length stddev-length mult-up mult-down] :as options}]
  (let [ds-study (study-bollinger-indicator ds options)]
    (-> ds-study
        add-above-below
        (tablecloth/select-rows is-above-or-below)
        (drop-beginning options)
        add-trailing-count
        filter-count-1)))

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

(defn make-window
  "Takes a look-forward window out of a dataframe.
  Returns nil if full window not possible
  Start is after the event"
  [df idx forward-size]
  ;(assert (and (df? df) (int? Idx) (int? forward-size))
  ;        (when (< (size df) (+ idx forward-size))
  ;          (select-cols df idx (+ idx forward-size))))
  )

(defn calc-channel-turningpoint-event
  [df idx dir-label forward-size]
  ;(assert (df-has-cols df #{:close :high :low :chan-up :chan-down})
        ;  (when-let [w (window df idx forward-size)] ; at end of series window might be nil. 
        ;    (let [;[p band-up band-down]  (cols df idx [:close :chan-up :chan-down])
        ;          r (- band-up band-down)
        ;          h (high (:high w))
        ;          l (low (:low w))]))
  )

; (let [df  :close

(defn events-goodness
  [df bollinger-events]
  "input: sequence of bollinger events
   output: value that can be put to the optimizer (average difference of range)")

(defn event-stats [dst-event options]
  (merge options
         {:evt-count (tablecloth/row-count dst-event)
         ;:shape (tablecloth/shape ds-event)
          }))
(defn goodness-event-count [ds]
  (tablecloth/row-count ds)
  ;(tablecloth/shape ds)
  )
(strategy/run-study
 "ETHUSD" "D"
 strategy/study-bollinger
 options
 "bollinger-upcross")

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

;  
  )

