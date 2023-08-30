(ns ta.tradingview.goldly.interact2
  (:require
   [reagent.core :as r]))


; same as interact namespace, but requires tv (tradingview-widget) parameter

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


(defn chart-active [tv]
  (.activeChart tv))


(defn reset-data [tv]
  (let [chart (chart-active tv)]
     (println "reset-data! (of active chart)")
    (.resetData chart)
    nil))