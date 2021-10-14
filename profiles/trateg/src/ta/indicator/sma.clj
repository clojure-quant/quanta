(ns ta.indicator.sma
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [tick.alpha.api :as tick]
   [tech.v3.dataset :as tds]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype.functional :as fun]
   [tech.v3.datatype :as dtype]
   [tech.v3.dataset.print :refer [print-range]]
   [tablecloth.api :as tablecloth]
   [fastmath.core :as math]
   [fastmath.stats :as stats]
   [ta.series.ta4j :as ta4j]
   [ta.dataset.backtest :as backtest]
   [ta.warehouse :as wh]
   [ta.data.date :as dt]))

(defn add-sma-indicator [ds {:keys [sma-length-st sma-length-lt] :as options}]
  ""
  (let [; input data needed for ta4j indicators
        bars (ta4j/ds->ta4j-ohlcv ds)
        close (ta4j/ds->ta4j-close ds)
        ; setup the ta4j indicators
        sma-st (ta4j/ind :SMA close sma-length-st)
        sma-st-values  (ta4j/ind-values sma-st)
        sma-lt (ta4j/ind :SMA close sma-length-lt)
        sma-lt-values  (ta4j/ind-values sma-lt)]
    (-> ds
        (tablecloth/add-column :sma-st sma-st-values)
        (tablecloth/add-column :sma-lt sma-lt-values))))

(comment)