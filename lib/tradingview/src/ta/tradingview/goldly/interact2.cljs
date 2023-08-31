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



;; STUDY / INDICAOR

(defn study-list [tv]
  (let [studies-js (.getStudiesList tv)]
      (.log js/console)
      (js->clj studies-js)))

; JSServer.studyLibrary.push.apply(JSServer.studyLibrary,e)

(defn add-study [tv study-name study-args]
  (let [chart (chart-active tv)
        study-args-js (-> study-args vec clj->js)]
    (println "add study: " study-name " args:" study-args)
    (.createStudy chart study-name false false study-args-js)))

(defn remove-entity [tv entity-id]
  (let [chart (chart-active tv)]
    (println "remove entity-id: " entity-id)
    (.removeEntity chart entity-id)
    nil))

(defn remove-all-studies [tv]
  ;(println "remove-all-studies")
  (let [chart (chart-active tv)]
    (.removeAllStudies chart)
    nil))