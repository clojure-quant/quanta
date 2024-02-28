(ns ta.viz.ds.highchart.highchart-spec
  (:require
   [ta.viz.ds.highchart.chart-spec :refer [chart->series axes-count]]))

;; SERIES SPEC

;{:type "candlestick" ; :type "ohlc"
;  :name "priceseries"
;  :dataGrouping grouping
;  :id "0"}

(defn one-series [{:keys [column type axis color title]
                   :or {color "blue"
                        title (name column)}}]
  (let [series {:type type
                :name title
                :yAxis axis
                :dataGrouping {:enabled false}
                :color color}
        series (if (= type :flags)
                 (assoc series :shape "squarepin" :width 16 :onSeries "0")
                 series)]
    series))

(defn series [chart]
  (let [series-seq (chart->series chart)]
    (->> (map one-series series-seq)
         (into []))))

;; AXES SPEC

(def ohlc-height 600)
(def other-height 10)

(def default-axis
  {:resize {:enabled true}
    :labels {:align "right" :x -3}
    :title {:text "OHLC"}
    :height ohlc-height ; "60%"
    :lineWidth 2})

(defn create-axis [axis-idx]
  {:labels {:align "right"
            :x -3}
              ;:title {:text "Volume"}
              ;:top "65%"
   :top (+ ohlc-height (* axis-idx other-height)) ; first additional axes starts at no = 0
   :height 200; "35%"
              ;:offset 0
   :lineWidth 2
   :resize {:enabled true}})

(defn y-axis [chart]
  (let [nr (axes-count chart)]
    (into []
          (-> (map create-axis (range nr))
              (conj default-axis)))))

;; HIGHCHART-SPEC

(defn highchart-opts-default [axes-nr]
  {;:title {:text title}
   ;:xAxis {:categories (:labels data)}
   :xAxis    {:crosshair {:snap true}}
   :navigator     {:enabled true}
   :tooltip {:style {:width "200px"}
             :valueDecimals 4
             ;:valueSuffix " %"
             :shared true}
   :chart {:height (+ ohlc-height
                      (* other-height (dec axes-nr))
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

;  grouping {:units [["week" [1]] ; // unit name - allowed multiples
;                  ["month" [1, 2, 3, 4, 6]]]}

(defn highchart-spec [chart]
  (merge {:yAxis (y-axis chart)
          :series (series chart)}
         (highchart-opts-default (axes-count chart))))


(comment

  (def chart-spec [{:trade :flags
                    :bb-lower {:type :line
                               :linewidth 2
                                 ;:color (color :blue-900)
                               }
                    :bb-upper {:type "line"
                               :linewidth 4
                                 ;:color (color :red)
                               }}
                   {:volume {:type "line"
                               ;:color (color :gold)
                               ;:plottype (plot-type :columns)
                             }}])

  (y-axis chart-spec)

  (series chart-spec)

  (highchart-spec chart-spec)

 ; 
  )