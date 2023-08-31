(ns ta.tradingview.goldly.feed.algo2
  (:require
    [promesa.core :as p]
    [goldly.service.core :refer [run-cb clj]]
   [ta.tradingview.goldly.interact2 :refer [add-shape]]
    [ta.tradingview.goldly.interact :refer [tv-widget-atom]]
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
               (clj->js {:noData false}))
        ]
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
          (window-response algo-ctx period onHistoryCallback onErrorCallback)    )
      )))


(defn request-shapes [algo symbol frequency options epoch-start epoch-end]
  (let [cb (fn [[_ data]] ;  _ = event-type
             (let [{:keys [result _error]} data
                   batch-add (fn []
                               (doall
                                (map #(add-shape @tv-widget-atom (:points %) (:override %)) result)))]
               ;(println "SHAPES RCVD: " result)
               ;[{:points [{:time 1649791880}], :override {:shape vertical_line}}
               (js/setTimeout batch-add 1300)))]

    ;(println "GETTING SHAPES" algo epoch-start epoch-end)
    (run-cb {:fun 'ta.algo.manager/algo-shapes
             :args [algo symbol frequency options epoch-start epoch-end]
             :cb cb})))


(defn tradingview-algo-feed [algo-ctx]
  ;(println "tradingview-algo-feed setup ..")
  (clj->js
   {:onReady (fn [onConfigCallback]
               (let [cb (fn [[_ data]] ;  _ = event-type
                          (let [{:keys [result _error]} data
                                result-js (clj->js result)]
                            ;(println "TV CONFIG: " result)
                            (onConfigCallback result-js)))]
                 ;(println "TV/CONFIG..")
                 (run-cb {:fun 'ta.tradingview.handler-datasource/get-server-config
                          :args {}
                          :cb cb})))
    :searchSymbols (fn [userInput exchange symbolType onResultReadyCallback]
                     ;(println "TV/SEARCH " userInput exchange symbolType)
                     (let [query userInput
                           type symbolType
                           limit 10
                           cb (fn [[_ data]] ;  _ = event-type
                                (let [{:keys [result _error]} data
                                      result-js (clj->js result)]
                                  ;(println "TV SYMBOL SEARCH: " result)
                                  (onResultReadyCallback result-js)))]
                       (run-cb {:fun 'ta.tradingview.handler-datasource/symbol-search
                                :args [query type exchange limit]
                                :cb cb})))
    :resolveSymbol (fn [symbolName onSymbolResolvedCallback _onResolveErrorCallback]
                     ;(println "TV/resolve symbol" symbolName)
                     (let [cb (fn [[_ data]] ;  _ = event-type
                                (let [{:keys [result _error]} data
                                      result-js (clj->js result)]
                                  ;(println "TV SYMBOL INFO: " result)
                                  (onSymbolResolvedCallback result-js)))]
                       (run-cb {:fun 'ta.tradingview.handler-datasource/symbol-info
                                :args [symbolName] ; {:symbol symbolName}
                                :cb cb})))
    :getBars (fn [_symbolInfo _resolution period onHistoryCallback onErrorCallback]
               (let [period-clj (extract-period period)]
                   ;(request-shapes algo symbol frequency opts epoch-start epoch-end) 
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
                     
                     )
    :unsubscribeBars (fn [subscriberUID]
                       (println "unsubscribe-bars: " subscriberUID)
                       )
    :getServerTime (fn [onTimeCallback]
                     (let [rp (clj 'ta.tradingview.handler-datasource/server-time)]
                       (p/then rp (fn [time]
                          (println "TV TIME: " time)
                          (onTimeCallback (clj->js time))))
                       (p/catch rp (fn [err]
                                     (println "ERROR GETTING SERVER TIME: " err)))))
    
    :calculateHistoryDepth (fn [resolution resolutionBack intervalBack]
                             (println "calculate history depth:" resolution resolutionBack intervalBack))

    :getMarks (fn [symbolInfo startDate endDate onDataCallback resolution]
                (let [;period-clj (extract-period period)
                      epoch-start startDate ; (:from period-clj)
                      epoch-end endDate ; (:to period-clj)
                      frequency (if (= resolution "1D")
                                  "D"
                                  resolution)
                      symbol-info-clj (js->clj symbolInfo :keywordize-keys true)
                      symbol (:ticker symbol-info-clj)
                      ;{:keys [algo options]} (get-algo-and-options)
                      cb (fn [[_ data]] ;  _ = event-type
                           (let [{:keys [result _error]} data
                                 result-js (clj->js result)]
                             ;(println "TV MARKS: " result)
                             (onDataCallback result-js)))]
                  ;(println "TV MARKS: algo" algo symbol startDate endDate frequency)
                  #_(run-cb {:fun 'ta.algo.manager/algo-marks
                           :args [algo symbol frequency options epoch-start epoch-end]
                           :cb cb})
                  (onDataCallback (clj->js []))
                  ))
    :getTimeScaleMarks (fn [symbolInfo startDate endDate _onDataCallback resolution]
                         ;(println "get-timescale-marks" symbolInfo startDate endDate resolution)
                         )
    :about "algo-feed"}))


(defn get-tradingview-options-algo-feed [algo-ctx]
  (fn []
    {:datafeed (tradingview-algo-feed algo-ctx)
     :charts_storage_url "/api/tv/storage"}))
