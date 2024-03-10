(ns ta.viz.ds.highchart.spec.default)

;; HIGHCHART-SPEC

(def chart-default
  {; our settings
   :box :lg
   :ohlc-height 600
   :other-height 100
   ; highchart
   :xAxis    {:crosshair {:snap true}
              ;:categories (:labels data)  
              }
   ;:title {:text title}
   :navigator     {:enabled true}
   :tooltip {:style {:width "200px"}
             :valueDecimals 4
             ;:valueSuffix " %"
             :shared true}
   :chart {:height 1000} ; this gets overwritten by set-chart-height
   :rangeSelector {; timeframe selector on the top
                   :verticalAlign "top"
                                     ;:selected 1   
                   :x 0
                   :y 0}
   :plotOptions {:series {:animation 0
                            ;:label {;:pointStart 2010
                            ;        :connectorAllowed false}
                          }}
   :credits {:enabled false}})