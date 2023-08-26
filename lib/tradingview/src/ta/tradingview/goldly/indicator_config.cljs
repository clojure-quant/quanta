(ns ta.tradingview.goldly.indicator-config
  (:require
   [ta.tradingview.goldly.indicator.bar-colorer :refer [study-bar-colorer]]
   [ta.tradingview.goldly.indicator.clj :refer [study-clj]]
   [ta.tradingview.goldly.indicator.clj-main :refer [study-clj-main]]
   [ta.tradingview.goldly.indicator.clj-col :refer [study-clj-col]]
   [ta.tradingview.goldly.indicator.clj-char :refer [study-clj-char]]
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

