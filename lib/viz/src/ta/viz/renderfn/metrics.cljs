(ns ta.viz.renderfn.metrics
  (:require 
   [ta.viz.trade-metrics.roundtrip-table :refer [roundtrip-table]]
   ))

(defn metrics [spec {:keys [roundtrips] :as data}]
  (with-meta
    [:div {:class "bg-blue-500"}
     [:h1.bg-red-500 "trade metrics"]
     ;[:p (pr-str spec)]
     ;[:p (pr-str data)]
     [roundtrip-table roundtrips]

     ]
    {:R true}))
