(ns notebook.strategy.sma-crossover.viz
  (:require
   [tablecloth.api :as tc]
   [ta.viz.ds.highchart :refer [highstock-render-spec]]))

(def chart-spec
  {:topic :juan-daily-chart
   :chart {:box :fl}
   :charts [{:close :candlestick ; :ohlc ; :line 
             :sma-lt :line
             :sma-st :line}
            {:volume :column}]})

(defn calc-viz-sma [bar-ds]
  (highstock-render-spec nil chart-spec bar-ds))
