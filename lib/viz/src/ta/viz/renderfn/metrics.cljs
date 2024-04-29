(ns ta.viz.renderfn.metrics
  (:require
   [ta.viz.renderfn.rtable :refer [rtable]]
   [ta.viz.trade.metrics :refer [metrics-view]]
   [ta.viz.renderfn.vega :refer [vega-lite]]))

(defn metrics [spec {:keys [roundtrips metrics nav nav-chart rt] :as data}]
  (with-meta
    [:div.grid.grid-cols-2.w-full.h-full
     [:div.bg-green-300.w-full.h-full
      [:h1.bg-red-500 "metrics"]
      [metrics-view metrics]
      [:h1.bg-red-500 "chart"]
      [vega-lite (:spec nav-chart) (:data nav-chart)]]
     [:div.bg-blue-300.w-full.h-full
      [rtable (:spec rt) (:data rt)]]]
;[:p (pr-str spec)]
     ;[:p (pr-str data)]

    {:R true}))
