(ns ta.viz.ds.highchart
  (:require
   [tick.core :as t]
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.viz.ds.highchart.chart-spec :refer [chart-pane-spec? chart-cols]]
   [ta.viz.ds.highchart.data :refer [convert-data]]
   [ta.viz.ds.highchart.highchart-spec :refer [highchart-spec]]))


(defn highstock-render-spec
  "returns a render specification {:render-fn :spec :data}. 
   spec must follow chart-pane format.
   The ui shows a barchart with extra specified columns 
   plotted with a specified style/position, 
   created from the bar-algo-ds"
  [env spec bar-algo-ds]
  (let [chart-spec (:charts spec)] ; search for :axes-spec
    (assert (chart-pane-spec? chart-spec) "please comply with chart-pane-spec")
    {:render-fn 'ta.viz.renderfn.highcharts/highstock
     :data (-> bar-algo-ds
               (tc/select-columns (chart-cols chart-spec))
               (convert-data chart-spec))
     :spec (highchart-spec chart-spec)}))

(defn add-data-to-spec 
  "render-spec contains :spec and :data separately. 
   this function merges both. This really should be done in cljs, 
   but it is handy to have it here for testing."
  [render-spec]
  (let [{:keys [data spec]} render-spec
        series (:series spec)
        series (map (fn [series d]
                      (assoc series :data d)) series data)]
    (assoc spec :series series)))

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

  (highstock-render-spec nil spec ds)

  (-> (highstock-render-spec nil spec ds)
      add-data-to-spec)


; 
  )