(defn days-ago [epoch days]
  (- epoch (* 24 60 60 1000 days)))

(defn bar [epoch last? days]
  {:time (days-ago epoch days)
   :open days
   :high days
   :low days
   :close days
   :volume days
   :isBarClosed true
   :isLastBar last?
   })

(def random-series 
  (let [today-dt (js/Date.)
        today (.valueOf today-dt)
        today 1649030400000
        _ (println "TODAY: " today-dt "epoch: " today)
        bars (map #(bar today false %) (range 2000 1 -1))
        last-bar (bar today true 1)
        bars (concat bars [last-bar])
        bars (into [] bars)]
    bars))

(println "BARS: " random-series)

(defn filter-random [from to]
  (println "filter-random " from to)
  (let [bars-filtered (->> (filter (fn [{:keys [time] :as bar}]
                                     (and (> time from) (< time to))) random-series)
                           (into []))]
    (println "bars-filtered: " (count bars-filtered))
    bars-filtered))

(defn get-bars-random [symbolInfo resolution period ok err]
  (.log js/console (.keys js/Object period))
  (let [symbol (.-ticker symbolInfo)
			  from (.-from period)
        to (.-to period)

        from1000 (* 1000 from)
        to1000 (* 1000 to)
        bars (filter-random from1000 to1000)
        data {:bars bars
              :meta {:noData false ; This flag should be set if there is no data in the requested period.
                    ;:nextTime nil
                     }}
        ;data-js (clj->js data)
        data-js (clj->js bars)
        ]
    (println "DATA: " data)
    (set! (.-tvd js/globalThis) data-js)
    (ok data-js) 
    nil
    ))

(get-bars-random "SPY" "D" {:from 1000 :to 2000} (fn [data] (println "DEMO: " data)) nil false)



(defn tradingview-study-adapter [udf study-data]
   {:onReady (fn [cb]
                   (println "on-ready")
                   (.onReady udf cb))
    :searchSymbols (fn [userInput exchange symbolType onResultReadyCallback]
                         (println "search symbol " userInput exchange symbolType)
                         (.searchSymbols udf userInput exchange symbolType onResultReadyCallback))
    :resolveSymbol (fn [symbolName onSymbolResolvedCallback onResolveErrorCallback]
                         (println "resolve symbol" symbolName)
                         (.resolveSymbol udf symbolName onSymbolResolvedCallback onResolveErrorCallback))

    :getBars (fn [symbolInfo resolution period onHistoryCallback onErrorCallback]
                   (let [ok (fn [data]
                              (.log js/console "DATA:")
                              (.log js/console data)
                              (set! (.-ddd js/window) data)
                              (onHistoryCallback data)
                              )
                         err (fn [data]
                               (.log js/console "DATA-ERR:")
                               (.log js/console data)
                               (onErrorCallback data))
                         ]
                   (println "GET-BARS" symbolInfo resolution period)
                   (if (.startsWith (.-name symbolInfo) "#")
                     (get-bars-random symbolInfo resolution period ok err)
                     (.getBars udf symbolInfo resolution period ok err))))

    :subscribeBars (fn [symbolInfo resolution onRealtimeCallback subscribeUID onResetCacheNeededCallback]
                         (println "subscribe: " symbolInfo resolution subscribeUID)
                         (.subscribeBars udf symbolInfo resolution onRealtimeCallback subscribeUID onResetCacheNeededCallback))
    :unsubscribeBars (fn [subscriberUID]
                           (println "unsubscribe: " subscriberUID)
                           (.unsubscribeBars udf subscriberUID))
    :getServerTime  (fn [cb]
                          (println "get-server-time")
                          (.getServerTime udf cb))
    :calculateHistoryDepth (fn [resolution resolutionBack intervalBack]
                                 (println "calculate history depth:" resolution resolutionBack intervalBack)
                                 (.calculateHistoryDepth udf resolution resolutionBack intervalBack))
    :getMarks (fn [symbolInfo startDate endDate onDataCallback resolution]
                    (println "getMarks" symbolInfo startDate endDate resolution)
                    (.getMarks udf symbolInfo startDate endDate onDataCallback resolution))
    :getTimeScaleMarks (fn [symbolInfo startDate endDate onDataCallback resolution]
                             (println "get-timescale-marks" symbolInfo startDate endDate resolution)
                             (.getMarks udf symbolInfo startDate endDate onDataCallback resolution))})

(defn tradingview-study [main]
  (fn []
    ;#js {"main" main} ; this main fn cannot be called.
    ;(set! (.-main this) main) ; this not defined in sci
    (let [;t (js/mystudy. main)
          t (js/bongo. main)]
      (println "created type: " t)
      t)))

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
                     [color])))})

(def clj-meta
  {:_metainfoVersion 51
   :id "clj@tv-basicstudies-1"
   :name "CLJ"
   :description "CLJ DESC ISA"
   :shortDescription "CLJ SHORT ISA"
   "isCustomIndicator" true
   "is_price_study" false
   "isTVScript" false
   "isTVScriptStub" false
   "format" {"type" "price"
             "precision" 4}
   "plots" [{"id" "plot_0"
             "type" "line"}]
   "defaults" {"styles" {"plot_0" {"linestyle" 0
                                   "visible" true
                                   "linewidth" 1 ; Make the line thinner
                                   "plottype" 2 ; Plot type is Line
                                   "trackPrice" true ; Show price line
                                   "color" "#880000" ; Set the plotted line color to dark red
                                   }}}

   "inputs" {}; [{"id" "test1"
              ; "name" "Bla"
              ; "type" "integer"
              ; "defval" 0}]

   "styles" {"plot_0" {"title" "Equity value" ; Output name will be displayed in the Style window
                       "histogramBase"  0}}}
  )

; (tradingview-study
;  (fn [& a] ; _ context input
;    (println "main..bar..colorer..")
;    (let [color (if (> 0.5 (js/Math.random))
;                  100
;                  200)]
;      [color])))
 

(defn clj-study-runner [PineJS] 
  (fn []
    (clj->js 
      {:init (fn [context inputCallback]
            (println "CLJ INIT!") ; called 1x
            (let [symbol "#EQUITY"]
              ;this._context = context;
              ;this._input = inputCallback;
              ;PineJS.Std.period (this._context)
              (.new_sym context symbol "D")
              nil))
       :main (fn [context inputCallback]
               (println "CLJ MAIN!") ; called for EACH BAR.
               (.select_sym context 1) ;this._context.select_sym (1);
               (let [v (-> PineJS .-Std (.close context))] ;var v = PineJS.Std.close (this._context);  
                 (println "VALUE: " v)
                 ;this._context = context;
                 ;this._input = inputCallback;
                 #js [v]))})))

(defn study-clj [PineJS]
  (clj->js 
   {:name "CLJ"
    :metainfo clj-meta
    :constructor (clj-study-runner PineJS)}))



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

(def tv-options-default
  {:debug true ; false
   :symbol "BTCUSD" ; DAX Index"
   :interval "D"
   :library_path "/r/tradingview/charting_library_21/" ; "/r/tradingview/charting_library/"
   :locale "en" ;
   ;:snapshot_url "https://myserver.com/snapshot",
   "hide_top_toolbar" true
   "hide_legend" true
   :disabled_features ["widget_logo"
                       "control_bar"
                       "create_volume_indicator_by_default" ; if disabled, no volume
                       ;"create_volume_indicator_by_default_once"
                       "volume_force_overlay" ; if disabled volume goes to separate pane
                       ;"use_localstorage_for_settings"
                       "charting_library_debug_mode"
                       "legend_widget"
                       "items_favoriting" ; Disabling this feature hides "Favorite this item" icon for Drawings and Intervals
                      
                       ;"header_compare"
                       "header_undo_redo"
                       "header_saveload"
                       "header_settings"
                       "header_fullscreen_button"
                       "header_screenshot"
                       ] 
   :enabled_features ["study_templates"
                      "side_toolbar_in_fullscreen_mode" ; enable drawing in fullscreen mode
                      "header_in_fullscreen_mode"
                      "two_character_bar_marks_labels" ; display at most two characters in bar marks. The default behaviour is to only display one character
                      ;"datasource_copypaste"	;	Enables copying of drawings and studies
                      ; "seconds_resolution"	;Enables the support of resolutions that start from 1 second
                      ;"tick_resolution" ;	Enables the support of tick resolution
                      "secondary_series_extend_time_scale"	; Enables a feature to allow an additional series to extend the time scale
                      ;"cl_feed_return_all_data" "off" ;Allows you to return more bars from the data feed than requested and displays it on a chart simultaneously
                      "same_data_requery" "off" ;Allows you to call setSymbol with the same symbol to refresh the data
                      "high_density_bars" "off" ;Allows zooming out to show more than 60000 bars on a single screen

                      "disable_resolution_rebuild" ; Shows bar time exactly as provided by the data feed with no adjustments.
                      ;; TRADING TERMINAL
                      "right_toolbar"
                      "add_to_watchlist"
                      "support_multicharts"
                      "show_trading_notifications_history"
                      ]
   :charts_storage_api_version "1.1"
   :client_id 77 ; "tradingview.com"
   :user_id 77 ; "public_user_id"
   ;:load_last_chart true ; Set this parameter to true if you want the library to load the last saved chart for a user (you should implement save/load first to make it work) .

   ; size
   :width 1200
   :height 800
   :fullscreen false ; // all window
   ;:autosize true ; all space in container
   :overrides {"mainSeriesProperties.style" 0
               "mainSeriesProperties.candleStyle.wickUpColor" "#336854"
               "mainSeriesProperties.candleStyle.wickDownColor" "#7f323f"
               "mainSeriesProperties.showCountdown" true
               "priceAxisProperties.autoScale" true
               "priceAxisProperties.autoScaleDisabled" false
               "priceAxisProperties.percentage" false
               "priceAxisProperties.percentageDisabled" false
               "priceAxisProperties.log" true
               "priceAxisProperties.logDisabled" true
               "priceAxisProperties.showSymbolLabels" false
               "mainSeriesProperties.priceAxisProperties.log" true
               "mainSeriesProperties.priceAxisProperties.autoScale" true
						   "volumePaneSize" "tiny"
			         "paneProperties.background" "#131722"
			         "paneProperties.vertGridProperties.color" "#363c4e"
		           "paneProperties.horzGridProperties.color" "#363c4e"
		           "scalesProperties.textColor" "#AAA"}
	:studies_overrides {"volume.volume.color.0" "#00FFFF"
						          "volume.volume.color.1" "#0000FF"
						          "volume.volume.transparency" 70
						          "volume.volume ma.color" "#FF0000"
						          "volume.volume ma.transparency" 30
						          "volume.volume ma.linewidth" 5
						          "volume.volume ma.visible" true
						          "bollinger bands.median.color" "#33FF88"
						          "bollinger bands.upper.linewidth" 7}
   :study_count_limit 5 ; Maximum amount of studies on the chart of a multichart layout. Minimum value is 2.
   :toolbar_bg "#f4f7f9"
   :favorites {:intervals ["1D" "D" "10"]
               :chartTypes ["Area" "Line"]}
   :custom_indicators_getter (fn [PineJS]
                               (let [s (clj->js [study-bar-colorer 
                                                 (js/equitystudy PineJS)
                                                 (study-clj PineJS)
                                                 ])]
                                 (.log js/console s)
                                 (.resolve js/Promise s)))

   :allow_symbol_change true
   :left_toolbar false
   "calendar" true
   "watchlist" ["NASDAQ:AAPL", "NASDAQ:TSLA", "NASDAQ:AMZN"]
   "withdateranges" true
   "range" "12M"
})

(def tv-widget-atom (r/atom nil))

(defn show-tradingview-widget [id {:keys [feed options]
                                   :or {options {}}}]
  (let [datafeed (get-tradingview-datafeed feed) ; :demo
        tv-options-data  {;:datafeed datafeed
                          :datafeed (tradingview-study-adapter datafeed {:test 123})
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
    tv-widget))

(defn tradingview-chart [options]
  [with-js
   {(browser-defined? "TradingView") "/r/tradingview/charting_library_21/charting_library.js" ;  "/r/tradingview/charting_library.min.js" ; js/TradingView
    (browser-defined? "Datafeeds")   "/r/tradingview/UDF_21/bundle.js" ; "/r/tradingview/UDF/bundle.js"
    (browser-defined? "MyStudy") "/r/tradingview/study.js"} ; js/Datafeeds
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
    [tradingview-chart options]))

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
                        :options {:autosize true}}]]])

(add-page tradingview-page :tradingview)

(ns tv
  (:require
   [user :refer [println tv-widget-atom chart-active alert]]
   [r]))

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
    (swap! state assoc :range vis-range)))

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
  "started saving chart..")

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
       :click (fn [] (alert "second clicked."))}
    #_{:position "bottom"
       :text "Bottom menu item"
       :click (fn [] (alert "third clicked."))}]))


