(ns notebook.playground.viz.scratchpad)

;; open webbrowser on port 8080, then go to devtools->scratchpad
;; evaling one of the forms below will show the visualization

(tap> ^{:render-fn 'ui.clock/clock} [])
  
(tap>
  ^{:render-fn 'reval.goldly.viz.render-fn/reagent}
    [:p.bg-blue-500 "hello, world!"])


[ta.algo.ds :refer [has-col?]]
(defn- default-algo-chart-spec [ds-algo]
  (if (has-col? ds-algo :trade)
    [{:trade "flags"}
     {:volume "column"}]
    [{:volume "column"}]))


 [ta.viz.study-highchart :refer [study-highchart] :as hc]

(defn highchart [{:keys [ds-algo algo-charts]}]
  (let [axes-spec (if algo-charts
                    algo-charts
                    (default-algo-chart-spec ds-algo))]
    (println "highchart axes spec:" axes-spec)
    (-> (study-highchart ds-algo nil); axes-spec)
        second)))

(defn highchart [ds-study axes-spec]
  (let [axes-spec (if axes-spec
                    axes-spec
                    (if (has-col? ds-study :trade)
                      [{:trade "flags"}
                       {:volume "column"}]
                      [{:volume "column"}]))]
    (println "highchart axes spec:" axes-spec)
    (-> (study-highchart ds-study axes-spec)
        second)))


(defn tap-highchart [data]
  (let [data (highchart data)
        ;data (assoc data :chart {:height 600 :width 1200})
        ]
    (tap> ^{:render-fn 'ui.highcharts/highstock}
     {:data data
      :box :lg})))

   ; (require '[ta.viz.study-highchart :refer [ds-epoch series-flags]])
  #_(-> (algo-backtest "buy-hold s&p")
      ;keys
        :backtest
        :ds-study
        hc/ds-epoch
        (hc/series-flags :trade))