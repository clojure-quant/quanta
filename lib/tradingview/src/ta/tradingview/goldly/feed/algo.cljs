(ns ta.tradingview.goldly.feed.algo
  (:require
   [goldly.service.core :refer [run-cb]]
   [ta.tradingview.goldly.interact :as interact]
   [ta.tradingview.goldly.helper :refer [extract-period]]
   ))

(defn study-col? [symbol]
  (.includes symbol "#"))

(defn study-col [symbol]
  (let [a (.split symbol "#")
        symbol (aget a 0)
        col (aget a 1)]
    {:symbol symbol
     :col col}))

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

(defn convert-col-bar [col b]
  {:time (* 1000 (:epoch b))
   :open (col b)
   :high (col b)
   :low (col b)
   :close (col b)
   :volume (col b)
   :isBarClosed true
   :isLastBar false})

(defn convert-col-bars [col bars]
  (let [col (keyword col)]
    ;(println "convert-col-bars " col)
    (->> (map #(convert-col-bar col %) bars)
         (into [])
         (clj->js))))




(defn request-shapes [algo symbol frequency options epoch-start epoch-end]
  (let [cb (fn [[_ data]] ;  _ = event-type
             (let [{:keys [result _error]} data
                   batch-add (fn []
                               (doall
                                (map #(interact/add-shape (:points %) (:override %)) result)))]
               ;(println "SHAPES RCVD: " result)
               ;[{:points [{:time 1649791880}], :override {:shape vertical_line}}
               (js/setTimeout batch-add 1300)))]

    ;(println "GETTING SHAPES" algo epoch-start epoch-end)
    (run-cb {:fun 'ta.algo.manager/algo-shapes
             :args [algo symbol frequency options epoch-start epoch-end]
             :cb cb})))



(defn tradingview-algo-feed [get-algo-and-options]
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
    :getBars (fn [symbolInfo resolution period onHistoryCallback _onErrorCallback]
               (let [period-clj (extract-period period)
                     epoch-start (:from period-clj)
                     epoch-end (:to period-clj)

                     frequency (if (= resolution "1D")
                                 "D"
                                 resolution)
                     symbol-info-clj (js->clj symbolInfo :keywordize-keys true)
                     symbol (:ticker symbol-info-clj)
                     ;algo (or (get-algo) "buy-hold")
                     ;options {}
                     {:keys [algo options]} (get-algo-and-options)
                     study? (study-col? symbol)
                     col (when study? (:col (study-col symbol)))
                     symbol (if study? (:symbol (study-col symbol)) symbol)]
                 (if study?
                   (println "study col: " col)
                   (request-shapes algo symbol frequency options epoch-start epoch-end))
                 ;(println "GET-BARS" symbol-info-clj resolution period-clj "study: " study?)
                 (let [cb (fn [[_ data]] ;  _ = event-type
                            (let [{:keys [result _error]} data
                                  ;result-js (clj->js result)
                                  ]
                                 ;(println "TV BARS: " result)
                              (let [bars-tv (if study?
                                              (convert-col-bars col result)
                                              (convert-bars result))]
                                    ;(println "TV BARS CONVERTED: " bars-tv)
                                (onHistoryCallback bars-tv))))]
                   (run-cb {:fun 'ta.algo.manager/run-window-browser
                            :args [algo symbol frequency options epoch-start epoch-end]
                            :cb cb}))))
    :subscribeBars (fn [symbolInfo resolution _onRealtimeCallback subscribeUID _onResetCacheNeededCallback]
                     ;(println "subscribe: " symbolInfo resolution subscribeUID)
                     )
    :unsubscribeBars (fn [subscriberUID]
                       ;(println "unsubscribe: " subscriberUID)
                       )
    :getServerTime (fn [onTimeCallback]
                     (let [cb (fn [[_ data]] ;  _ = event-type
                                (let [{:keys [result _error]} data
                                      result-js (clj->js result)]
                                  ;(println "TV TIME: " result)
                                  (onTimeCallback result-js)))]
                       ;(println "TV/TIME")
                       (run-cb {:fun :tv/time
                                :args []
                                :cb cb})))
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
                      {:keys [algo options]} (get-algo-and-options)
                       ;algo (or (get-algo) "buy-hold")
                       ;options {}
                      cb (fn [[_ data]] ;  _ = event-type
                           (let [{:keys [result _error]} data
                                 result-js (clj->js result)]
                             ;(println "TV MARKS: " result)
                             (onDataCallback result-js)))]
                  ;(println "TV MARKS: algo" algo symbol startDate endDate frequency)
                  (run-cb {:fun 'ta.algo.manager/algo-marks
                           :args [algo symbol frequency options epoch-start epoch-end]
                           :cb cb})))
    :getTimeScaleMarks (fn [symbolInfo startDate endDate _onDataCallback resolution]
                         ;(println "get-timescale-marks" symbolInfo startDate endDate resolution)
                         )
    :about "algo-feed"}))


(defn get-tradingview-options-algo-feed [get-algo-and-options]
  (fn []
    {:datafeed (tradingview-algo-feed get-algo-and-options)
     :charts_storage_url "/api/tv/storage"}))
