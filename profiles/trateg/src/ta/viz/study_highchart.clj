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

(defn add-series [ds-e grouping plot-no [col type-map]]
  (let [{:keys [type color]} (if (map? type-map)
                               type-map
                               {:color "blue"
                                :type type-map})]
    {:type type
     :name (name col)
     :data (series ds-e col)
     :yAxis plot-no
     :color color
   ;:dataGrouping grouping
     }))

(def ohlc-height 800)

(defn add-axis [ds-e grouping {:keys [yAxis series no]} line]
  (let [axis {:labels {:align "right" :x -3}
              ;:title {:text "Volume"}
              ;:top "65%"
              :top (+ ohlc-height (* no 200))
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
               :height ohlc-height ; "60%"
               :lineWidth 2}]
        series [{:type "candlestick" ; :type "ohlc"
                 :name "priceseries"
                 :data (series-ohlc ds-e)
                 ;:dataGrouping grouping
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

(comment

  (require '[ta.warehouse :as wh])
  (require '[tablecloth.api :as tc])
  (-> (wh/load-symbol :crypto "D" "ETHUSD")
      (tc/select-rows (range 10))
      (study-highchart [])
      second)

  {:chart {:height 400},
   :rangeSelector {:verticalAlign "top", :x 0, :y 0},
   :plotOptions {:series {:animation 0}},
   :credits {:enabled false},
   :yAxis [{:labels {:align "right", :x -3},
            :title {:text "OHLC"},
            :height 400,
            :lineWidth 2}],
   :series [{:type "candlestick", :name "priceseries",
             :data [[1548374400000 116 120.3499984741211 110 115.05000305175781 3587513]
                    [1548460800000 115.05000305175781 119.55000305175781 114.6500015258789 115.44999694824219 3807679]
                    [1548547200000 115.44999694824219 115.55000305175781 110.3499984741211 111.3499984741211 2006643]
                    [1548633600000 111.3499984741211 112.8499984741211 100.5 105.25 5392457]
                    [1548720000000 105.25 106 102.25 104.0999984741211 4614649]
                    [1548806400000 104.0999984741211 109.9000015258789 102.80000305175781 107.55000305175781 5984667]
                    [1548892800000 107.55000305175781 109.8499984741211 105.05000305175781 105.69999694824219 8769371]
                    [1548979200000 105.69999694824219 107.80000305175781 103.1500015258789 105.80000305175781 3081103]
                    [1549065600000 105.80000305175781 109.80000305175781 104.75 109.3499984741211 2976905]
                    [1549152000000 109.3499984741211 109.94999694824219 104.5 106 2796383]],
             :dataGrouping {:units [["week" [1]] ["month" [1 2 3 4 6]]]},
             :id "0"}],
   :no 0}

  {:chart {:height 1000},
   :rangeSelector {:verticalAlign "top", :x 0, :y 0},
   :plotOptions {:series {:animation 0}},
   :credits {:enabled false},
   :yAxis [{:labels {:align "right", :x -3},
            :title {:text "OHLC"}, :height 400, :lineWidth 2}
           {:labels {:align "right", :x -3}, :top 400, :height 200, :lineWidth 2}
           {:labels {:align "right", :x -3}, :top 600, :height 200, :lineWidth 2}
           {:labels {:align "right", :x -3}, :top 800, :height 200, :lineWidth 2}],
   :series ({:type "candlestick", :name "priceseries", :data [[1548374400000 116 120.3499984741211 110 115.05000305175781 3587513] [1548460800000 115.05000305175781 119.55000305175781 114.6500015258789 115.44999694824219 3807679] [1548547200000 115.44999694824219 115.55000305175781 110.3499984741211 111.3499984741211 2006643] [1548633600000 111.3499984741211 112.8499984741211 100.5 105.25 5392457] [1548720000000 105.25 106 102.25 104.0999984741211 4614649] [1548806400000 104.0999984741211 109.9000015258789 102.80000305175781 107.55000305175781 5984667] [1548892800000 107.55000305175781 109.8499984741211 105.05000305175781 105.69999694824219 8769371] [1548979200000 105.69999694824219 107.80000305175781 103.1500015258789 105.80000305175781 3081103] [1549065600000 105.80000305175781 109.80000305175781 104.75 109.3499984741211 2976905] [1549152000000 109.3499984741211 109.94999694824219 104.5 106 2796383]],
             :dataGrouping {:units [["week" [1]] ["month" [1 2 3 4 6]]]}, :id "0"}
            {:type "line", :name "open", :data [[1548374400000 116] [1548460800000 115.05000305175781] [1548547200000 115.44999694824219] [1548633600000 111.3499984741211] [1548720000000 105.25] [1548806400000 104.0999984741211] [1548892800000 107.55000305175781] [1548979200000 105.69999694824219] [1549065600000 105.80000305175781] [1549152000000 109.3499984741211]],
             :yAxis 1, :dataGrouping {:units [["week" [1]] ["month" [1 2 3 4 6]]]}}
            {:type "column", :name "volume", :data [[1548374400000 3587513] [1548460800000 3807679] [1548547200000 2006643] [1548633600000 5392457] [1548720000000 4614649] [1548806400000 5984667] [1548892800000 8769371] [1548979200000 3081103] [1549065600000 2976905] [1549152000000 2796383]],
             :yAxis 2, :dataGrouping {:units [["week" [1]] ["month" [1 2 3 4 6]]]}}), :no 3}

; 
  )