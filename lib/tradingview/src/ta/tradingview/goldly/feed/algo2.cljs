(ns ta.tradingview.goldly.feed.algo2
  (:require
   [promesa.core :as p]
   [goldly.service.core :refer [run-cb clj]]
   [ta.tradingview.goldly.helper :refer [extract-period]]
   [ta.tradingview.goldly.algo.context :as c]))

(defn convert-bar [b]
  {:time (* 1000 (:epoch b))
   :open (:open b)
   :high (:high b)
   :low (:low b)
   :close (:close b)
   :volume (:volume b)
   :isBarClosed true
   :isLastBar false})

(defn convert-bars [bars]
  (->> (map convert-bar bars)
       (into [])
       (clj->js)))

(defn get-algo-full [algo-ctx]
  (let [{:keys [algo opts]} (c/get-algo-input algo-ctx)
        _ (println "get-algo-full algo: " algo "symbol: " (:symbol opts))
        rp (clj 'ta.algo.manager/algo-run-browser algo opts)]
    (-> rp
        (p/then (fn [r]
                  (println "get-algo-full received data successfully!")
                  (c/set-algo-data algo-ctx r)
                  nil))
        (p/catch (fn [r]
                   (println "get-algo-full exception: " r)
                   nil)))))

(defn first-epoch-available [ctx-algo]
  (let [rows (c/get-chart-series ctx-algo)
        e  (-> rows first :epoch)]
    (println "first-epoch-available: " e "row-count: " (count rows))
    e))

(defn no-data-opts [algo-ctx]
  (clj->js
   {:noData true
     ;:nextTime (first-epoch-available algo-ctx)
    }))

; from - unix timestamp, leftmost required bar time (inclusive end)
; to: unix timestamp, rightmost required bar time (not inclusive)
; countBack - the exact amount of bars to load, should be considered a higher priority than from if your datafeed supports it (see below). 
; It may not be specified if the user requests a specific time period.
; firstDataRequest: boolean to identify the first call of this method. 
; When it is set to true you can ignore to (which depends on browser's Date.now()) 
; and return bars up to the latest bar.

; {:from 1595381213, :to 1650504413, :count-back 456, :first-request? true}
; {:from 1592184413, :to 1595381213, :count-back 27, :first-request? false}

; noData: boolean. This flag should be set if there is no data in the requested period.
; nextTime: unix timestamp (UTC). Time of the next bar in the history. 
; It should be set if the requested period represents a gap in the data. 
; Hence there is available data prior to the requested period.

(defn window-response [algo-ctx period onHistoryCallback onErrorCallback]
  (println "window-response .. calculating..")
  (let [rows-in-window (c/get-chart-series-window algo-ctx period)
        bars-tv (convert-bars rows-in-window)
        window-count (count rows-in-window)
        no-data? (= 0 window-count)
        bars (if no-data?
               (clj->js [])
               bars-tv)
        opts (if no-data?
               (no-data-opts algo-ctx)
               (clj->js {:noData false}))]
    (println "window-response period: " period " rows: " window-count)
    (onHistoryCallback bars opts)))

(defn get-bars [algo-ctx period onHistoryCallback onErrorCallback]
  (let [{:keys [algo opts]} (c/get-algo-input algo-ctx)
        _ (println "get-bars algo: " algo "symbol: " (:symbol opts) "period: " period)
        data (c/get-data algo-ctx)]
    (if (nil? data)
      (-> (get-algo-full algo-ctx)
          (p/then (fn [&_args]
                    (window-response algo-ctx period onHistoryCallback onErrorCallback))))
      (do (println "serving data from cached algo-ctx")
          (window-response algo-ctx period onHistoryCallback onErrorCallback)))))

(defn tradingview-algo-feed [algo-ctx]
  ;(println "tradingview-algo-feed setup ..")
  (clj->js
   {:onReady (fn [onConfigCallback]
               (let [rp (clj 'ta.tradingview.handler.config/get-server-config)]
                 (-> rp
                     (p/then (fn [result]
                               (println "TV CONFIG: " result)
                               (onConfigCallback (clj->js result))))
                     (p/catch (fn [err]
                                (println "TV CONFIG ERROR: " err))))))

    :searchSymbols (fn [userInput exchange symbolType onResultReadyCallback]
                     (let [query userInput
                           type symbolType
                           limit 10
                           rp (clj 'ta.tradingview.handler.asset/symbol-search query type exchange limit)]
                       (-> rp
                           (p/then (fn [result]
                                     (onResultReadyCallback (clj->js result))))
                           (p/catch (fn [err]
                                      (println "TV SEARCH error: " err))))))

    :resolveSymbol (fn [symbolName onSymbolResolvedCallback onResolveErrorCallback]
                     (println "TV/resolve symbol" symbolName)
                     (let [rp (clj 'ta.tradingview.handler.asset/symbol-info symbolName)]
                       (-> rp
                           (p/then (fn [result]
                                     (println "TV SYMBOL RESOLVED: " result)
                                     (onSymbolResolvedCallback (clj->js result))))
                           (p/catch (fn [err]
                                      (println "TV RESOLVE SYMBOL error: " err)
                                      (onResolveErrorCallback (clj->js err)))))))

    :getBars (fn [_symbolInfo _resolution period onHistoryCallback onErrorCallback]
               (let [period-clj (extract-period period)]
                 (get-bars algo-ctx period-clj onHistoryCallback onErrorCallback)
                 nil))

    :subscribeBars (fn [symbolInfo resolution _onRealtimeCallback subscribeUID onResetCacheNeededCallback]
                     (println "subscribe-bars: " symbolInfo resolution subscribeUID)
                     (add-watch c/need-cache-reset-atom :cache-reset
                                (fn [key state old-value new-value]
                                  (when new-value
                                    (println "triggering reset-cache-needed")
                                    (reset! c/need-cache-reset-atom false)
                                    (onResetCacheNeededCallback))))
                     nil)
    :unsubscribeBars (fn [subscriberUID]
                       (println "unsubscribe-bars: " subscriberUID))

    :getServerTime (fn [onTimeCallback]
                     (let [rp (clj 'ta.tradingview.handler.time/server-time)]
                       (p/then rp (fn [time]
                                    (println "TV TIME: " time)
                                    (onTimeCallback (clj->js time))))
                       (p/catch rp (fn [err]
                                     (println "ERROR GETTING SERVER TIME: " err)))))

    :calculateHistoryDepth (fn [resolution resolutionBack intervalBack]
                             (println "calculate history depth:" resolution resolutionBack intervalBack))

    :getMarks (fn [symbolInfo epoch-start epoch-end onDataCallback resolution]
                (let [;period-clj (extract-period period)
                      ; frequency (if (= resolution "1D") "D" resolution)
                      ;symbol-info-clj (js->clj symbolInfo :keywordize-keys true)
                      ;symbol (:ticker symbol-info-clj)
                      {:keys [algo opts]} (c/get-algo-input algo-ctx)
                      rp (clj 'ta.algo.manager/algo-marks algo opts epoch-start epoch-end)]
                  (-> rp
                      (p/then (fn [result]
                                (println "MARKS RESULT: " result)
                              ;(onDataCallback (clj->js []))
                                (onDataCallback (clj->js result))))
                      (p/catch (fn [err]
                                 (println "ERROR GETTING MARKS: " err))))
                  nil))

    :getTimeScaleMarks (fn [symbolInfo startDate endDate _onDataCallback resolution]
                         (println "**GET-TIMESCALE-MARKS**")
                         ;(println "get-timescale-marks" symbolInfo startDate endDate resolution)
                         )

    :about "algo-feed"}))

(defn get-tradingview-options-algo-feed [algo-ctx]
  (fn []
    {:datafeed (tradingview-algo-feed algo-ctx)
     :charts_storage_url "/api/tv/storage"}))
