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

(def vega-spec 
  {:cols [:date :close :asset]
   :spec spec
    })

