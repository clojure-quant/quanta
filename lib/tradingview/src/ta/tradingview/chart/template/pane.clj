(ns ta.tradingview.chart.template.pane)

(def pane-template
  {:leftAxisState {:m_topMargin 0.05
                   :m_isLog false
                   :m_height 695
                   :m_isPercentage false
                   :m_priceRange nil
                   :m_isIndexedTo100 false
                   :m_bottomMargin 0.05
                   :m_isAutoScale true
                   :m_isLockScale false}
   :rightAxisState {:m_topMargin 0.05
                    :m_isLog true
                    :m_height 695
                    :m_isPercentage false
                    :m_priceRange {:m_maxValue 8.839996252388016, :m_minValue 8.458282716843062}
                    :m_isIndexedTo100 false
                    :m_bottomMargin 0.05
                    :m_isAutoScale true
                    :m_isLockScale false}
   :overlayPriceScales {:aTG7BS {:m_topMargin 0.75
                                 :m_isLog false
                                 :m_height 695
                                 :m_isPercentage false
                                 :m_priceRange {:m_maxValue 31890630656
                                                :m_minValue 0}
                                 :m_isIndexedTo100 false
                                 :m_bottomMargin 0
                                 :m_isAutoScale true
                                 :m_isLockScale false}}

   :stretchFactor 2000
   :mainSourceId nil ;id-main ; "pOQ6pA"
   :leftAxisSources []
   :rightAxisSources [] ;(into [id-main] ids-drawings) ; ["pOQ6pA"  "Co0ff2" "xy6qRv" "srISFZ" "8RaFG7" "pm68xf" "BlBo4C"]
   :sources []
   ;   
   })