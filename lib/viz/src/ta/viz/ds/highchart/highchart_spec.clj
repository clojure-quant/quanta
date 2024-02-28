(ns ta.viz.ds.highchart.highchart-spec
  (:require
   [ta.viz.ds.highchart.chart-spec :refer [chart->series axes-count]]))

;; SERIES SPEC

;{:type "candlestick" ; :type "ohlc"
;  :name "priceseries"
;  :dataGrouping grouping
;  :id "0"}

(defn type->str [t]
  (if (keyword? t)
    (name t)
    t))

(defn one-series [{:keys [column type axis color title]
                   :or {color "blue"
                        title (name column)}}]
  (let [series {:type (type->str type)
                :id (name column)
                :name title
                :yAxis axis
                :dataGrouping {:enabled false}
                :color color}
        series (if (= type :flags)
                 (assoc series 
                        :shape "squarepin" ; "circlepin"
                        :width 16 
                        :selected true
                        :onSeries "close")
                 series)]
    series))

(defn series [panes]
  (let [series-seq (chart->series panes)]
    (->> (map one-series series-seq)
         (into []))))

;; AXES SPEC

(def axes-default
  {:resize {:enabled true}
   :lineWidth 2
   :labels {:align "right" :x -3}})

(defn ohlc-axis [ohlc-height]
  (assoc axes-default
         :height ohlc-height ; "60%"      
         :title {:text "OHLC"}))

(defn other-axis [ohlc-height other-height axis-idx]
  (assoc axes-default
  ;:title {:text "Volume"}
   ;:top "65%"
         :top (+ ohlc-height (* axis-idx other-height)) ; first additional axes starts at no = 0
         :height other-height ; "35%"
   ;:offset 0
         ))

(defn y-axis [chart panes]
  (let [nr (axes-count panes)
        ohlc-height (:ohlc-height chart)
        other-height (:other-height chart)]
    (into []
          (-> (map #(other-axis ohlc-height other-height %) (range nr))
              (conj (ohlc-axis ohlc-height))))))

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

;  grouping {:units [["week" [1]] ; // unit name - allowed multiples
;                  ["month" [1, 2, 3, 4, 6]]]}
(defn set-chart-height [chart panes]
  (let [axes-nr (axes-count panes)
        ohlc-height (:ohlc-height chart)
        other-height (:other-height chart)]
    (assoc-in chart [:chart :height]
              (+ ohlc-height
                 (* other-height (dec axes-nr))
                 100 ; size of time window selector
                 ))))

(defn highchart-spec [chart panes]
  (let [chart (or chart {})
        chart (merge chart-default chart)
        chart (set-chart-height chart panes)]
    (assoc chart 
       :yAxis (y-axis chart panes)
       :series (series panes))))

(comment

  (def chart-spec [{:trade :flags
                    :bb-lower {:type :flags
                               :linewidth 2
                                 ;:color (color :blue-900)
                               }
                    :bb-upper {:type :line
                               :linewidth 4
                                 ;:color (color :red)
                               }}
                   {:volume {:type "line"
                               ;:color (color :gold)
                               ;:plottype (plot-type :columns)
                             }}])

  (y-axis {:ohlc-height 600
           :other-height 100} chart-spec)

  (series chart-spec)

  (highchart-spec {:ohlc-height 600
                   :other-height 100} chart-spec)

  (highchart-spec {:box :sm
                   :ohlc-height 600
                   :other-height 100
                   } chart-spec)

 ; 
  )