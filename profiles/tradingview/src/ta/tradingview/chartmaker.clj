(ns ta.tradingview.chartmaker
  (:require
   [nano-id.core :refer [nano-id]]
   [ta.tradingview.db :refer [save-chart now-epoch chart-list load-chart load-chart-boxed]]))


(def empty-meta {:listed_exchange ""
                 :symbol "MSFT"
                 :name "ffffg copy"
                 :client "77"
                 :is_realtime "1"
                 :short_name "MSFT"
                 :publish_request_id "r5kl776mb6o"
                 :resolution "D"
                 :legs [{:symbol "MSFT", :pro_symbol "MSFT"}]
                 :exchange "NasdaqNM"
                 :timestamp 1636555326
                 :user "77"
                 :symbol_type "stock"})


(def empty-chart
  {:version 2
   :timezone "Etc/UTC"
   :timeScale {:m_barSpacing 23.33052489784434, :m_rightOffset 5}
   :chartProperties {:paneProperties {:background "#ffffff"
                                      :topMargin 5
                                      :bottomMargin 5
                                      :legendProperties {:showStudyArguments true, :showStudyTitles true, :showStudyValues true, :showSeriesTitle true, :showSeriesOHLC true, :showLegend true, :showBarChange true, :showOnlyPriceSource true}
                                      :leftAxisProperties {:percentageDisabled false, :autoScaleDisabled false, :lockScale false, :autoScale true, :alignLabels true, :percentage false, :indexedTo100 false, :log false, :logDisabled false}
                                      :rightAxisProperties {:autoScale true, :autoScaleDisabled false, :lockScale false, :percentage false, :percentageDisabled false, :log false, :logDisabled false, :alignLabels true}
                                      :gridProperties {:color "#e1ecf2", :style 0}
                                      :horzGridProperties {:color "#e1ecf2", :style 0}, :vertGridProperties {:color "#e1ecf2", :style 0}
                                      :crossHairProperties {:color "rgba(117, 134, 150, 1)", :style 2, :transparency 0, :width 1}}
                     :scalesProperties {:showRightScale true, :showLeftScale false, :showStudyPlotLabels false, :showSeriesLastValue true, :showSeriesPrevCloseValue false, :showStudyLastValue false, :scaleSeriesOnly false, :textColor "#555", :barSpacing 6, :lineColor "#555", :fontSize 11, :backgroundColor "#ffffff", :showSymbolLabels false}
                     :chartEventsSourceProperties {:visible true, :futureOnly true, :breaks {:color "rgba(85, 85, 85, 1)", :visible false, :style 2, :width 1}}}})

(def empty-pane
  {:leftAxisState {:m_topMargin 0.05, :m_isLog false, :m_height 695, :m_isPercentage false, :m_priceRange nil, :m_isIndexedTo100 false, :m_bottomMargin 0.05, :m_isAutoScale true, :m_isLockScale false}
   :rightAxisState {:m_topMargin 0.05, :m_isLog false, :m_height 695, :m_isPercentage false, :m_priceRange {:m_maxValue 47.95, :m_minValue 33.6}, :m_isIndexedTo100 false, :m_bottomMargin 0.05, :m_isAutoScale true, :m_isLockScale false}
   :stretchFactor 2000
   :overlayPriceScales {:rN3cUr {:m_topMargin 0.75, :m_isLog false, :m_height 695, :m_isPercentage false, :m_priceRange {:m_maxValue 13478823, :m_minValue 0}, :m_isIndexedTo100 false, :m_bottomMargin 0, :m_isAutoScale true, :m_isLockScale false}}
   :mainSourceId "pOQ6pA"
   :leftAxisSources []
   :rightAxisSources ["pOQ6pA" "Co0ff2" "xy6qRv" "srISFZ" "8RaFG7" "pm68xf" "BlBo4C"]
   :sources []
   ;   
   })



(def source-mainseries
  {:type "MainSeries"
   :symbol "AMZN"
   :id "pOQ6pA"
   :zorder 4
   :pnfStyle {:studyId "BarSetPnF@tv-prostudies-15"}
   :renkoStyle {:studyId "BarSetRenko@tv-prostudies-15"}
   :rangeStyle {:studyId "BarSetRange@tv-basicstudies-72"}
   :pbStyle {:studyId "BarSetPriceBreak@tv-prostudies-15"}
   :haStyle {:studyId "BarSetHeikenAshi@tv-basicstudies-60"}
   :kagiStyle {:studyId "BarSetKagi@tv-prostudies-15"}
   :state {:shortName "AMZN"
           :interval "D"
           :visible true
           :showInDataWindow true
           :timeframe ""
           :pnfStyle {:upColor "rgba( 83, 185, 135, 1)" :downColor "rgba( 255, 77, 92, 1)" :upColorProjection "rgba( 169, 220, 195, 1)"
                      :downColorProjection "rgba( 245, 166, 174, 1)"
                      :inputs {:sources "Close", :reversalAmount 3, :boxSize 1, :style "ATR", :atrLength 14}
                      :inputInfo {:sources {:name "Source"}
                                  :boxSize {:name "Box size"}
                                  :reversalAmount {:name "Reversal amount"}
                                  :style {:name "Style"}, :atrLength {:name "ATR Length"}}}
           :baseLineColor "#B2B5BE", :prevClosePriceLineColor "rgba( 85, 85, 85, 1)"
           :renkoStyle {:borderDownColor "rgba( 255, 77, 92, 1)"
                        :wickUpColor "rgba( 83, 185, 135, 1)"
                        :wickDownColor "rgba( 255, 77, 92, 1)"
                        :inputInfo {:source {:name "Source"}, :boxSize {:name "Box size"}
                                    :style {:name "Style"}, :atrLength {:name "ATR Length"}
                                    :wicks {:name "Wicks"}}, :downColor "rgba( 255, 77, 92, 1)"
                        :inputs {:source "close", :boxSize 3, :style "ATR", :atrLength 14, :wicks true}
                        :downColorProjection "rgba( 245, 166, 174, 1)", :borderDownColorProjection "rgba( 245, 166, 174, 1)"
                        :borderUpColorProjection "rgba( 169, 220, 195, 1)", :upColor "rgba( 83, 185, 135, 1)"
                        :borderUpColor "rgba( 83, 185, 135, 1)", :upColorProjection "rgba( 169, 220, 195, 1)"}
           :showCountdown false
           :areaStyle {:color1 "rgba( 96, 96, 144, 0.5)"
                       :color2 "rgba( 1, 246, 245, 0.5)"
                       :linecolor "rgba( 0, 148, 255, 1)"
                       :linestyle 0, :linewidth 1
                       :priceSource "close", :transparency 50}
           :onWidget false, :rangeStyle {:upColor "rgba( 83, 185, 135, 1)"
                                         :downColor "rgba( 255, 77, 92, 1)"
                                         :upColorProjection "rgba( 169, 220, 195, 1)"
                                         :downColorProjection "rgba( 245, 166, 174, 1)"
                                         :inputs {:range 10, :phantomBars false}
                                         :inputInfo {:range {:name "Range"}
                                                     :phantomBars {:name "Phantom Bars"}}}
           :prevClosePriceLineWidth 1
           :barStyle {:upColor "rgba( 83, 185, 135, 1)"
                      :downColor "rgba( 255, 77, 92, 1)"
                      :barColorsOnPrevClose false, :dontDrawOpen false}
           :pbStyle {:borderDownColor "rgba( 255, 77, 92, 1)"
                     :inputInfo {:source {:name "Source"}, :lb {:name "Number of line"}}
                     :downColor "rgba( 255, 77, 92, 1)", :inputs {:source "close", :lb 3}
                     :downColorProjection "rgba( 245, 166, 174, 1)"
                     :borderDownColorProjection "rgba( 245, 166, 174, 1)"
                     :borderUpColorProjection "rgba( 169, 220, 195, 1)"
                     :upColor "rgba( 83, 185, 135, 1)"
                     :borderUpColor "rgba( 83, 185, 135, 1)"
                     :upColorProjection "rgba( 169, 220, 195, 1)"}
           :haStyle {:borderDownColor "rgba( 255, 77, 92, 1)", :drawWick true, :wickUpColor "rgba( 83, 185, 135, 1)"
                     :borderColor "rgba( 55, 134, 88, 1)", :wickDownColor "rgba( 255, 77, 92, 1)"
                     :inputInfo {}, :downColor "rgba( 255, 77, 92, 1)"
                     :barColorsOnPrevClose false
                     :inputs {}, :drawBorder true, :upColor "rgba( 83, 185, 135, 1)"
                     :borderUpColor "rgba( 83, 185, 135, 1)", :showRealLastPrice false
                     :wickColor "rgba( 115, 115, 117, 1)"}, :esdFlagSize 2, :esdShowBreaks false
           :baselineStyle {:baselineColor "rgba( 117, 134, 150, 1)", :baseLevelPercentage 50, :topFillColor2 "rgba( 83, 185, 135, 0.1)"
                           :topFillColor1 "rgba( 83, 185, 135, 0.1)", :bottomLineWidth 1
                           :topLineColor "rgba( 83, 185, 135, 1)", :bottomFillColor2 "rgba( 235, 77, 92, 0.1)"
                           :priceSource "close", :bottomFillColor1 "rgba( 235, 77, 92, 0.1)"
                           :bottomLineColor "rgba( 235, 77, 92, 1)", :transparency 50, :topLineWidth 1}, :style 1
           :priceLineWidth 1, :priceLineColor ""
           :statusViewStyle {:fontSize 17, :showExchange true :showInterval true, :showSymbolAsDescription false}
           :lineStyle {:color "rgba( 60, 120, 216, 1)", :linestyle 0, :linewidth 1, :priceSource "close", :styleType 2}
           :priceAxisProperties {:percentageDisabled false, :autoScaleDisabled false, :lockScale false
                                 :autoScale false, :alignLabels true, :percentage false, :indexedTo100 false
                                 :log false, :logDisabled false}, :showPrevClosePriceLine false, :esdShowSplits true
           :esdShowEarnings true
           :hollowCandleStyle {:borderDownColor "rgba( 255, 77, 92, 1)"
                               :drawWick true, :wickUpColor "rgba( 169, 220, 195, 1)"
                               :borderColor "rgba( 55, 134, 88, 1)", :wickDownColor "rgba( 245, 166, 174, 1)"
                               :downColor "rgba( 255, 77, 92, 1)", :drawBorder true
                               :upColor "rgba( 83, 185, 135, 1)", :borderUpColor "rgba( 83, 185, 135, 1)"
                               :wickColor "rgba( 115, 115, 117, 1)"}
           :showPriceLine true
           :candleStyle {:borderDownColor "#eb4d5c", :drawWick true
                         :wickUpColor "#a9cdd3", :borderColor "#378658"
                         :wickDownColor "#f5a6ae", :downColor "#eb4d5c"
                         :barColorsOnPrevClose false, :drawBorder true
                         :upColor "#53b987", :borderUpColor "#53b987"
                         :wickColor "#737375"}
           :minTick "default"
           :kagiStyle {:upColor "rgba( 83, 185, 135, 1)", :downColor "rgba( 255, 77, 92, 1)"
                       :upColorProjection "rgba( 169, 220, 195, 1)", :downColorProjection "rgba( 245, 166, 174, 1)"
                       :inputs {:source "close", :style "ATR", :atrLength 14, :reversalAmount 1}
                       :inputInfo {:source {:name "Source"}, :style {:name "Style"}
                                   :atrLength {:name "ATR Length"}, :reversalAmount {:name "Reversal amount"}}}
           :esdBreaksStyle {:color "rgba( 235, 77, 92, 1)", :style 2, :width 1}, :esdShowDividends true, :sessVis false
           :extendedHours false}})


(def source-study
  {:type "Study"
   :id "rN3cUr"
   :zorder 3
   :state {:name "Volume@tv-basicstudies"
           :id "Volume@tv-basicstudies"
           :fullId "Volume@tv-basicstudies-1"
           :productId "tv-basicstudies"
           :is_hidden_study false
           :description "Volume"
           :shortDescription "Volume"
           :paneSize "large"
           :isTVScript false
           :description_localized "Volume"
           :showInDataWindow true
           :shortId "Volume"
           :isTVScriptStub false
           :version "1"
           :visible true
           :is_price_study false
           :packageId "tv-basicstudies"
           :palettes {:volumePalette {:colors {:0 {:color "#eb4d5c", :width 1, :style 0, :name "Falling"}
                                               :1 {:color "#53b987", :width 1, :style 0, :name "Growing"}}}}
           :precision "default", :showStudyArguments true
           :inputs {:showMA false, :maLength 20}
           :graphics {}
           :styles {:vol {:linewidth 1, :color "#000080", :trackPrice false, :joinPoints false, :plottype 5, :title "Volume"
                          :linestyle 0, :visible true, :histogramBase 0, :transparency 65}
                    :vol_ma {:linewidth 1, :color "#0496FF", :trackPrice false, :joinPoints false
                             :plottype 4, :title "Volume MA", :linestyle 0, :visible true, :histogramBase 0, :transparency 65}}
           :area {}
           :plots {:0 {:id "vol", :type "line"}
                   :1 {:id "volumePalette", :palette "volumePalette", :target "vol", :type "colorer"}
                   :2 {:id "vol_ma", :type "line"}}
           :bands {}
           :_metainfoVersion 15
           :transparency 65}
   :metaInfo {:description "Volume"
              :isTVScript false
              :description_localized "Volume"
              :name "Volume@tv-basicstudies"
              :shortId "Volume"
              :id "Volume@tv-basicstudies-1"
              :fullId "Volume@tv-basicstudies-1"
              :packageId "tv-basicstudies"
              :productId "tv-basicstudies"
              :is_price_study false
              :palettes {:volumePalette {:colors {:0 {:name "Falling"}
                                                  :1 {:name "Growing"}}}}
              :isTVScriptStub false
              :defaults {:styles {:vol {:linestyle 0, :linewidth 1, :plottype 5, :trackPrice false, :transparency 65, :visible true, :color "#000080"}
                                  :vol_ma {:linestyle 0, :linewidth 1, :plottype 4, :trackPrice false, :transparency 65, :visible true, :color "#0496FF"}}, :precision 0, :palettes {:volumePalette {:colors {:0 {:color "#eb4d5c", :width 1, :style 0}, :1 {:color "#53b987", :width 1, :style 0}}}}, :inputs {:showMA false, :maLength 20}}, :shortDescription "Volume", :inputs [{:id "showMA", :name "show MA", :defval false, :type "bool"} {:id "maLength", :name "MA Length", :defval 20, :type "integer", :min 1, :max 2000}]
              :is_hidden_study false
              :graphics {}
              :styles {:vol {:title "Volume", :histogramBase 0}
                       :vol_ma {:title "Volume MA", :histogramBase 0}}
              :plots [{:id "vol", :type "line"}
                      {:id "volumePalette", :palette "volumePalette", :target "vol", :type "colorer"}
                      {:id "vol_ma", :type "line"}]
              :version "1",  :_metainfoVersion 15, :transparency 65}})




(def source-study-sessions
  {:type "study_Sessions"
   :id "BlBo4C"
   :state {:zorder 1
           :name "Sessions@tv-basicstudies"
           :description "Sessions"
           :ownerSource "pOQ6pA"
           :shortId "Sessions"
           :id "Sessions@tv-basicstudies"
           :fullId "Sessions@tv-basicstudies-1"
           :linkedToSeries true
           :description_localized "Sessions"
           :palettes {}
           :showInDataWindow true
           :precision "default"
           :showStudyArguments true
           :packageId "tv-basicstudies"
           :productId "tv-basicstudies"
           :shortDescription "Sessions"
           :inputs {}
           :is_hidden_study true
           :graphics {:vertlines {:sessBreaks {:color "#4985e7", :style 2, :visible false, :width 1, :name "Session Break"}}}
           :styles {}
           :is_price_study true
           :area {}
           :plots {}
           :version "1"
           :visible true
           :bands {}
           :_metainfoVersion 44}
   :metaInfo {:description "Sessions"
              :description_localized "Sessions"
              :palettes {}, :name "Sessions@tv-basicstudies"
              :shortId "Sessions", :productId "tv-basicstudies"
              :defaults {:graphics {:vertlines {:sessBreaks {:color "#4985e7", :style 2, :visible false, :width 1}}}
                         :linkedToSeries true, :precision 4}
              :shortDescription "Sessions"
              :inputs []
              :fullId "Sessions@tv-basicstudies-1"
              :is_hidden_study true
              :id "Sessions@tv-basicstudies-1"
              :graphics {:vertlines {:sessBreaks {:name "Session Break"}}}
              :is_price_study true
              :plots []
              :version "1"
              :packageId "tv-basicstudies"
              :_metainfoVersion 44}})

(defn source-trendline [{:keys [symbol a-p b-p a-t b-t]}]
  {:type "LineToolTrendLine"
   :id "pm68xf"
   :ownerSource "pOQ6pA"
   :linkKey "IPJgHK9obb7d"
   :zorder 2
   :state {:symbol symbol
           :lastUpdateTime 0
           :clonable true
           :interval "D"
           :visible true
           :frozen false
           :_isActualInterval true
           ; specific to trendline
           :linewidth 1
           :intervalsVisibilities {:minutesFrom 1, :daysTo 366, :secondsTo 59, :hoursTo 24, :months true, :days true, :seconds true, :daysFrom 1, :secondsFrom 1, :hours true, :ranges true, :hoursFrom 1, :minutes true, :minutesTo 59, :weeks true}
           :bold false, :linecolor "rgba( 21, 153, 128, 1)"
           :showMiddlePoint false, :leftEnd 0, :extendRight false
           :rightEnd 0
           :showPriceRange false
           :alwaysShowStats false, :snapTo45Degrees true
           :showBarsRange false
           :font "Verdana", :textcolor "rgba( 21, 119, 96, 1)"
           :linestyle 0, :showDistance false, :showAngle false
           :fontsize 12,  :statsPosition 2
           :italic false
           :showDateTimeRange false, :extendLeft false}
   :points [{:time_t a-t, :offset 0, :price a-p}
            {:time_t b-t, :offset 0, :price b-p}]})




(def source-pitchfork
  {:type "LineToolPitchfork"
   :id "xy6qRv"
   :linkKey "IaufLUGXhE6H"
   :ownerSource "pOQ6pA"
   :zorder -1
   :state {:symbol "NasdaqNM:AMZN"
           :lastUpdateTime 1636558442000
           :clonable true
           :frozen false
           :visible true
           :interval "D"
           :_isActualInterval true
           :intervalsVisibilities {:minutesFrom 1, :daysTo 366, :secondsTo 59, :hoursTo 24, :months true, :days true, :seconds true, :daysFrom 1, :secondsFrom 1, :hours true, :ranges true, :hoursFrom 1, :minutes true, :minutesTo 59, :weeks true}
           :level0 [0.25 "rgba( 160, 107, 0, 1)" false 0 1]
           :level1 [0.382 "rgba( 105, 158, 0, 1)" false 0 1]
           :level2 [0.5 "rgba( 0, 155, 0, 1)" true 0 1]
           :level3 [0.618 "rgba( 0, 153, 101, 1)" false 0 1]
           :level4 [0.75 "rgba( 0, 101, 153, 1)" false 0 1]
           :level5 [1 "rgba( 0, 0, 153, 1)" true 0 1]
           :level6 [1.5 "rgba( 102, 0, 153, 1)" false 0 1]
           :level7 [1.75 "rgba( 153, 0, 102, 1)" false 0 1]
           :level8 [2 "rgba( 165, 0, 0, 1)" false 0 1]
           :median {:visible true, :color "rgba( 165, 0, 0, 1)", :linewidth 1, :linestyle 0}
           :fillBackground true
           :style 0
           :transparency 80}
   :points [{:time_t 1514903400, :offset 0, :price 1383.1908446757407}
            {:time_t 1517581800, :offset 0, :price 1523.6126711861293}
            {:time_t 1516113000, :offset 0, :price 1294.0812396146428}]})


(defn source-gann [{:keys [symbol a-p b-p a-t b-t]}]
  {:type "LineToolGannComplex"
   :id "HirvcH"
   :state {:symbol symbol
           :zorder 2
           :linkKey "7ezN7K4XBLS8"
           :ownerSource "pOQ6pA"
           :version 2
           :scaleRatio 11.095765351911353
           :lastUpdateTime 0
           :interval "D"
           :clonable true
           :visible true
           :frozen false
           :reverse false
           :showLabels true
           :intervalsVisibilities {:minutesFrom 1, :daysTo 366, :secondsTo 59, :hoursTo 24, :months true, :days true, :seconds true, :daysFrom 1, :secondsFrom 1, :hours true, :ranges true, :hoursFrom 1, :minutes true, :minutesTo 59, :weeks true}
           :labelsStyle {:font "Verdana", :fontSize 12, :bold false, :italic false}
           :arcsBackground {:fillBackground true, :transparency 80}
           :fillBackground false, :_isActualInterval true
           :fanlines {:0 {:color "rgba( 165, 0, 255, 1)", :visible false, :width 1, :x 8, :y 1}
                      :1 {:color "rgba( 165, 0, 0, 1)", :visible false, :width 1, :x 5, :y 1}
                      :2 {:color "rgba( 128, 128, 128, 1)", :visible false, :width 1, :x 4, :y 1}
                      :3 {:color "rgba( 160, 107, 0, 1)", :visible false, :width 1, :x 3, :y 1}
                      :4 {:color "rgba( 105, 158, 0, 1)", :visible true, :width 1, :x 2, :y 1}
                      :5 {:color "rgba( 0, 155, 0, 1)", :visible true, :width 1, :x 1, :y 1}
                      :6 {:color "rgba( 0, 153, 101, 1)", :visible true, :width 1, :x 1, :y 2}
                      :7 {:color "rgba( 0, 153, 101, 1)", :visible false, :width 1, :x 1, :y 3}
                      :8 {:color "rgba( 0, 0, 153, 1)", :visible false, :width 1, :x 1, :y 4}
                      :9 {:color "rgba( 102, 0, 153, 1)", :visible false, :width 1, :x 1, :y 5}
                      :10 {:color "rgba( 165, 0, 255, 1)", :visible false, :width 1, :x 1, :y 8}}
           :arcs {:0 {:color "rgba( 160, 107, 0, 1)", :visible true, :width 1, :x 1, :y 0}
                  :1 {:color "rgba( 160, 107, 0, 1)", :visible true, :width 1, :x 1, :y 1}
                  :2 {:color "rgba( 160, 107, 0, 1)", :visible true, :width 1, :x 1.5, :y 0}
                  :3 {:color "rgba( 105, 158, 0, 1)", :visible true, :width 1, :x 2, :y 0}
                  :4 {:color "rgba( 105, 158, 0, 1)", :visible true, :width 1, :x 2, :y 1}
                  :5 {:color "rgba( 0, 155, 0, 1)", :visible true, :width 1, :x 3, :y 0}
                  :6 {:color "rgba( 0, 155, 0, 1)", :visible true, :width 1, :x 3, :y 1}
                  :7 {:color "rgba( 0, 153, 101, 1)", :visible true, :width 1, :x 4, :y 0}
                  :8 {:color "rgba( 0, 153, 101, 1)", :visible true, :width 1, :x 4, :y 1}
                  :9 {:color "rgba( 0, 0, 153, 1)", :visible true, :width 1, :x 5, :y 0}
                  :10 {:color "rgba( 0, 0, 153, 1)" :visible true, :width 1, :x 5, :y 1}}
           :levels {:0 [1 "rgba( 128, 128, 128, 1)" true]
                    :1 [1 "rgba( 160, 107, 0, 1)" true]
                    :2 [1 "rgba( 105, 158, 0, 1)" true]
                    :3 [1 "rgba( 0, 155, 0, 1)" true]
                    :4 [1 "rgba( 0, 153, 101, 1)" true]
                    :5 [1 "rgba( 128, 128, 128, 1)" true]}}
   :points [{:time_t a-t, :offset 0, :price a-p}
            {:time_t b-t, :offset 0, :price b-p}]})


(defn source-gann [{:keys [symbol a-p b-p a-t b-t]}]
  {:type "LineToolGannComplex"
   :id (nano-id 6) ; "Ix5dtc"
   :state {:intervalsVisibilities {:minutesFrom 1 :daysTo 366  :secondsTo 59  :hoursTo 24  :months true  :days true  :seconds true  :daysFrom 1  :secondsFrom 1
                                   :hours true :ranges true  :hoursFrom 1  :minutes true  :minutesTo 59  :weeks true}
           :labelsStyle {:font "Verdana"
                         :fontSize 12
                         :bold false
                         :italic false}
           :arcsBackground {:fillBackground true
                            :transparency 80}
           :fillBackground false
           :_isActualInterval true
           :fanlines {:10 {:color "rgba( 165, 0, 255, 1)"  :visible false  :width 1  :x 1 :y 8}
                      :0 {:color "rgba( 165, 0, 255, 1)" :visible false  :width 1 :x 8 :y 1}
                      :4 {:color "rgba( 105, 158, 0, 1)" :visible true  :width 1  :x 2  :y 1}
                      :7 {:color "rgba( 0, 153, 101, 1)" :visible false  :width 1  :x 1  :y 3}
                      :1 {:color "rgba( 165, 0, 0, 1)" :visible false  :width 1  :x 5  :y 1}
                      :8 {:color "rgba( 0, 0, 153, 1)" :visible false  :width 1  :x 1  :y 4}
                      :9 {:color "rgba( 102, 0, 153, 1)" :visible false  :width 1  :x 1 :y 5}
                      :2 {:color "rgba( 128, 128, 128, 1)" :visible false  :width 1  :x 4  :y 1}
                      :5 {:color "rgba( 0, 155, 0, 1)" :visible true :width 1 :x 1 :y 1}
                      :3 {:color "rgba( 160, 107, 0, 1)" :visible false  :width 1  :x 3 :y 1}
                      :6 {:color "rgba( 0, 153, 101, 1)" :visible true  :width 1  :x 1  :y 2}}
           :symbol symbol
           :showLabels true
           :arcs {:10 {:color "rgba( 0, 0, 153, 1)" :visible true :width 1 :x 5 :y 1}
                  :0 {:color "rgba( 160, 107, 0, 1)" :visible true  :width 1  :x 1  :y 0}
                  :4 {:color "rgba( 105, 158, 0, 1)" :visible true  :width 1  :x 2  :y 1}
                  :7 {:color "rgba( 0, 153, 101, 1)" :visible true  :width 1  :x 4  :y 0}
                  :1 {:color "rgba( 160, 107, 0, 1)" :visible true  :width 1  :x 1 :y 1}
                  :8 {:color "rgba( 0, 153, 101, 1)"  :visible true  :width 1 :x 4  :y 1}
                  :9 {:color "rgba( 0, 0, 153, 1)" :visible true  :width 1  :x 5 :y 0}
                  :2 {:color "rgba( 160, 107, 0, 1)" :visible true :width 1  :x 1.5  :y 0}
                  :5 {:color "rgba( 0, 155, 0, 1)"  :visible true :width 1 :x 3  :y 0}
                  :3 {:color "rgba( 105, 158, 0, 1)" :visible true :width 1 :x 2 :y 0}
                  :6 {:color "rgba( 0, 155, 0, 1)" :visible true :width 1 :x 3 :y 1}}
           :clonable true
           :levels {:0 [1 "rgba( 128, 128, 128, 1)" true]
                    :1 [1 "rgba( 160, 107, 0, 1)" true]
                    :2 [1 "rgba( 105, 158, 0, 1)" true]
                    :3 [1 "rgba( 0, 155, 0, 1)" true]
                    :4 [1 "rgba( 0, 153, 101, 1)" true]
                    :5 [1 "rgba( 128, 128, 128, 1)" true]}
           :scaleRatio 737.9710852623244
           :lastUpdateTime 1636584460200
           :interval "D"
           :visible true
           :frozen false
           :reverse false}
   :points [{:time_t a-t :offset 0 :price a-p}
            {:time_t b-t :offset 0 :price b-p}]
   :zorder 3
   :linkKey "tgEoPNzjMxMh"
   :ownerSource "pOQ6pA"
   :version 2})


(defn make-chart [chart-id symbol name drawings]
  (->> (assoc empty-meta
              :symbol symbol
              :short_name symbol
              :name name
              :desciption "autogenerated desc"
              :charts
              [(assoc
                empty-chart
                :panes [(assoc empty-pane
                               :sources (into [] (concat [source-mainseries
                                                          source-study
                                                          source-study-sessions]
                                                         drawings)))])])
       (save-chart 77 77 chart-id)))


(defn gann-vertical [p-0 d-p n a-t b-t]
  (into []
        (for [i (range n)]
          (source-gann
           {:symbol "NasdaqNM:AMZN"
            :a-p (+ p-0 (* i d-p))  :a-t a-t
            :b-p (+ p-0 (* (inc i) d-p))  :b-t b-t}))))


(gann-vertical 1000.0 200.0 5 1511879400 1515076200)


(make-chart 777  "AMZN" "alex"
            [;source-pitchfork
             (source-trendline {:symbol "NYSE:APA"
                                :a-p 40.20  :a-t 1519223400
                                :b-p 35.88060448358687  :b-t 1521725400})
             (source-gann
              {:symbol "NasdaqNM:AMZN"
               :a-p 1517.0  :a-t 1511879400
               :b-p 1794.0  :b-t 1515076200})
             (source-gann
              {:symbol "NasdaqNM:AMZN"
               :a-p 1300.0  :a-t 1511879400
               :b-p 1517.0  :b-t 1515076200})])

(make-chart 555  "AMZN" "vert ganns"
  (let [a-t 1511879400
        d-t 3196800
        ]         
  (concat (gann-vertical 1000.0 200.0 20 a-t (+ a-t d-t))
          (gann-vertical 1000.0 400.0 10 a-t (+ a-t (* 2 d-t)))
   )
            
            ))




(- 1515076200 1511879400 )






(comment


  (->> (assoc empty-meta
              :symbol "AMZN"
              :short_name "AMZN"
              :name "autogen gann2"
              :desciption "autogenerated desc"
              :charts
              [(assoc
                empty-chart
                :panes [(assoc empty-pane
                               :sources [source-mainseries
                                         source-study
                                         source-study-sessions
                                         (source-trendline {:symbol "NYSE:APA"
                                                            :a-p 40.20  :a-t 1519223400
                                                            :b-p 35.88060448358687  :b-t 1521725400})
                                         (source-gann
                                          {:symbol "NasdaqNM:AMZN"
                                           :a-p 1517.0  :a-t 1511879400
                                           :b-p 1794.0  :b-t 1515076200})
                                         ;source-pitchfork
                                         ])])])

       (save-chart 77 77 999))








  (chart-list 10 10)
  (chart-list 77 77)


  (load-chart 77 77 888)

  (load-chart-boxed 77 77 888)

  (-> (load-chart 77 77 722072) ; AMZN: Pitchfork   MSFT: LineTrend
      :charts
      first
      keys)



  (-> (load-chart 77 77 1636558275) ; AMZN: Pitchfork   MSFT: LineTrend
      :content  ; :layout :charts
      :charts
      first
      :panes
      first ; (:sources :leftAxisSources  :rightAxisSources :leftAxisState :rightAxisState  :overlayPriceScales :mainSourceId)
   ; :sources
      :sources
    ;count
      (get 5)
    ;:type
    ;(get-in [:state :styles])
      )

;
  )
