(ns quanta.notebook.viz.publish.highstock-raw
  (:require
   [ta.viz.publish :as p]))

(defn add-data-to-spec
  "render-spec contains :spec and :data separately. 
   this function merges both. This really should be done in cljs, 
   but it is handy to have it here for testing."
  [render-spec]
  (let [{:keys [data spec]} render-spec
        series (:series spec)
        series (map (fn [series d]
                      (assoc series :data d)) series data)
        series (into [] series)]
    (assoc spec :series series)))

(def spec {:title {:text "Demo - Annotations"}
           :xAxis {:categories ["Jan" "Feb" "Mar"
                                "Apr" "May" "Jun"
                                "Jul" "Aug" "Sep"
                                "Oct" "Nov" "Dec"]}
           :annotations [{:labels [{:point "max"
                                    :text "MAX!"}
                                   {:point "min"
                                    :text "MIN!"
                                    :backgroundColor "white"}]}]
           :series [{}]})

(def data [[{:y 29.9 :id "min"}
            71.5 106.4 129.2 144.0 176.0 135.6 148.5
            {:y 216.4 :id "max"}
            194.1 95.6 54.4]])

(add-data-to-spec {:data data :spec spec})

(def render-spec
  {:render-fn 'ta.viz.renderfn.highcharts/highchart
   :data data
   :spec spec})

(p/publish nil {:topic :highchart-raw} render-spec)