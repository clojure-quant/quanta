(ns ta.viz.view.result
  (:require
   [reagent.core :as r]))





(defn result-view [subscription-id]
  [:div 
     "algo result for subscription: " (str subscription-id)
   ])