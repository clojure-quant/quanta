(ns ta.tradingview.goldly.algo.tradingview
  (:require
   [reagent.core :as r]
   [js-loader :refer [with-js browser-defined?]]
   [ta.tradingview.goldly.tradingview :as tv :refer [tradingview-chart show-tradingview-widget shutdown-tradingview!]]
   [ta.tradingview.goldly.feed.algo2 :refer [get-tradingview-options-algo-feed]]
   [ta.tradingview.goldly.algo.context :as c]
   [ta.tradingview.goldly.algo.interaction :as i]
   [ta.tradingview.goldly.algo.indicator-config :as config]))

#_(defn tradingview-algo-widget [algo-ctx]
    (let [{:keys [algo opts]} (c/get-algo-input algo-ctx)
          symbol (:symbol opts)]
      (println "showing tradingview-widget algo-mode algo: " algo " symbol: " symbol)
      [tradingview-chart {:feed (get-tradingview-options-algo-feed algo-ctx)
                          :options {:autosize true
                                    :symbol symbol}}]))

#_(defn tradingview-algo [algo-ctx]
  ;(i/track-interactions algo-ctx)   ; interactions tracking does not work.
    (tradingview-algo-widget algo-ctx))

(defn tradingview-widget-algo [algo-ctx]
  (let [id "tv-widget-1" ;(uuid/uuid-string (uuid/make-random-uuid))
        tv (r/atom nil)]
    (r/create-class
     {:display-name  "tradingview"
      :reagent-render  (fn [_]
                         [:div {:id id :style {:width "100%" :height "100%"}}])
      :component-did-mount (fn [_]
                             (println "TradingViewChart.ComponentDidMount")
                             (let [{:keys [algo opts]} (c/get-algo-input algo-ctx)
                                   symbol (:symbol opts)
                                   widget (show-tradingview-widget
                                           id
                                           {:feed (get-tradingview-options-algo-feed algo-ctx)
                                            :options {:autosize true
                                                      :symbol symbol
                                                      :custom_indicators_getter (config/algo-all-custom-indicator-promise algo-ctx)}})]
                               (reset! tv widget)
                               (set! (-> js/window .-tvwidget) widget)
                               (i/track-interactions algo-ctx widget)
                               (.onChartReady @tv #(println "TradingView ChartWidget has loaded!"))))
      :component-will-unmount (fn [this]
                                (println "TradingViewChart.ComponentDid-UN-Mount")
                                (shutdown-tradingview! @tv))
      ;:component-will-receive-props (fn [this new-argv]
      ;                                (println "receive props: " new-argv))
      :component-did-update (fn [this [_ prev-props prev-more]]
                              (println "tradingview-algo-widget did update.")
                              ;(let [[_ new-config] (r/argv this)]
                               ; (println "TradingViewChart.ComponentDidUpdate " new-config)
                                ;(if (not (=
                                ;(reset! tv (change-feed-config id new-config @tv))
                                ;                          )
                              )})))
(defn tradingview-algo [algo-ctx]
  [with-js
   {(browser-defined? "TradingView") "/r/tradingview/charting_library_21/charting_library.js" ;  "/r/tradingview/charting_library.min.js" ; js/TradingView
    (browser-defined? "Datafeeds")   "/r/tradingview/UDF_21/bundle.js" ; "/r/tradingview/UDF/bundle.js"
    (browser-defined? "MyStudy") "/r/tradingview/study.js"} ; js/Datafeeds
       ;[:h1 "tv loaded!"]
   [tradingview-widget-algo algo-ctx]])

