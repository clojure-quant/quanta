(ns ta.tradingview.chart.template.chart)

(def chart-template
  {:version 2
   :timezone "Etc/UTC"
   :timeScale {:m_barSpacing 23.33052489784434, :m_rightOffset 5}
   :chartProperties
   {:paneProperties {:background "#ffffff"
                     :topMargin 5
                     :bottomMargin 5
                     :legendProperties {:showStudyArguments true,
                                        :showStudyTitles true,
                                        :showStudyValues true,
                                        :showSeriesTitle true,
                                        :showSeriesOHLC true,
                                        :showLegend true,
                                        :showBarChange true,
                                        :showOnlyPriceSource true}
                     :leftAxisProperties {:percentageDisabled false,
                                          :autoScaleDisabled false,
                                          :lockScale false,
                                          :autoScale true,
                                          :alignLabels true,
                                          :percentage false,
                                          :indexedTo100 false,
                                          :log false,
                                          :logDisabled false}
                     :rightAxisProperties {:autoScale true,
                                           :autoScaleDisabled false,
                                           :lockScale false,
                                           :percentage false,
                                           :percentageDisabled false,
                                           :log false,
                                           :logDisabled false,
                                           :alignLabels true
                                           :indexedTo100 false}
                     :gridProperties {:color "#e1ecf2", :style 0}
                     :horzGridProperties {:color "#e1ecf2", :style 0},
                     :vertGridProperties {:color "#e1ecf2", :style 0}
                     :crossHairProperties {:color "rgba(117, 134, 150, 1)", :style 2, :transparency 0, :width 1}}
    :scalesProperties {:showRightScale true,
                       :showLeftScale false,
                       :showStudyPlotLabels false,
                       :showSeriesLastValue true,
                       :showSeriesPrevCloseValue false,
                       :showStudyLastValue false,
                       :scaleSeriesOnly false,
                       :textColor "#555",
                       :barSpacing 6,
                       :lineColor "#555",
                       :fontSize 11,
                       :backgroundColor "#ffffff",
                       :showSymbolLabels false}
    :chartEventsSourceProperties {:visible true, :futureOnly true, :breaks {:color "rgba(85, 85, 85, 1)", :visible false, :style 2, :width 1}}}})





