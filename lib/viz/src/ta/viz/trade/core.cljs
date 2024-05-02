(ns ta.viz.trade.core
  (:require
   [container :refer [tab]]
   [ta.viz.renderfn.rtable :refer [rtable]]
   [ta.viz.trade.metrics :refer [metrics-view]]
   [ta.viz.renderfn.vega :refer [vega-lite]]))

(defn roundtrip-stats-ui [{:keys [style class]
                           :or {style {:height "600px"
                                       :width "800px"}
                                class "bg-red-500"}
                           :as spec}
                          {:keys [metrics chart rt] :as data}]
  (with-meta
    [tab {:class class
          :style style}
     "metrics"
     [metrics-view metrics]
     "chart"
     [vega-lite (:spec chart) (:data chart)]
     "roundtrips"
     [rtable (:spec rt) (:data rt)]]
    {:R true}))
