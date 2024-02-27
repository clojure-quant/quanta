(ns ta.viz.notebook.highstock
  (:require
   [ta.viz.renderfn.highstock :refer [highstock]]))

(def ohlc-series
  [[1560864600000,49.01,50.07,48.8,49.61]
   [1560951000000,49.92,49.97,49.33,49.47]
   [1561037400000,50.09,50.15,49.51,49.87]
   [1561123800000,49.7,50.21,49.54,49.69]
   [1561383000000,49.63,50.04,49.54,49.65]
   [1561469400000,49.61,49.81,48.82,48.89]
   [1561555800000,49.44,50.25,49.34,49.95]
   [1561642200000,50.07,50.39,49.89,49.94]
   [1561728600000,49.67,49.88,49.26,49.48]
   [1561987800000,50.79,51.12,50.16,50.39]
   [1562074200000,50.35,50.78,50.34,50.68]
   [1562160600000,50.82,51.11,50.67,51.1]
   [1562333400000,50.84,51.27,50.72,51.06]
   [1562592600000,50.2,50.35,49.6,50.01]
   [1562679000000,49.8,50.38,49.7,50.31]
   [1562765400000,50.46,50.93,50.39,50.81]
   [1562851800000,50.83,51.1,50.43,50.44]
   [1562938200000,50.61,51,50.55,50.83]])

(def close-series
  [[1560864600000,49.01]
   [1560951000000,49.92]
   [1561037400000,50.09]
   [1561123800000,49.7]
   [1561383000000,49.63]
   [1561469400000,49.61]
   [1561555800000,49.44]
   [1561642200000,50.07]
   [1561728600000,49.67]
   [1561987800000,50.79]
   [1562074200000,50.35]
   [1562160600000,50.82]
   [1562333400000,50.84]
   [1562592600000,50.2]
   [1562679000000,49.8]
   [1562765400000,50.46]
   [1562851800000,50.83]
   [1562938200000,50.61]])

(def volume-series
  [[1560864600000,49.01]
   [1560951000000,49.92]
   [1561037400000,50.09]
   [1561123800000,49.7]
   [1561383000000,49.63]
   [1561469400000,49.61]
   [1561555800000,49.44]
   [1561642200000,50.07]
   [1561728600000,49.67]
   [1561987800000,50.79]
   [1562074200000,50.35]
   [1562160600000,50.82]
   [1562333400000,50.84]
   [1562592600000,50.2]
   [1562679000000,49.8]
   [1562765400000,50.46]
   [1562851800000,50.83]
   [1562938200000,50.61]])


(defn make-chart-config [{:keys [title series]}]
  (let [{:keys [ohlc close volume]} series
        grouping {:units [["week" [1]] ; // unit name - allowed multiples
                          ["month" [1, 2, 3, 4, 6]]]}]
    {;:chart {;:type "line"
   ;        :animation false}
     :title {:text title}
   ;:xAxis {:categories (:labels data)}
   ;:tooltip {:valueSuffix " %"}
     :rangeSelector {;:selected 1   ; time selector on the top
                     :verticalAlign "top"
                     :x 0
                     :y 0}
     :plotOptions {:series {:animation 0
                          ;:label {;:pointStart 2010
                          ;        :connectorAllowed false}
                            }}

     :credits {:enabled false}

     :yAxis [{:labels {:align "right" :x -3}
              :title {:text "OHLC"}
              :height "60%"
              :lineWidth 2
            ;resize: {
            ;    enabled: true
            ;}
              }{:labels {:align "right" :x -3}
                :title {:text "Volume"},
                :top "60%",
                :height "40%",
                :offset 0,
                :lineWidth 2}]

     :series [{:type "candlestick" ; :type "ohlc"
               :name "priceseries"
               :data ohlc
               :dataGrouping grouping
               :id "27"}
              {:type         "line"
               :name         "close"
               :linkedTo     "priceseries"
               :data         close
             ;:yAxis        1
               :dataGrouping grouping}

              {:type "flags"
               :data [{:x 1561469400000     ; // Point where the flag appears
                       :title "O" ;, // Title of flag displayed on the chart 
                       :text  "open trade" ;  // Text displayed when the flag are highlighted.
                       }]
               :onSeries  "27" ;  // Id of which series it should be placed on. If not defined  the flag series will be put on the X axis
               :shape "flag"  ;// Defines the shape of the flags.
               :dataGrouping grouping}
              {:type "column"
               :name "Volume"
               :data volume,
               :yAxis 1,
               :dataGrouping grouping}]}))

(def data
  {:ohlc ohlc-series
   :close close-series
   :volume volume-series})

(def spec
  (make-chart-config
   {:title "stockchart"
    :series data}))

spec

(highstock {:data spec
            :box :lg} 
            data)
