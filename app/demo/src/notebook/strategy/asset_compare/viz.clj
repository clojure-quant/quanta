(ns notebook.strategy.asset-compare.viz
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [tablecloth.api :as tc]
   [ta.viz.ds.vega :refer [convert-data]]))

(def w 1600)

(def spec
  {:box :fl
   :width w ;"100%"
   :height "600" ;"100%"
   :description "Multiple Assets (Closing Price) over Time."
   :mark "line"
   :encoding  {:y {:field "close", :type "quantitative"}
               :color {:field "asset", :type "nominal"}
               :x {:type "temporal"
                   :field "date"
                   :axis {:tickCount 8
                          :labelAlign "left"}}}})


(defn calc-viz-vega [bar-algo-ds]
  (when bar-algo-ds
    (warn "calculating sentiment-spread viz for: " (tc/row-count bar-algo-ds))
    {:render-fn 'ta.viz.renderfn.vega/vega-lite
     :data {:values (convert-data bar-algo-ds [:date :close :asset])}
     :spec spec}))

