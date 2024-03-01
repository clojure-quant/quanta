(ns ta.viz.notebook.scratchpad)

;; open webbrowser on port 8080, then go to devtools->scratchpad
;; evaling one of the forms below will show the visualization

(tap> ^{:render-fn 'ui.clock/clock} [])

(tap>
 ^{:render-fn 'reval.goldly.viz.render-fn/reagent}
 [:p.bg-blue-500 "hello, world!"])

(tap>
 ^{:render-fn 'ta.viz.render-spec}
 {:render-fn 'ta.viz.ui.demo/demo
  :spec {:some :specification}
  :data "peace to the world"})

(tap>
 ^{:render-fn 'ta.viz.ui.render-spec}
 {:render-fn 'ta.viz.ui.rtable/rtable
  :spec {:class "table-head-fixed padding-sm table-red table-striped table-hover"
         :style {:width "50vw"
                 :height "40vh"
                 :border "3px solid green"}
         :columnss [{:path :close}
                    {:path :sma}]}
  :data [{:close 10 :sma 10}
         {:close 14 :sma 12}
         {:close 9 :sma 11}
         {:close 16 :sma 13}
         {:close 11 :sma 12}
         {:close 10 :sma 11}]})

[ta.algo.ds :refer [has-col?]]
(defn- default-algo-chart-spec [ds-algo]
  (if (has-col? ds-algo :trade)
    [{:trade "flags"}
     {:volume "column"}]
    [{:volume "column"}]))

(if algo-charts
  algo-charts
  (default-algo-chart-spec ds-algo))

(if axes-spec
  axes-spec
  (if (has-col? ds-study :trade)
    [{:trade "flags"}
     {:volume "column"}]
    [{:volume "column"}]))

#_(-> (algo-backtest "buy-hold s&p")
      ;keys
      :backtest
      :ds-study
      hc/ds-epoch
      (hc/series-flags :trade))