(ns notebook.studies.bollinger
  (:require
   [taoensso.timbre :refer [info]]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tc]
   [ta.helper.print :as helper]
   [ta.warehouse :as wh]
   [ta.helper.window :refer [get-forward-window]]
   [demo.algo.bollinger :as bollinger]))

; forward statistics

(defn calc-forward-window-stats
  [ds-study idx forward-size] ;label
  ;(assert (df-has-cols df #{:close :high :low :chan-up :chan-down})
  (when-let [forward-window (get-forward-window ds-study idx forward-size)] ; at end of series window might be nil.
    (let [event-row (tc/first (tc/select-rows  ds-study (dec idx)))
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
          forward-skew (- max-forward-up max-forward-down)]
      (info "event range:"  bb-range event-row)
      {:idx idx
       :date date
       :bb-range bb-range
       :close close
       :forward-high forward-high
       :forward-low forward-low
       :max-forward-up   max-forward-up
       :max-forward-down max-forward-down
       :max-forward-up-prct   max-forward-up-prct
       :max-forward-down-prct max-forward-down-prct
       :forward-skew forward-skew
       :forward-skew-prct (* 100 (/ forward-skew close))
       :bb-event-type event-type})))

(defn calc-forward-window-all-events [ds-study ds-events options]
  (let [calc-event (fn [{:keys [index] #_:as #_row}]
                     (calc-forward-window-stats ds-study index (:forward-size options)))]
    (as-> (map calc-event (tds/mapseq-reader ds-events)) v
      (remove nil? v)
      (tc/dataset v))))

(defn backtest-grouper [ds-backtest]
  (-> ds-backtest
      (tc/group-by :bb-event-type)
      (tc/aggregate {:min (fn [ds]
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
                              (tc/row-count ds))})))

(defn bollinger-backtest-stats
  [df-result]
  (let [row-bb-up (tc/select-rows df-result  (fn [r] (= (:$group-name r) :bb-up)))
        breakout-up-count (first (:count row-bb-up))
        breakout-up-result (first (:avg row-bb-up))
        row-bb-down (tc/select-rows df-result  (fn [r] (= (:$group-name r) :bb-down)))
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

(comment

  ; calculate bollinger strategy
  (let [ds (wh/load-symbol  :crypto  "D" "ETHUSD")]
    (bollinger-study ds {:sma-length 20
                         :stddev-length 20
                         :mult-up 1.5
                         :mult-down 1.5})
    (calc-forward-window-stats ds 24 5))

;  
  )
; Dataset Printing

(defn print-ds-cols-all  [ds1 cols]
  (let [ds2 (if cols
              (tc/select-columns ds1 cols)
              ds1)
        ds3 (helper/print-all ds2)]
    (println ds3)))

(defn- ds-print-cols-all [r k cols]
  (let [ds1 (get r k)
        ds2 (if cols
              (tc/select-columns ds1 cols)
              ds1)
        ds3 (helper/print-all ds2)]
    (println ds3)))

(defn- ds-print-cols-overview [r k cols]
  (let [ds1 (get r k)
        ds2 (if cols
              (tc/select-columns ds1 cols)
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


