(ns ta.indicator.sma
  (:require
   [tablecloth.api :as tablecloth]
   [ta.series.ta4j :as ta4j]))

(defn add-sma-indicator
  ""
  [ds {:keys [sma-length-st sma-length-lt] #_:as #_options}]
  (let [; input data needed for ta4j indicators
        ;bars (ta4j/ds->ta4j-ohlcv ds)
        close (ta4j/ds->ta4j-close ds)
        ; setup the ta4j indicators
        sma-st (ta4j/ind :SMA close sma-length-st)
        sma-st-values  (ta4j/ind-values sma-st)
        sma-lt (ta4j/ind :SMA close sma-length-lt)
        sma-lt-values  (ta4j/ind-values sma-lt)]
    (-> ds
        (tablecloth/add-column :sma-st sma-st-values)
        (tablecloth/add-column :sma-lt sma-lt-values))))

(comment

;  
  )