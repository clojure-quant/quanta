(ns ta.viz.study-highchart
  (:require
   [tech.v3.dataset :as tds]
   [ta.data.date :as dt]))

; uses highcharts
(defn ds-epoch [ds]
  (tds/column-map ds :epoch #(* 1000 (dt/->epoch %)) [:date]))

; ohlc-series
;  [[1560864600000,49.01,50.07,48.8,49.61]
;   [1560951000000,49.92,49.97,49.33,49.47]])
(defn series-ohlc [ds-epoch]
  (let [r (tds/mapseq-reader ds-epoch)]
    (mapv (juxt :epoch :open :high :low :close :volume) r)))

(defn series [ds-epoch col]
  (let [r (tds/mapseq-reader ds-epoch)]
    (mapv (juxt :epoch col) r)))

; [{:close "line"
;  :sma30 "line"}  
; {:volume "line"}]

(defn add-series [ds-e grouping plot-no [col type]]
  {:type type
   :name (name col)
   :data (series ds-e col)
   :yAxis plot-no
   :dataGrouping grouping})

(defn add-axis [ds-e grouping {:keys [yAxis series no]} line]
  (let [axis {:labels {:align "right" :x -3}
              ;:title {:text "Volume"}
              ;:top "65%"
              :top (+ 400 (* no 200))
              :height 200; "35%"
              ;:offset 0
              :lineWidth 2}
        plot-no (dec (count yAxis))
        new-series (map (partial add-series ds-e grouping plot-no) line)]
    {:yAxis (conj yAxis axis)
     :series (concat series new-series)
     :no (inc no)}))

(defn add-data [ds axes-spec]
  (let [ds-e (ds-epoch ds)
        grouping {:units [["week" [1]] ; // unit name - allowed multiples
                          ["month" [1, 2, 3, 4, 6]]]}
        axes [{:labels {:align "right" :x -3}
               :title {:text "OHLC"}
               :height 400 ; "60%"
               :lineWidth 2}]
        series [{:type "candlestick" ; :type "ohlc"
                 :name "priceseries"
                 :data (series-ohlc ds-e)
                 :dataGrouping grouping
                 :id "0"}]]
    (reduce (partial add-axis ds-e grouping)
            {:yAxis axes :series series :no 0}
            axes-spec)))

(defn study-highchart [ds axes-spec]
  (let [spec-base {;:title {:text title}
                   ;:xAxis {:categories (:labels data)}
                   ;:tooltip {:valueSuffix " %"}
                   :chart {:height (+ 400 (* 200 (count axes-spec)))}
                   :rangeSelector {; timeframe selector on the top
                                   :verticalAlign "top"
                                   ;:selected 1   
                                   :x 0
                                   :y 0}
                   :plotOptions {:series {:animation 0
                          ;:label {;:pointStart 2010
                          ;        :connectorAllowed false}
                                          }}
                   :credits {:enabled false}}]
    ^:R
    [:p/highstock (merge spec-base (add-data ds axes-spec))]))


