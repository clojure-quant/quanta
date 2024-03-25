(ns ta.viz.ds.highchart
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.viz.chart-spec :refer [chart-pane-spec? chart-cols]]
   [ta.viz.ds.highchart.data :refer [convert-data]]
   [ta.viz.ds.highchart.spec :refer [highchart-spec]]))

(defn highstock-render-spec
  "returns a render specification {:render-fn :spec :data}. 
   spec must follow chart-pane format.
   The ui shows a barchart with extra specified columns 
   plotted with a specified style/position, 
   created from the bar-algo-ds"
  [spec bar-algo-ds]
  (let [chart-spec (or (:chart spec) {})
        pane-spec (:charts spec)]
    (assert (chart-pane-spec? pane-spec) "please comply with chart-pane-spec")
    {:render-fn 'ta.viz.renderfn.highcharts/highstock
     :data (-> bar-algo-ds
               (tc/select-columns (chart-cols pane-spec))
               (convert-data pane-spec))
     :spec (highchart-spec chart-spec pane-spec)}))

(comment

  (def ds
    (tc/dataset [{:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5}
                 {:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5}
                 {:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5}
                 {:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5}]))

  ds

  (def spec {:charts  [{:open "line"
                        :low "line"
                        ;:close :flags
                        }
                       {:volume "column"}]})

  (-> spec :charts chart-cols)

  (highstock-render-spec spec ds)

; 
  )