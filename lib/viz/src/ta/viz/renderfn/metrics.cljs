(ns ta.viz.renderfn.metrics
  (:require
   ;[spaces.core :as spaces]
   ;[spaces.layout.screen :as layout-viewport]
   [spaces.layout.fixed :as layout-fixed]
   [ta.viz.trade.roundtrip-table :refer [roundtrip-table]]
   [ta.viz.trade.metrics :refer [metrics-view]]
   [ta.viz.trade.nav-chart :refer [nav-chart]]))

(defn metrics [spec {:keys [roundtrips metrics nav] :as data}]
  (with-meta
    [:div.grid.grid-cols-2.w-full.h-full
    [:div.bg-green-300.w-full.h-full 
             [:h1.bg-red-500 "metrics"]
             [metrics-view metrics]
             [:h1.bg-red-500 "nav (realized) chart"]
             [nav-chart nav]]
     [:div.bg-blue-300.w-full.h-full 
              [roundtrip-table roundtrips]]
      ]

     ;[:p (pr-str spec)]
     ;[:p (pr-str data)]
     
    {:R true}))
