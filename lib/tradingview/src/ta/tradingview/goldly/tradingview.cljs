


(defn tradingview-study [main]
  (fn []
    ;#js {"main" main} ; this main fn cannot be called.
    ;(set! (.-main this) main) ; this not defined in sci
    (let [;t (js/mystudy. main)
          t (js/bongo. main)
          ]
      (println "created type: " t)
      t
      
      )
    
    ))

(def study-bar-colorer 
  {:name "Bar Colorer Demo"
   :metainfo {:_metainfoVersion 51
              :id "BarColoring@tv-basicstudies-1"
              :name "BarColoring"
              :description "Bar Colorer Demo"
              :shortDescription "BarColoring"
              "isCustomIndicator" true
              "is_price_study" true
              "isTVScript" false
              "isTVScriptStub" false
              "format" {"type" "price"
                        "precision" 4}
              "defaults" {"palettes" {"palette_0" {; palette colors
                                                ;  change it to the default colors that you prefer,
                                                ; but note that the user can change them in the Style tab
                                                ; of indicator properties
                                                "colors" [{"color" "#FFFF00"}
                                                         {"color" "#0000FF"}]}}}
              "inputs" []
              "plots" [{"id" "plot_0"
                       "type" "bar_colorer" ; plot type should be set to 'bar_colorer'
                       ; this is the name of the palette that is defined
                       ; in 'palettes' and 'defaults.palettes' sections
                       "palette" "palette_0"}]
              "palettes" {"palette_0" {"colors" [{"name" "Color 0"}
                                                 {"name" "Color 1"}]
                                       ; the mapping between the values that
                                       ; are returned by the script and palette colors
                                       "valToIndex" {100 0
                                                     200 1}}}}
    :constructor (tradingview-study
        (fn [& a] ; _ context input
          (println "main..bar..colorer..")
          (let [color (if (> 0.5 (js/Math.random))
                        100
                        200)]
            [color])))
     
    })

;(deftype Foo [a b c]
;       Object
;       ()




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
                         :library_path "/r/tradingview/charting_library_21/" ; "/r/tradingview/charting_library/"
                         :locale "en" ;
                         :disabled_features [] ;  ['use_localstorage_for_settings']
                         :enabled_features ["study_templates"]
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
                         :toolbar_bg "#f4f7f9"
                         :favorites {:intervals ["1D" "D" "10"]
						                         :chartTypes ["Area" "Line"]}
                         :custom_indicators_getter (fn [PineJS]
                                                     (let [s (clj->js [study-bar-colorer (js/equitystudy PineJS)])]
                                                       (.log js/console s)
                                                     (.resolve js/Promise s))
                                                     ) 
                         :widgetbar {:details true
                                     :watchlist true
                                     :news true
                                     :datawindow true
                                     :watchlist_settings {
                                        :default_symbols ["NYSE:AA" "NYSE:AAL" "NASDAQ:AAPL"]
                                        :readonly false}}
                         })

(def tv-widget-atom (r/atom nil))




(defn show-tradingview-widget [id {:keys [feed options]
                                   :or {options {}}}]
  (let [datafeed (get-tradingview-datafeed feed) ; :demo
        tv-options-data  {:datafeed datafeed
                          :container id ; :container_id
                          :charts_storage_url (feed storage-urls)}
        tv-options (merge tv-options-default options tv-options-data)
        ;_ (info "tv options cljs: " tv-options)
        tv-options (clj->js tv-options) ;  {:keyword-fn name}) ; this brings an error.
        ;_ (info (str "tv options: " tv-options) )
        tv-widget (js/TradingView.widget. tv-options) ; new TradingView.widget ({});
        ]
     (reset! tv-widget-atom tv-widget)
     ;(js/TradingView.widget. tv-options) ; new TradingView.widget ({});
    (set! (.-xxx js/window) tv-widget)
    tv-widget
    ))

(defn tradingview-chart [options]
  [with-js
   {(browser-defined? "TradingView") "/r/tradingview/charting_library_21/charting_library.js" ;  "/r/tradingview/charting_library.min.js" ; js/TradingView
    (browser-defined? "Datafeeds")   "/r/tradingview/UDF_21/bundle.js" ; "/r/tradingview/UDF/bundle.js"
    (browser-defined? "MyStudy") "/r/tradingview/study.js"
    } ; js/Datafeeds
     ;[:h1 "tv loaded!"]
   [component {:style {:height "100%"
                       :width "100%"}
               :start show-tradingview-widget
               :config options}]

      ;
   ])

; in scratchpad:
; (show-tradingview-widget "scratchpadtest" {:feed :ta})


;widget.activeChart () .createStudy ('MACD', false, false, [14, 30, "close", 9])



;widget.activeChart () .crossHairMoved () .subscribe (null,
;                                                     ({time, price}) => console.log (time, price));

 ;(.subscribe chm nil (fn [time price] (.log js/console time price)  )) 

 ;widget.activeChart () .onVisibleRangeChanged () .subscribe (null,
 ;                                                           ({from, to}) => console.log (from, to));


;(show-tradingview-widget "scratchpadtest" {:feed :ta} )
;@tv-widget-atom

;const from = Date.now() / 1000 - 500 * 24 * 3600; // 500 days ago
;const to = Date.now() / 1000;
;widget.activeChart().createMultipointShape(
;    [{ time: from, price: 150 }, { time: to, price: 150 }],
;    {
;        shape: "trend_line",
;        lock: true,
;        disableSelection: true,
;        disableSave: true,
;        disableUndo: true,
;        text: "text",
;    }
;);

(defn chart-active []
  (.activeChart @tv-widget-atom))

(defn tv-add-shapes [points shape]
  (let [chart (chart-active)
        points-js (clj->js points)
        shape-js (clj->js shape)
        ]
    (.createMultipointShape chart points-js shape-js)))

(defn add-demo-trendline []
  (let [to   (-> (.now js/Date) (/ 1000) )
        from (- to  (* 500 24 3600)) ; 500 days ago
        ]
    (println "adding trendline from:" from " to: " to)
    (tv-add-shapes
    [{:time from :price 30000}
     {:time to :price 40000}]
     {:shape "trend_line"
      ;:lock true
      :disableSelection true
      :disableSave true
      :disableUndo true
      :text "mega trend"})))

; window.tvWidget.activeChart().dataReady(() => {

(defn wrap-chart-ready [f]
  ; It's now safe to call any other methods of the widget
  (.onChartReady @tv-widget-atom f))

; (wrap-chart-ready (fn [] (println "chart ready!")))

(defn wrap-header-ready [f]
  (.headerReady @tv-widget-atom f))

; (wrap-header-ready (fn [] (println "header ready!")))


(defn add-header-button [text tooltip on-click-fn]
  (let [options (clj->js nil)
        button (.createButton @tv-widget-atom options)]
    (println "button: " button)
    (set! (.-textContent button) text)
    (set! (.-title button) tooltip)
    (.addEventListener button "click" on-click-fn)))

(defn demo-add-button []
  (add-header-button "re-gann" "my tooltip" (fn [] (println "button clicked "))))


(defn add-tv-menu [menu]
  (let [chart (chart-active)
        menu-js (clj->js menu)
        add-context-menu (fn [unixtime price]
                           (println "adding menu: " menu)
                           (println "args: " unixtime)
                           menu-js)]
    (println "adding menu: phase1: " menu)
    (println "menu-js: " menu-js)
    (.onContextMenu @tv-widget-atom add-context-menu)
    ))

(defn add-demo-menu []
  (add-tv-menu 
   [{"position" "top"
     "text" (str "First top menu item"); , time: " unixtime  ", price: " price)
     "click" (fn [] (alert "First clicked."))}
    #_{:text "-"
     :position "top"}
    #_{:text "-Objects Tree..."}
    #_{:position "top"
     :text "Second top menu item 2"
     :click: (fn [] (alert "second clicked."))} 
    #_{:position "bottom"
     :text "Bottom menu item"
     :click (fn [] (alert "third clicked."))}]))

(defn on-range-change [f]
  (let [chart (chart-active)
        visible-range (.onVisibleRangeChanged chart)]
    (.subscribe visible-range nil f)))

(defn print-range [r]
  (let [{:keys [from to]} (js->clj r)]
    (.log js/console "visible range changed from: " from "to: " to)))

(defn demo-range []
   (on-range-change print-range))

(defn on-crosshair-moved [f]
  (let [chart (chart-active)
        cross-hair (.crossHairMoved chart)]
    (.subscribe cross-hair nil f)))

(defn print-position [r]
  (let [{:keys [time price]} (js->clj r)]
    (println "position :" r)
    (.log js/console "crosshair position time: " time "price: " price)))

(defn demo-crosshair []
  (on-crosshair-moved print-position))


#_(let [chart (chart-active)
      chm (.crossHairMoved chart)
      ]
  (.createStudy chart "MACD" false false (clj->js [14 30 "close" 9]))
  (.createShape chart (clj->js {:time 1649791880}) (clj->js {:shape "vertical_line"}))
   ;(.subscribe chm nil (fn [time price] (.log js/console time price)  )) 
  ;(.log js/console chm)
  )





(defn tradingview-page [route]
  [:div.w-screen.h-screen.m-0.p-5
   [tradingview-chart {:feed :ta
                       :options {:autosize true}}]])

(add-page tradingview-page :tradingview)