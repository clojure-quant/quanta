(ns ta.viz.ds.highchart.spec.chart)

(def chart-default
  {;; our settings
   :box :lg
   :ohlc-height 600
   :other-height 100
   ;; highchart settings start here.
   :xAxis    {:crosshair {:snap true}
              :resize {:enabled true}
              ;:categories (:labels data)  
              }

   ;:title {:text title}

   :tooltip {:style {:width "200px"}
             :valueDecimals 4
             ;:valueSuffix " %"
             :shared true}

   ; webgl boost enabled by default
   :boost false
   #_:boost #_{:useGPUTranslations true
               :seriesThreshold 5 ; Chart-level boost when there are more than 5 series in the chart
               :debug {:timeSetup true
                       :timeSeriesProcessing true
                       :timeKDTree true
                       :timeBufferCopy  true
                       :timeRendering true}}

   :chart {:height 1000 ; gets overwritten by set-chart-height
           ; zoom/pan
           :zooming {; https://api.highcharts.com/highcharts/chart.zooming.type
                     :key "alt" ; Should be set different than chart.panKey.
                     :type "x"}
           :panning {:enabled true
                     :type "x" ; "xy"
                     }
           :panKey "ctrl" ; "alt"" "shift"  "meta"
           ;:zoomType "x" ; depreciated, moved to :zooming
           }
   :plotOptions {:series {:animation 0
                            ;:label {;:pointStart 2010
                            ;        :connectorAllowed false}
                          }}

   :credits {:enabled false}

   :accessibility {:enabled false}

   ;; highstock specific starting here: *************************

    ; The navigator is a small series below the main series, displaying a view of the entire data set.
   :navigator {:enabled false}
    ;The range selector is a tool for selecting ranges to display within 
    ; the chart. It provides buttons to select preconfigured ranges in 
    ; the chart, like 1 day, 1 week, 1 month etc. It also provides input 
    ; boxes where min and max dates can be manually input.
   :rangeSelector {:enabled false
                    ;:verticalAlign "top" ; timeframe selector on the top
                    ;:selected 1   
                    ;:x 0
                    ;:y 0
                   }
 ;  
   })