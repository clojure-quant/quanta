(ns ta.tradingview.goldly.feed.udf
  (:require
   [ta.tradingview.goldly.feed.random :refer [get-bars-random]]
   [ta.tradingview.goldly.helper :refer [extract-period]]))

(defn get-bars-random-response [symbolInfo resolution period onHistoryCallback onErrorCallback]
  (let [ok (fn [data]
                                      ;(.log js/console "DATA:")
                                      ;(.log js/console data)
                                      ;(set! (.-ddd js/window) data)
             (onHistoryCallback data))
        err (fn [data]
              (.log js/console "DATA-ERR:")
              (.log js/console data)
              (onErrorCallback data))]
    (get-bars-random symbolInfo resolution period ok err)))

(defn tradingview-udf-feed [udf]
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
                    symbol-info-clj (js->clj symbolInfo :keywordize-keys true)]
                ;(println "GET-BARS" symbol-info-clj resolution period-clj)
                (if (.startsWith (.-name symbolInfo) "#")
                  (get-bars-random-response symbolInfo resolution period onHistoryCallback onErrorCallback)
                  (.getBars udf symbolInfo resolution period onHistoryCallback onErrorCallback))))

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

(def datafeed-urls
  {:demo "https://demo_feed.tradingview.com"
   :ta "/api/tv"})

(def storage-urls
  {:demo "https://saveload.tradingview.com"
   :ta "/api/tv/storage"})

(defn get-tradingview-options-udf-feed [kw]
  (fn []
    (let [storage-url (kw storage-urls)
          feed-url (kw datafeed-urls)
          configured-udf-feed (js/Datafeeds.UDFCompatibleDatafeed. feed-url)]
      {:datafeed configured-udf-feed
       :charts_storage_url storage-url})))

#_(defn get-tradingview-options-udf-feed [kw]
    (fn []
      (let [storage-url (kw storage-urls)
            feed-url (kw datafeed-urls)
            configured-udf-feed (js/Datafeeds.UDFCompatibleDatafeed. feed-url)]
        {:datafeed (tradingview-udf-feed configured-udf-feed)
         :charts_storage_url storage-url})))
