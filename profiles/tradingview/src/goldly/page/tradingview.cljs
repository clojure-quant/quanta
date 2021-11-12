


(def config-tradingview-demo {:feed-url "https://demo_feed.tradingview.com"
                              :storage-url "https://saveload.tradingview.com"})

(def datafeed-urls
  {:demo "https://demo_feed.tradingview.com"
   :ta "/api/tv"})

(def storage-urls
  {:demo "https://saveload.tradingview.com"
   :ta "/api/tv/storage"})


(defn get-tradingview-datafeed [kw]
  (let [url (kw datafeed-urls)]
    ; new Datafeeds.UDFCompatibleDatafeed ()
    (js/Datafeeds.UDFCompatibleDatafeed. url)))

(def tv-options {:debug true ; false
                 :symbol "BTCUSD" ; DAX Index"
                 :interval "D"
                 :library_path "/r/tradingview/charting_library/"
                 :locale "en" ;
                 :disabled_features [] ;  ['use_localstorage_for_settings']
                 :enabled_features ['study_templates']
                 :charts_storage_api_version "1.1"
                 :client_id 77 ; "tradingview.com"
                 :user_id 77 ; "public_user_id"
                 ; size
                 :width 1200
                 :height 800
                 :fullscreen false ; // all window
                 ;:autosize true ; all space in container
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
                 })

(defn show-tradingview-widget [id feed-kw]
  (let [datafeed (get-tradingview-datafeed feed-kw) ; :demo
        tv-options (assoc tv-options 
                          :datafeed datafeed
                          :container_id id
                          :charts_storage_url (feed-kw storage-urls))
        tv-options (clj->js tv-options {:keyword-fn name})]
    ; new TradingView.widget ({});
    (js/TradingView.widget. tv-options)))

(defn tradingview-chart [feed-config-kw]
  [with-js
   {(browser-defined? "TradingView") "/r/tradingview/charting_library.min.js" ; js/TradingView
    (browser-defined? "Datafeeds")   "/r/tradingview/UDF/bundle.js"} ; js/Datafeeds
     ;[:h1 "tv loaded!"]
     [component {:start show-tradingview-widget
                 :config feed-config-kw}]
     
      ;
     ])

; in scratchpad enter:
; (show-tradingview-widget "scratchadtest" :ta )
; (js/TradingView.widget. tv-options)


(defn tradingview-page [route]
  [tradingview-chart :ta])

(add-page tradingview-page :tradingview)