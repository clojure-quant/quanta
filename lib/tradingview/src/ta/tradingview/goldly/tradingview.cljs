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



(defn chart-active []
  (.activeChart @tv-widget-atom))

(defn on-data-loaded [& args]
  (println "tv data has been loaded (called after set-symbol)"))

(defn set-symbol [symbol interval]
  (println "tv set symbol:" symbol "interval: " interval)
  (.setSymbol @tv-widget-atom symbol interval on-data-loaded)
  nil)

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






  ;(set! (.-bongo js/globalThis) i-clj)

(defn tradingview-chart-controlled [options symbol]
  (let [widget @tv-widget-atom]
    [tradingview-chart options]
    ))


(defn goto-symbol [s]
  [:a.pr-5.bg-blue-300 {:on-click #(set-symbol s "1D")} s])




(defn tradingview-page [route]
  [:div.w-screen.h-screen.m-0.p-0
   [:div.h-full.w-full.flex.flex-col
     [:div.h-64-w-full
       [goto-symbol "BTCUSD"]
       [goto-symbol "SPY"]
       [goto-symbol "TLT"]]
     [tradingview-chart {:feed :ta
                         :options {:autosize true}}] 
    ]
   ])

(add-page tradingview-page :tradingview)

(ns tv
  (:require 
    [user :refer [println tv-widget-atom chart-active alert]]
    [r]
   ))

(defonce state (r/atom {}))

(defn get-symbol []
  (let [i (.symbolInterval @tv-widget-atom)
        symbol (.-symbol i)
        interval (.-interval i)]
    (println "symbol: " symbol "interval: " interval)
    {:symbol symbol :interval interval}))


(defn on-crosshair-moved [f]
  (let [chart (chart-active)
        cross-hair (.crossHairMoved chart)]
    (.subscribe cross-hair nil f)))

(defn print-position [r]
  (let [price (.-price r)
        time (.-time r)
        position {:price price :time time}]
    ;(set! (.-bongo js/globalThis) r)
    (println "tv pos: " position)
    (swap! state assoc :position position)))

(defn demo-crosshair []
  (on-crosshair-moved print-position))

(defn on-range-change [f]
  (let [chart (chart-active)
        visible-range (.onVisibleRangeChanged chart)]
    (.subscribe visible-range nil f)))

(defn print-range [r]
  (let [from (.-from r)
        to (.-to r)
        vis-range {:from from :to to}]
    (.log js/console "visible range changed from: " from "to: " to)
    (swap! state assoc :range vis-range)
    ))

(defn demo-range []
  (on-range-change print-range))

;;

(defn add-shapes [points shape]
  (let [chart (chart-active)
        points-js (clj->js points)
        shape-js (clj->js shape)]
    (.createMultipointShape chart points-js shape-js)))

(defn add-study [study-name study-args]
  (let [chart (chart-active)
        study-args-js (-> study-args vec clj->js)]
    (.createStudy chart study-name false false study-args-js)
    nil))


(defonce tv-data (r/atom nil))

(defn on-save [data]
  (println "chart saved to: window.data")
  (.log js/console data)
  (reset! tv-data data)
  (set! (.-data js/globalThis) data))

(defn save-chart []
  (println "saving chart..")
  (.save @tv-widget-atom on-save)
  ;nil
  "started saving chart.."
  )

(defn get-chart []
  ;data.pa [0] .charts 
  (let [d @tv-data
        ;pa (.-pa d)
        d-clj (js->clj d)]
    (println "get-chart: ")
    (.log js/console d-clj)
     d-clj))



(defn add-tv-menu [menu]
  (let [chart (chart-active)
        menu-js (clj->js menu)
        add-context-menu (fn [unixtime price]
                           (println "adding menu: " menu)
                           (println "args: " unixtime)
                           menu-js)]
    (println "adding menu: phase1: " menu)
    (println "menu-js: " menu-js)
    (.onContextMenu @tv-widget-atom add-context-menu)))


;; DEMO

; in scratchpad:
; (show-tradingview-widget "scratchpadtest" {:feed :ta})
;@tv-widget-atom


(defn add-demo-trendline []
  (let [to   (-> (.now js/Date) (/ 1000))
        from (- to  (* 500 24 3600)) ; 500 days ago
        ]
    (println "adding trendline from:" from " to: " to)
    (add-shapes
     [{:time from :price 30000}
      {:time to :price 40000}]
     {:shape "trend_line"
      ;:lock true
      :disableSelection true
      :disableSave true
      :disableUndo true
      :text "mega trend"})))

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


