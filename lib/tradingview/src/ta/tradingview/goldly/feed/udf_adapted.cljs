(ns ta.tradingview.goldly.feed.udf-adapted
  (:require
      [ta.tradingview.goldly.feed.random :refer [get-bars-random]]
      [ta.tradingview.goldly.helper :refer [extract-period]]
   ))



(defn tradingview-study-adapter [udf _study-data]
  {:onReady (fn [cb]
              ;(println "on-ready")
              (.onReady udf cb))
   :searchSymbols (fn [userInput exchange symbolType onResultReadyCallback]
                    ;(println "search symbol " userInput exchange symbolType)
                    (.searchSymbols udf userInput exchange symbolType onResultReadyCallback))
   :resolveSymbol (fn [symbolName onSymbolResolvedCallback onResolveErrorCallback]
                    ;(println "resolve symbol" symbolName)
                    (.resolveSymbol udf symbolName onSymbolResolvedCallback onResolveErrorCallback))

   :getBars (fn [symbolInfo resolution period onHistoryCallback onErrorCallback]
              (let [period-clj (extract-period period)
                    symbol-info-clj (js->clj symbolInfo :keywordize-keys true)
                    ok (fn [data]
                              ;(.log js/console "DATA:")
                              ;(.log js/console data)
                              ;(set! (.-ddd js/window) data)
                         (onHistoryCallback data))
                    err (fn [data]
                          (.log js/console "DATA-ERR:")
                          (.log js/console data)
                          (onErrorCallback data))]
                ;(println "GET-BARS" symbol-info-clj resolution period-clj)
                (if (.startsWith (.-name symbolInfo) "#")
                  (get-bars-random symbolInfo resolution period ok err)
                  (.getBars udf symbolInfo resolution period ok err))))

   :subscribeBars (fn [symbolInfo resolution onRealtimeCallback subscribeUID onResetCacheNeededCallback]
                    ;(println "subscribe: " symbolInfo resolution subscribeUID)
                    (.subscribeBars udf symbolInfo resolution onRealtimeCallback subscribeUID onResetCacheNeededCallback))
   :unsubscribeBars (fn [subscriberUID]
                      ;(println "unsubscribe: " subscriberUID)
                      (.unsubscribeBars udf subscriberUID))
   :getServerTime  (fn [cb]
                     ;(println "get-server-time")
                     (.getServerTime udf cb))
   :calculateHistoryDepth (fn [resolution resolutionBack intervalBack]
                            ;(println "calculate history depth:" resolution resolutionBack intervalBack)
                            (.calculateHistoryDepth udf resolution resolutionBack intervalBack))
   :getMarks (fn [symbolInfo startDate endDate onDataCallback resolution]
               ;(println "getMarks" symbolInfo startDate endDate resolution)
               (.getMarks udf symbolInfo startDate endDate onDataCallback resolution))
   :getTimeScaleMarks (fn [symbolInfo startDate endDate onDataCallback resolution]
                        ;(println "get-timescale-marks" symbolInfo startDate endDate resolution)
                        (.getMarks udf symbolInfo startDate endDate onDataCallback resolution))})



