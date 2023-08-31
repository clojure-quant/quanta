(ns demo.algo.bollinger
  (:require
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.series.ta4j :as ta4j]
   [ta.backtest.signal :refer [add-running-index]]
   [ta.helper.window :refer [drop-beginning calc-trailing-true-counter]]
   [ta.algo.manager :refer [add-algo]]
   [ta.tradingview.chart.color :refer [color]]
   [ta.tradingview.chart.plot :refer [plot-type]]
   ))

(defn add-bollinger-indicator
  "adds bollinger indicator to dataset
   * Middle Band = 20-day simple moving average (SMA)
   * Upper Band = 20-day SMA + (20-day standard deviation of price x 2) 
   * Lower Band = 20-day SMA - (20-day standard deviation of price x 2)"
  [ds {:keys [sma-length stddev-length mult-up mult-down] #_:as #_options}]
  ;(println "ADDING BOLLINGER sma length: " sma-length)
  (let [; input data needed for ta4j indicators
        ;bars (ta4j/ds->ta4j-ohlcv ds)
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
        (tc/add-column :bb-lower bb-lower-values)
        (tc/add-column :bb-upper bb-upper-values))))

(defn calc-is-above [{:keys [close bb-upper] #_:as #_row}]
  (> close bb-upper))

(defn calc-is-below [{:keys [close bb-lower] #_:as #_row}]
  (< close bb-lower))

(defn add-above-below [ds]
  (tc/add-columns
   ds
   {:above (map calc-is-above (tds/mapseq-reader ds))
    :below (map calc-is-below (tds/mapseq-reader ds))}))

(defn is-above-or-below [row]
  (or (:above row) (:below row)))

(defn add-trailing-count [ds]
  (tc/add-columns
   ds
   {:above-count (calc-trailing-true-counter ds :above)
    :below-count (calc-trailing-true-counter ds :below)}))

(defn filter-count-1 [ds]
  (tc/select-rows
   ds
   (fn [{:keys [above-count below-count]}]
     (or (= 1 above-count) (= 1 below-count)))))

(defn add-bollinger-with-signal [ds options]
  (let [ds-study (add-bollinger-indicator ds options)]
    (-> ds-study
        add-running-index
        add-above-below
        add-trailing-count)))

(defn filter-bollinger-events [ds-study options]
  (-> ds-study
      (drop-beginning (:sma-length options))
      (tc/select-rows is-above-or-below)
      filter-count-1))

(add-algo
 {:name "bollinger"
  :comment "just the data"
  :algo add-bollinger-with-signal
  :charts [{;:trade "flags"
            :bb-lower {:type "line" 
                       :linewidth 2 
                       :color (color :blue-900)
                       }
            :bb-upper {:type "line" 
                       :linewidth 4
                       :color (color :red)
                       }}
           {:volume {:type "line" 
                     :color (color :gold)
                     :plottype (plot-type :columns)
                     }}] ;
  :options {:symbol "SPY"
            :frequency "D"
            :sma-length 30
            :stddev-length 30
            :mult-up 1.0
            :mult-down 1.0}})


(comment
  (require '[ta.helper.date-ds  :refer [days-ago]])
  (def ds
    (-> {:close [10.0 10.6 10.7]
         :date [(days-ago 1) (days-ago 2) (days-ago 3)]}
        tc/dataset))
  (require '[ta.algo.manager :refer [col-info]])
  (col-info ds)

  (ta4j/ds->ta4j-close ds)

  #_(add-bollinger-indicator {:sma-length 3
                              :stddev-length 3
                              :mult-up 1.0
                              :mult-down 1.0})
  ;
  )