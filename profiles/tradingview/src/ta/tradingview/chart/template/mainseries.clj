(ns ta.tradingview.chart.template.mainseries)

(def template-mainseries
  {:type "MainSeries"
   :id ""
   :zorder 4
   :pnfStyle {:studyId "BarSetPnF@tv-prostudies-15"}
   :renkoStyle {:studyId "BarSetRenko@tv-prostudies-15"}
   :rangeStyle {:studyId "BarSetRange@tv-basicstudies-72"}
   :pbStyle {:studyId "BarSetPriceBreak@tv-prostudies-15"}
   :haStyle {:studyId "BarSetHeikenAshi@tv-basicstudies-60"}
   :kagiStyle {:studyId "BarSetKagi@tv-prostudies-15"}
   :state {:shortName ""
           :symbol ""
           :interval "D"
           :visible true
           :showInDataWindow true
           :showSessions false
           :timeframe ""
           :pnfStyle {:upColor "rgba( 83, 185, 135, 1)" :downColor "rgba( 255, 77, 92, 1)" :upColorProjection "rgba( 169, 220, 195, 1)"
                      :downColorProjection "rgba( 245, 166, 174, 1)"
                      :inputs {:sources "Close", :reversalAmount 3, :boxSize 1, :style "ATR", :atrLength 14}
                      :inputInfo {:sources {:name "Source"}
                                  :boxSize {:name "Box size"}
                                  :reversalAmount {:name "Reversal amount"}
                                  :style {:name "Style"}, :atrLength {:name "ATR Length"}}}
           :baseLineColor "#B2B5BE"
           :prevClosePriceLineColor "rgba( 85, 85, 85, 1)"
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
           :onWidget false
           :rangeStyle {:upColor "rgba( 83, 185, 135, 1)"
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
                     :wickColor "rgba( 115, 115, 117, 1)"}

           :baselineStyle {:baselineColor "rgba( 117, 134, 150, 1)", :baseLevelPercentage 50, :topFillColor2 "rgba( 83, 185, 135, 0.1)"
                           :topFillColor1 "rgba( 83, 185, 135, 0.1)", :bottomLineWidth 1
                           :topLineColor "rgba( 83, 185, 135, 1)", :bottomFillColor2 "rgba( 235, 77, 92, 0.1)"
                           :priceSource "close", :bottomFillColor1 "rgba( 235, 77, 92, 0.1)"
                           :bottomLineColor "rgba( 235, 77, 92, 1)", :transparency 50, :topLineWidth 1}
           :style 1
           :priceLineWidth 1
           :priceLineColor ""
           :statusViewStyle {:fontSize 17, :showExchange true :showInterval true, :showSymbolAsDescription false}
           :lineStyle {:color "rgba( 60, 120, 216, 1)", :linestyle 0, :linewidth 1, :priceSource "close", :styleType 2}
           :priceAxisProperties {:percentageDisabled false
                                 :autoScaleDisabled false
                                 :lockScale false
                                 :autoScale true
                                 :alignLabels true
                                 :percentage false
                                 :indexedTo100 false
                                 :log true
                                 :logDisabled false}
           :showPrevClosePriceLine false

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
           :esdBreaksStyle {:color "rgba( 235, 77, 92, 1)", :style 2, :width 1}
           :esdShowSplits true
           :esdShowEarnings true
           :esdFlagSize 2
           :esdShowBreaks false
           :esdShowDividends true
           :sessVis false
           :extendedHours false}})


