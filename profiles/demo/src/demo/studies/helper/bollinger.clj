(ns demo.studies.helper.bollinger
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
   [ta.warehouse :as wh]
   [ta.indicator.bollinger :as bollinger]
   [ta.trade.backtest :as backtest]
   [demo.env.config :refer [w-crypto]]
   ;[demo.studies.helper.experiments-helpers :as experiments-helpers]
   ))

(defn make-filename [frequency symbol]
  (str symbol "-" frequency))

; forward statistics

(defn calc-forward-window-stats
  [ds-study idx forward-size] ;label
  ;(assert (df-has-cols df #{:close :high :low :chan-up :chan-down})
  (when-let [forward-window (backtest/get-forward-window ds-study idx forward-size)] ; at end of series window might be nil.
    (let [event-row (tablecloth/first (tablecloth/select-rows  ds-study (dec idx)))
          {:keys [date close bb-upper bb-lower above below]} (tds/row-at ds-study (dec idx))
          ;close (first (:close event-row))
          ;date (first (:date event-row))
          ;bb-upper (first (:bb-upper event-row))
          ;bb-lower (first (:bb-lower event-row))
          ;above (first (:above event-row))
          ;below (first (:below event-row))
          ; calculated
          event-type (cond
                       above :bb-up
                       below :bb-down
                       :else :bb-unknown)
          bb-range (- bb-upper bb-lower)
          forward-high (apply fun/max (:high forward-window))
          forward-low (apply fun/min (:low forward-window))
          max-forward-up (- forward-high close)
          max-forward-down (- close forward-low)
          max-forward-up-prct (- forward-high close)
          max-forward-down-prct (- close forward-low)
          forward-skew (-  max-forward-up max-forward-down)]
      (info "event range:"  bb-range event-row)
      {:idx idx
       :date date
       :bb-range bb-range
       :close close
       :forward-high forward-high
       :forward-low forward-low
       :max-forward-up   max-forward-up
       :max-forward-down max-forward-down
       :forward-skew forward-skew
       :forward-skew-prct (* 100 (/ forward-skew close))
       :bb-event-type event-type})))

(defn calc-forward-window-all-events [ds-study ds-events options]
  (let [calc-event (fn [{:keys [index] :as row}]
                     (calc-forward-window-stats ds-study index (:forward-size options)))]
    (as-> (map calc-event (tds/mapseq-reader ds-events)) v
      (remove nil? v)
      (tablecloth/dataset v))))

(defn backtest-grouper [ds-backtest]
  (-> ds-backtest
      (tablecloth/group-by :bb-event-type)
      (tablecloth/aggregate {:min (fn [ds]
                                    (->> ds
                                         :forward-skew-prct
                                         (apply min)))
                             :max (fn [ds]
                                    (->> ds
                                         :forward-skew-prct
                                         (apply max)))

                             :avg (fn [ds]
                                    (->> ds
                                         :forward-skew-prct
                                         fun/mean))

                             :count (fn [ds]
                                      (tablecloth/row-count ds))})))

(defn bollinger-backtest-stats
  [df-result]
  (let [row-bb-up (tablecloth/select-rows df-result  (fn [r] (= (:$group-name r) :bb-up)))
        breakout-up-count (first (:count row-bb-up))
        breakout-up-result (first (:avg row-bb-up))
        row-bb-down (tablecloth/select-rows df-result  (fn [r] (= (:$group-name r) :bb-down)))
        breakout-down-count (first (:count row-bb-down))
        breakout-down-result (first (:avg row-bb-down))]
    (info "up:" breakout-up-result "down: " breakout-down-result)
    {:up-count breakout-up-count
     :down-count breakout-down-count
     :goodness (- breakout-up-result breakout-down-result)}))

(defn bollinger-study
  [ds-bars options]
  (let [ds-study (bollinger/add-bollinger-with-signal ds-bars options)
        ds-events-all (bollinger/filter-bollinger-events ds-study options)
        ds-events-forward (calc-forward-window-all-events ds-study ds-events-all options)
        ds-performance (backtest-grouper ds-events-forward)
        backtest-numbers (-> ds-performance bollinger-backtest-stats (merge options))]
    {:ds-study ds-study
     :ds-events-all ds-events-all
     :ds-events-forward ds-events-forward
     :ds-performance ds-performance
     :backtest-numbers backtest-numbers}))

; Dataset Printing

(defn print-ds-cols-all  [ds1 cols]
  (let [ds2 (if cols
              (tablecloth/select-columns ds1 cols)
              ds1)
        ds3 (helper/print-all ds2)]
    (println ds3)))

(defn- ds-print-cols-all [r k cols]
  (let [ds1 (get r k)
        ds2 (if cols
              (tablecloth/select-columns ds1 cols)
              ds1)
        ds3 (helper/print-all ds2)]
    (println ds3)))

(defn- ds-print-cols-overview [r k cols]
  (let [ds1 (get r k)
        ds2 (if cols
              (tablecloth/select-columns ds1 cols)
              ds1)]
    (helper/print-overview ds2)))

(def cols
  {:ds-study  nil
   :ds-events-all [:index :date :close
                   :bb-lower :bb-upper
                   ;:above-count :below-count
                   :above :below]
   :ds-events-forward  [:idx
                        :date
                        :close
                        :bb-event-type
                        :forward-skew
                        :forward-skew-prct
                        :max-forward-up
                        :max-forward-down]
   :ds-performance nil})

(defn print-all [r ds-name]
  (ds-print-cols-all r ds-name (ds-name cols)))

(defn print-overview [r ds-name]
  (ds-print-cols-overview r ds-name (ds-name cols)))

(defn print-backtest-numbers [r]
  (info (pr-str (:backtest-numbers r))))

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

  (defn calc-above [ds]
    (map is-above (tds/mapseq-reader ds)))

  (defn add-calc-above [ds]
    (tablecloth/add-column  ds
                            :above (map is-above (tds/mapseq-reader ds))))

  (->> (backtest/load-study w "ETHUSD" "D" "bollinger")
      ;calc-above
       add-calc-above2)

  (-> (backtest/run-study w "ETHUSD" "D" study-bollinger {:sma-length 20
                                                          :stddev-length 20
                                                          :mult-up 1.5
                                                          :mult-down 1.5} "bollinger-upcross")
      (tablecloth/select-rows is-above-or-below)
      (tablecloth/select-columns [:date :close
                                  :bb-lower :bb-upper
                                  :above :below
                                  :above-count :below-count])
      (helper/print-all)
      ;(helper/print-overview)
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

  (->> (backtest/load-study w "ETHUSD" "D" "bollinger")
       ((juxt :close :bb-upper))
       (apply bad-add-vec)
       ;(apply fun/>)
       )

  (-> (backtest/load-study w "ETHUSD" "D" "bollinger")
      (tablecloth/map-columns :test [:close :bb-upper] bad-add)
       ;(apply bad-add-vec)
       ;(apply fun/>)
      )

  ((juxt + -) 1 2 3)

  (->> (backtest/load-study w "ETHUSD" "D" "bollinger")

       (apply bad-add-vec)
       ;(apply fun/>)
       )

  ; Forward-Window Selector - TODO: make this a unit test
  ((p-row-index-in-range 100 200) {:index 150})
  ((p-row-index-in-range 100 200) {:index 100})
  ((p-row-index-in-range 100 200) {:index 200})
  ((p-row-index-in-range 100 200) {:index 50})
  ((p-row-index-in-range 100 200) {:index 250})

  (-> (backtest/run-study w "ETHUSD" "D" study-bollinger {:sma-length 20
                                                          :stddev-length 20
                                                          :mult-up 1.5
                                                          :mult-down 1.5})

         ;24 | 2019-02-17T00:00:00Z
     ;(backtest/get-forward-window 24 5) ; idx 24 , forward-size 24
      (calc-forward-window-stats 24 5)
      ;    (helper/pprint-all)
      )

;  
  )

