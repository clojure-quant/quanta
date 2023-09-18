(ns ta.viz.view.vega-list
  (:require
   [ui.vega.plot :refer [list-plot]]))

(defn vega-list-plot [data]
  ^:R 
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


(comment 
   
   (vega-list-plot [1 2 3 2 1 2 3 4 5 3 2 1 2 5 3])
 
 ;
 )

