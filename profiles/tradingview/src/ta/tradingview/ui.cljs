(ns tradingview.chart
  (:require
   [clojure.string]
   [reagent.core :as r]
   [cljs-uuid-utils.core :as uuid]
   ["tradingview-lib" :as tv]
   ["tradingview-udf" :refer [UDFCompatibleDatafeed]]))

(defn create-feed! [feed-url]
  (println "Creating UDF Feed: " feed-url)
  (when (nil? UDFCompatibleDatafeed)
    (println "Error: UDF function is nil."))
  (UDFCompatibleDatafeed. feed-url))

(defn init-tradingview! [id {:keys [feed-url storage-url]}]
  (let [data-feed (create-feed! feed-url)
        options {:debug true ; false
                 :symbol "DAX Index"
                 :datafeed data-feed
                 :interval "D"
                 :container_id id ;  ID of the surrounding div
                 :library_path "/tradingview/charting_library/"
                 :locale "en" ;
                 :disabled_features [] ;  ['use_localstorage_for_settings']
                 :enabled_features ['study_templates']
                 :charts_storage_url storage-url ; "https://saveload.tradingview.com",
                 :charts_storage_api_version "1.1"
                 :client_id 77 ; "tradingview.com"
                 :user_id 77 ; "public_user_id"
			           ;width: 200,
			           ;height: 200,
                 :fullscreen false ; // all window
                 :autosize true ; all space in container

                 :studies_overrides {}
			;overrides: {
			; "mainSeriesProperties.showCountdown": true,
			;	"paneProperties.background": "#131722",
			;	"paneProperties.vertGridProperties.color": "#363c4e",
		  ;		"paneProperties.horzGridProperties.color": "#363c4e",
		  ;		"symbolWatermarkProperties.transparency": 90,
		  ;		"scalesProperties.textColor" : "#AAA",
		  ;		"mainSeriesProperties.candleStyle.wickUpColor": '#336854',
		  ;		"mainSeriesProperties.candleStyle.wickDownColor": '#7f323f'
		 ; 	}
                 }
        options-js (clj->js options {:keyword-fn name})
        _ (.log js/console options-js)]
    (tv/widget. options-js)))

(defn shutdown-tradingview! [tv]
  (println "shutting down tradingview ..")
  (if (nil? tv)
    (println "tv is nil. Not calling shutdown.")
    (.remove tv)))

(defn symbol->tradingview [str]
  (if (nil? str) str
      (clojure.string/replace str " " "_")))

(defn set-symbol! [tv]
  (println "TradingViewChart.ChangeSymbol: " symbol)
  (.setSymbol tv (symbol->tradingview symbol) "D"))

(defn change-feed-config [id config tv]
  (shutdown-tradingview! tv)
  (init-tradingview! id config))

(defn tradingview-chart [config]
  (let [id  (uuid/uuid-string (uuid/make-random-uuid))
        tv (r/atom nil)
        ;state (r/atom {})
        ]
    (r/create-class
     {:display-name  "tradingview"

      :reagent-render
      (fn [_]
        [:div {:id id :style {:width "100%" :height "100%"}}])

      :component-did-mount (fn [_]
                             (println "TradingViewChart.ComponentDidMount")
                             (reset! tv (init-tradingview! id config))
                             (.onChartReady @tv #(println "TradingView ChartWidget has loaded!")))

      :component-will-unmount (fn [this]
                                (println "TradingViewChart.ComponentDid-UN-Mount")
                                (shutdown-tradingview! @tv))

      ;:component-will-receive-props (fn [this new-argv]
      ;                                (println "receive props: " new-argv))

      :component-did-update (fn [this [_ prev-props prev-more]]
                              (let [[_ new-config] (r/argv this)]
                                (println "TradingViewChart.ComponentDidUpdate " new-config)
                                ;(if (not (=
                                (reset! tv (change-feed-config id new-config @tv))))})))

#_(defn tradingview-chart []
    [comp.loader/js-loader
     {:scripts {#(exists? js/Datafeeds) "/tradingview/UDF/bundle.js"
                #(exists? js/TradingView) "/tradingview/charting_library.min.js"
                #(exists? js/klipse) "https://storage.googleapis.com/app.klipse.tech/plugin/js/klipse_plugin.js"}
      :loading [:h1 "Loading Scripts..."]
      :loaded [:h1 "loaded!"]
    ;[chart]
      }])

