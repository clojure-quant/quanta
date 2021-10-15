(ns ta.indicator.bollinger
  (:require
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tablecloth]
   [ta.series.ta4j :as ta4j]
   [ta.trade.backtest :as backtest]))

(defn add-bollinger-indicator
  "adds bollinger indicator to dataset
   * Middle Band = 20-day simple moving average (SMA)
   * Upper Band = 20-day SMA + (20-day standard deviation of price x 2) 
   * Lower Band = 20-day SMA - (20-day standard deviation of price x 2)"
  [ds {:keys [sma-length stddev-length mult-up mult-down] #_:as #_options}]
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
        (tablecloth/add-column :bb-lower bb-lower-values)
        (tablecloth/add-column :bb-upper bb-upper-values))))

(defn calc-is-above [{:keys [close bb-upper] #_:as #_row}]
  (> close bb-upper))

(defn calc-is-below [{:keys [close bb-lower] #_:as #_row}]
  (< close bb-lower))

(defn add-above-below [ds]
  (tablecloth/add-columns
   ds
   {:above (map calc-is-above (tds/mapseq-reader ds))
    :below (map calc-is-below (tds/mapseq-reader ds))}))

(defn is-above-or-below [row]
  (or (:above row) (:below row)))

(defn add-trailing-count [ds]
  (tablecloth/add-columns
   ds
   {:above-count (backtest/calc-trailing-true-counter ds :above)
    :below-count (backtest/calc-trailing-true-counter ds :below)}))

(defn filter-count-1 [ds]
  (tablecloth/select-rows
   ds
   (fn [{:keys [above-count below-count]}]
     (or (= 1 above-count) (= 1 below-count)))))

(defn add-bollinger-with-signal [ds options]
  (let [ds-study (add-bollinger-indicator ds options)]
    (-> ds-study
        backtest/add-running-index
        add-above-below
        add-trailing-count)))

(defn filter-bollinger-events [ds-study options]
  (-> ds-study
      (backtest/drop-beginning (:sma-length options))
      (tablecloth/select-rows is-above-or-below)
      filter-count-1))