


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

(def tv-options-default {:debug true ; false
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

(defn show-tradingview-widget [id {:keys [feed options]
                                   :or {options {}}}]
  (let [datafeed (get-tradingview-datafeed feed) ; :demo
        tv-options-data  {:datafeed datafeed
                          :container_id id
                          :charts_storage_url (feed storage-urls)}
        tv-options (merge tv-options-default options tv-options-data)
        tv-options (clj->js tv-options {:keyword-fn name})]
    ; new TradingView.widget ({});
    (js/TradingView.widget. tv-options)))

(defn tradingview-chart [options]
  [with-js
   {(browser-defined? "TradingView") "/r/tradingview/charting_library.min.js" ; js/TradingView
    (browser-defined? "Datafeeds")   "/r/tradingview/UDF/bundle.js"} ; js/Datafeeds
     ;[:h1 "tv loaded!"]
     [component {:style {:height "100%"
                         :width "100%"
                         }
                 :start show-tradingview-widget
                 :config options}]
     
      ;
     ])

; in scratchpad enter:
; (show-tradingview-widget "scratchadtest" :ta )
; (js/TradingView.widget. tv-options)


(defn tradingview-page [route]
  [:div.w-screen.h-screen.m-0.p-5
  [tradingview-chart {:feed :ta 
                      :options {:autosize true}
                      }]])

(add-page tradingview-page :tradingview)