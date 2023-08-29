(ns ta.tradingview.goldly.algo.indicator-config
  (:require
   [ta.tradingview.goldly.indicator.bar-colorer :refer [study-bar-colorer]]
   [ta.tradingview.goldly.indicator.clj :refer [study-clj]]
   [ta.tradingview.goldly.indicator.clj-main :refer [study-clj-main]]
   [ta.tradingview.goldly.indicator.clj-col :refer [study-clj-col]]
   [ta.tradingview.goldly.indicator.clj-char :refer [study-clj-char]]
   [ta.tradingview.goldly.algo.indicator :refer [study-chart-studies]]
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

(defn study-custom-indicator-promise [algo-context PineJS]
  (println "custom-indicator-promise getter running...")
  (let [algo-indicators (study-chart-studies algo-context PineJS)
        indicators (concat [study-bar-colorer] algo-indicators)
        indicators-vec (into [] indicators)
        vec-cljs (clj->js indicators-vec)]
    (set! (.-pine js/window) PineJS)
    ;(.log js/console s)
    (.resolve js/Promise vec-cljs)))
