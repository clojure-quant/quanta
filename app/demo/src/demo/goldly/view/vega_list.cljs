(ns demo.goldly.view.vega-list
  (:require
   [ui.vega.plot :refer [list-plot]]))

(defn vega-list-plot [data]
  [:div
   [:h1 "vega-list-plot"]
   [:div.flex.flex-row.content-between
    [:div.flex.flex-col.justify-start
     (list-plot {:data data
                 :joined true
                 :plot-size 400
                 :color "red"
                 :aspect-ratio 1.6
                 :plot-range [:all :all]
                 :opacity 0.5})]]])

