(ns ta.viz.ds.highchart
  (:require
   [tick.core :as t]
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.helper.date :as dt]
   [ta.series.signal :refer [select-signal-is select-signal-has]]))

(defn ds-epoch
  "add epoch column to ds"
  [ds]
  (tds/column-map ds :epoch #(* 1000 (dt/->epoch-second %)) [:date]))

(defn series-ohlc
  "extracts ohlc series
   in format needed by highchart
   [[1560864600000,49.01,50.07,48.8,49.61]
    [1560951000000,49.92,49.97,49.33,49.47]]"
  [ds-epoch]
  (let [r (tds/mapseq-reader ds-epoch)]
    (mapv (juxt :epoch :open :high :low :close :volume) r)))

(defn series
  "extracts one column from ds 
   in format needed by highchart"
  [ds-epoch col]
  (let [r (tds/mapseq-reader ds-epoch)]
    (mapv (juxt :epoch col) r)))

(defn series-flags
  "extracts one column from ds 
   in format needed by highchart
   for signal plot"
  [ds-epoch col]
  (println "Series flags col:" col)
  (let [;ds-with-signal (select-signal-is ds-epoch col :buy)
        ds-with-signal (select-signal-has ds-epoch col)
        r (tds/mapseq-reader ds-with-signal)]
    (println "rows with signal: " (tds/row-count ds-with-signal))
    (into [] (map  (fn [row]
               ;(println "row: " row)
                     {:y (:close row)
                      ;:z 1000
                      :x (:epoch row)
                      :title (col row)
                      :text "desc"})) r)))

(defn add-series [ds-e grouping plot-no [col type-map]]
  (let [{:keys [type color]} (if (map? type-map)
                               type-map
                               {:color "blue"
                                :type type-map})
        axis {:type type
              :name (name col)
              :data (if (= type "flags")
                      (series-flags ds-e col)
                      (series ds-e col))
              :yAxis plot-no
              :color color
   ;:dataGrouping grouping
              }]
    (if (= type "flags")
      (assoc axis
             :shape "squarepin"
             :width 16
             :onSeries "0")
      axis)))

;; AXIS

(def ohlc-height 600)
(def other-height 10)

(def default-axis
  [{:resize {:enabled true}
    :labels {:align "right"
             :x -3}
    :title {:text "OHLC"}
    :height ohlc-height ; "60%"
    :lineWidth 2}])

(defn add-axis [ds-e grouping {:keys [yAxis series no]} line]
  (let [axis {:labels {:align "right"
                       :x -3}
              ;:title {:text "Volume"}
              ;:top "65%"
              :top (+ ohlc-height (* no other-height)) ; first additional axes starts at no = 0
              :height 200; "35%"
              ;:offset 0
              :lineWidth 2
              :resize {:enabled true}}
        plot-no (dec (count yAxis))
        new-series (map (partial add-series ds-e grouping plot-no) line)]
    {:yAxis (conj yAxis axis)
     :series (concat series new-series)
     :no (inc no)}))

(defn highchart-data [ds axes-spec]
  (let [ds-e (ds-epoch ds)
        grouping {:units [["week" [1]] ; // unit name - allowed multiples
                          ["month" [1, 2, 3, 4, 6]]]}
        series [{:type "candlestick" ; :type "ohlc"
                 :name "priceseries"
                 :data (series-ohlc ds-e)
                 ;:dataGrouping grouping
                 :id "0"}]]
    (reduce (partial add-axis ds-e grouping)
            {:yAxis default-axis
             :series series
             :no 0}
            axes-spec)))

(defn chart-pane-spec? [spec]
  true)

(defn chartpane-cols [spec]
  (concat [:date :open :high :low :close]
          []
          ;(map :path (:cols spec))
          ))

(defn highchart-opts-default [axes-spec]
  {;:title {:text title}
                     ;:xAxis {:categories (:labels data)}
   :tooltip {:style {:width "200px"}
             :valueDecimals 4
                               ;:valueSuffix " %"
             :shared true}
   :chart {:height (+ ohlc-height
                      (* other-height (dec (count axes-spec)))
                      100 ; size of time window selector
                      )}
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


(defn highstock-render-spec
  "returns a render specification {:render-fn :spec :data}. 
   spec must follow chart-pane format.
   The ui shows a barchart with extra specified columns 
   plotted with a specified style/position, 
   created from the bar-algo-ds"
  [env spec bar-algo-ds]
  (let [axes-spec (:charts spec)] ; search for :axes-spec
    (assert (chart-pane-spec? spec) "please comply with chart-pane-spec")
    {:render-fn 'ta.viz.ui/highstock
     :data (-> bar-algo-ds
               (tc/select-columns (chartpane-cols axes-spec))
               (highchart-data axes-spec))
     :spec (highchart-opts-default spec)}))

(comment

  (def ds
    (tc/dataset [{:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5}
                 {:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5}
                 {:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5}
                 {:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5}]))

  ds

  (highstock-render-spec nil {} ds)


  {:chart {:height 400}
   :rangeSelector {:verticalAlign "top", :x 0, :y 0}
   :plotOptions {:series {:animation 0}}
   :credits {:enabled false}
   :yAxis [{:labels {:align "right", :x -3}
            :title {:text "OHLC"}
            :height 400
            :lineWidth 2}]
   :series [{:type "candlestick", :name "priceseries"
             :data [[1548374400000 116 120.3499984741211 110 115.05000305175781 3587513]
                    [1548460800000 115.05000305175781 119.55000305175781 114.6500015258789 115.44999694824219 3807679]
                    [1548547200000 115.44999694824219 115.55000305175781 110.3499984741211 111.3499984741211 2006643]
                    [1548633600000 111.3499984741211 112.8499984741211 100.5 105.25 5392457]
                    [1548720000000 105.25 106 102.25 104.0999984741211 4614649]
                    [1548806400000 104.0999984741211 109.9000015258789 102.80000305175781 107.55000305175781 5984667]
                    [1548892800000 107.55000305175781 109.8499984741211 105.05000305175781 105.69999694824219 8769371]
                    [1548979200000 105.69999694824219 107.80000305175781 103.1500015258789 105.80000305175781 3081103]
                    [1549065600000 105.80000305175781 109.80000305175781 104.75 109.3499984741211 2976905]
                    [1549152000000 109.3499984741211 109.94999694824219 104.5 106 2796383]]
             :dataGrouping {:units [["week" [1]] ["month" [1 2 3 4 6]]]}
             :id "0"}]
   :no 0}

  {:chart {:height 1000}
   :rangeSelector {:verticalAlign "top", :x 0, :y 0}
   :plotOptions {:series {:animation 0}}
   :credits {:enabled false}
   :yAxis [{:labels {:align "right", :x -3}
            :title {:text "OHLC"}, :height 400, :lineWidth 2}
           {:labels {:align "right", :x -3}, :top 400, :height 200, :lineWidth 2}
           {:labels {:align "right", :x -3}, :top 600, :height 200, :lineWidth 2}
           {:labels {:align "right", :x -3}, :top 800, :height 200, :lineWidth 2}]
   :series ({:type "candlestick", :name "priceseries", :data [[1548374400000 116 120.3499984741211 110 115.05000305175781 3587513] [1548460800000 115.05000305175781 119.55000305175781 114.6500015258789 115.44999694824219 3807679] [1548547200000 115.44999694824219 115.55000305175781 110.3499984741211 111.3499984741211 2006643] [1548633600000 111.3499984741211 112.8499984741211 100.5 105.25 5392457] [1548720000000 105.25 106 102.25 104.0999984741211 4614649] [1548806400000 104.0999984741211 109.9000015258789 102.80000305175781 107.55000305175781 5984667] [1548892800000 107.55000305175781 109.8499984741211 105.05000305175781 105.69999694824219 8769371] [1548979200000 105.69999694824219 107.80000305175781 103.1500015258789 105.80000305175781 3081103] [1549065600000 105.80000305175781 109.80000305175781 104.75 109.3499984741211 2976905] [1549152000000 109.3499984741211 109.94999694824219 104.5 106 2796383]]
             :dataGrouping {:units [["week" [1]] ["month" [1 2 3 4 6]]]}, :id "0"}
            {:type "line", :name "open", :data [[1548374400000 116] [1548460800000 115.05000305175781] [1548547200000 115.44999694824219] [1548633600000 111.3499984741211] [1548720000000 105.25] [1548806400000 104.0999984741211] [1548892800000 107.55000305175781] [1548979200000 105.69999694824219] [1549065600000 105.80000305175781] [1549152000000 109.3499984741211]]
             :yAxis 1, :dataGrouping {:units [["week" [1]] ["month" [1 2 3 4 6]]]}}
            {:type "column", :name "volume", :data [[1548374400000 3587513] [1548460800000 3807679] [1548547200000 2006643] [1548633600000 5392457] [1548720000000 4614649] [1548806400000 5984667] [1548892800000 8769371] [1548979200000 3081103] [1549065600000 2976905] [1549152000000 2796383]]
             :yAxis 2, :dataGrouping {:units [["week" [1]] ["month" [1 2 3 4 6]]]}}), :no 3}

; 
  )