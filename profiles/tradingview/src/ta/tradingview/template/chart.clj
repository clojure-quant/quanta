(ns ta.tradingview.template.chart)



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

