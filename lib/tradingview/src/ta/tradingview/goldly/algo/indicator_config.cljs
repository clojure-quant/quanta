(ns ta.tradingview.goldly.algo.indicator-config
  (:require
   [ta.tradingview.goldly.indicator.bar-colorer :refer [study-bar-colorer]]
   [ta.tradingview.goldly.indicator.clj :refer [study-clj]]
   [ta.tradingview.goldly.indicator.clj-main :refer [study-clj-main]]
   [ta.tradingview.goldly.indicator.clj-col :refer [study-clj-col]]
   [ta.tradingview.goldly.indicator.clj-char :refer [study-clj-char]]
   [ta.tradingview.goldly.algo.indicator :refer [study-chart-studies]]
   [ta.tradingview.goldly.algo.context :as c]
   ))


(defn custom-indicator-promise [PineJS]
  (println "custom-indicator-promise getter running...")
  (let [s (clj->js [study-bar-colorer
                    ;(js/equitystudy PineJS)
                    (study-clj PineJS)
                    (study-clj-main PineJS)
                    (study-clj-col PineJS)
                    (study-clj-char PineJS)])]
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
