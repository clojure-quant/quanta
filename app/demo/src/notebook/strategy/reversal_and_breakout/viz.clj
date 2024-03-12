(ns notebook.strategy.reversal-and-breakout.viz
  (:require [ta.viz.ds.highchart :refer [highstock-render-spec]]))

(def chart-spec
  {:topic :rb-strategy
   :chart {:box :fl}
   :charts [{:close :candlestick
             :sh {:type :line :color "green"}
             :sl {:type :line :color "red"}
             :h {:type :line :color "blue"}
             :l {:type :line :color "yellow"}}
            ;{:volume :column}
            ]})

(defn calc-viz-test [bar-ds]
  (highstock-render-spec nil chart-spec bar-ds))