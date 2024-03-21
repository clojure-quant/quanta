(ns ta.tradingview.goldly.algo.indicator-config
  (:require
   [promesa.core :as p]
   [goldly.service.core :refer [clj]]
   [ta.tradingview.goldly.indicator.bar-colorer :refer [study-bar-colorer]]
   [ta.tradingview.goldly.algo.indicator :refer [study-chart-studies all-algo->studies]]
   [ta.tradingview.goldly.algo.context :as c]))

(defn custom-indicator-promise [PineJS]
  (println "custom-indicator-promise getter running...")
  (let [s (clj->js [study-bar-colorer
                    ;(js/equitystudy PineJS)
                    ])]
    (set! (.-pine js/window) PineJS)
    (.log js/console s)
    (.resolve js/Promise s)))

(defn study-custom-indicator-promise [algo-ctx]
  (fn [PineJS]
    (println "algo-custom-indicator-promise getter running... algo: " (c/get-algo-name algo-ctx))
    (let [algo-indicators (study-chart-studies algo-ctx PineJS)
          indicators (concat [study-bar-colorer] algo-indicators)
          indicators-vec (into [] indicators)
          names (map :name indicators-vec)
          vec-cljs (clj->js indicators-vec)]
      (println "custom indicators: " names)
      (set! (.-pine js/window) PineJS)
      ;(.log js/console s)
      (.resolve js/Promise vec-cljs))))

(defn algo-all-custom-indicator-promise [algo-ctx]
  (fn [PineJS]
    (println "algo-all-custom-indicator-promise getter running..")
    (let [rp (clj 'ta.algo.manager/tradingview-algo-chart-specs)]
      (p/catch rp (fn [err]
                    (println "ERROR GETTING ALGO CHART SPECS: " err)))
      (p/then rp (fn [chart-specs]
                   (println "TV algo chat specs " chart-specs)
                   (let [algo-indicators  (all-algo->studies algo-ctx PineJS chart-specs)
                         indicators (concat [study-bar-colorer] algo-indicators)
                         indicators-vec (into [] indicators)
                         names (map :name indicators-vec)
                         vec-js (clj->js indicators-vec)]
                     (println "custom indicators: " names)
                     (set! (.-algos js/window) vec-js)
                     (set! (.-pine js/window) PineJS)
                     vec-js))))))

