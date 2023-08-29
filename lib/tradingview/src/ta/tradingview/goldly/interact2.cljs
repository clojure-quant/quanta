(ns ta.tradingview.goldly.interact2
  (:require
   [reagent.core :as r]))



(defn on-data-loaded [& _args]
  (println "tv data has been loaded (called after set-symbol)"))

(defn set-symbol
  ([tv symbol interval]
      ;(println "tv set symbol:" symbol "interval: " interval)
   (set-symbol tv symbol interval on-data-loaded))
  ([tv symbol interval on-load-finished]
    (println "tv set symbol:" symbol "interval: " interval)
    (.setSymbol tv symbol interval on-load-finished)
    nil))