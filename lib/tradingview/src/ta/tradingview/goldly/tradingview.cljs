(ns ta.tradingview.goldly.tradingview
  (:require
   [js-loader :refer [with-js browser-defined? component]]
   [ta.tradingview.goldly.algo.indicator-config :refer [custom-indicator-promise]]
   [ta.tradingview.goldly.interact :refer [tv-widget-atom]]))

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
                       ;"control_bar"
                       "create_volume_indicator_by_default" ; if disabled, no volume
                       ;"create_volume_indicator_by_default_once"
                       "volume_force_overlay" ; if disabled volume goes to separate pane
                       "use_localstorage_for_settings"
                       "charting_library_debug_mode"
                       ;"legend_widget"
                       "items_favoriting" ; Disabling this feature hides "Favorite this item" icon for Drawings and Intervals
                       ;"header_compare"
                       "header_undo_redo"
                       ;"header_saveload"
                       ;"header_settings"
                       ;"header_fullscreen_button"
                       "header_screenshot"]
   :enabled_features ["study_templates"
                      "side_toolbar_in_fullscreen_mode" ; enable drawing in fullscreen mode
                      "header_in_fullscreen_mode"
                      "two_character_bar_marks_labels" ; display at most two characters in bar marks. The default behaviour is to only display one character
                      ;"datasource_copypaste"	;	Enables copying of drawings and studies
                      ; "seconds_resolution"	;Enables the support of resolutions that start from 1 second
                      ;"tick_resolution" ;	Enables the support of tick resolution
                      ;"secondary_series_extend_time_scale"	; Enables a feature to allow an additional series to extend the time scale
                      ;"cl_feed_return_all_data"  ;Allows you to return more bars from the data feed than requested and displays it on a chart simultaneously
                      "same_data_requery" ;Allows you to call setSymbol with the same symbol to refresh the data
                      ;"high_density_bars"  ;Allows zooming out to show more than 60000 bars on a single screen

                      ;"cropped_tick_marks"
                      ;"disable_resolution_rebuild" ; Shows bar time exactly as provided by the data feed with no adjustments.
                      "study_symbol_ticker_description" ; Applies symbol display mode (ticker/description) to indicator inputs in the status line
                      "collapsible_header"
                      "go_to_date"]
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
               "mainSeriesProperties.showCountdown" false
               "priceAxisProperties.autoScale" true
               "priceAxisProperties.autoScaleDisabled" false
               "priceAxisProperties.percentage" false
               "priceAxisProperties.percentageDisabled" false
               "priceAxisProperties.log" true
               "priceAxisProperties.logDisabled" true
               "priceAxisProperties.showSymbolLabels" true ; false
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
   :study_count_limit 50 ; Maximum amount of studies on the chart of a multichart layout. Minimum value is 2.
   :toolbar_bg "#f4f7f9"
   :favorites {:intervals ["1D" "D" "10"]
               :chartTypes ["Area" "Line"]}
   :custom_indicators_getter custom-indicator-promise

   :allow_symbol_change true
   :left_toolbar false
   "withdateranges" true
   "range" "12M"
   "time_frames" [{:text "100y" :resolution "1D" :description "All" :title "All"}
                  {:text "50y" :resolution "6M" :description "50 Years"}
                  {:text "10y" :resolution "1D" :description "10 Years" :title "10yr"}
                  {:text "3y" :resolution "1D" :description "3 Years" :title "3yr"}
                  {:text "12m" :resolution "1D" :description "1 Year" :title "1yr"}
                  {:text "9m" :resolution "1D" :description "9 Month" :title "9m"}
                  {:text "1m" :resolution "1D" :description "1 Month" :title "1m"}]})

(defn show-tradingview-widget [id {:keys [feed options]
                                   :or {options {}}}]
  (let [tv-options-widget {:container id}
        tv-options-datafeed (feed)
        tv-options (merge tv-options-default tv-options-datafeed options tv-options-widget)
        _ (println "tv options: " (pr-str tv-options))
        tv-options (clj->js tv-options) ;  {:keyword-fn name}) ; this brings an error.
        tv-widget (js/TradingView.widget. tv-options) ; new TradingView.widget ({});
        ]
    (println "tv widget configured!!")
    (reset! tv-widget-atom tv-widget)
    (set! (.-widget js/window) tv-widget)
    tv-widget))

(defn shutdown-tradingview! [tv]
  (println "shutting down tradingview ..")
  (if (nil? tv)
    (println "tv is nil. Not calling shutdown.")
    (.remove tv)))

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







