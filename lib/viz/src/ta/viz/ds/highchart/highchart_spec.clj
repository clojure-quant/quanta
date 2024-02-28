(ns ta.viz.ds.highchart.highchart-spec
  (:require
   [ta.viz.ds.highchart.chart-spec :refer [chart->series axes-count]]))


; todo:
; 1. pivot-points
;    https://www.highcharts.com/demo/stock/macd-pivot-points
; 2. vector (arrow links)
;    https://api.highcharts.com/highstock/series.vector



;A flag series consists of flags marking events or points of interests. 
; Used alone flag series will make no sense. 
; Flags can be placed on either the series of the chart or on the axis.



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
                :zIndex 1000
                :dataGrouping {:enabled false}
                :color color}
        series (if (= type :flags)
                 (assoc series 
                        :shape "squarepin" ; "circlepin"
                        ;:shape "url(data:image/svg+xml;base64,/r/indicator-svgrepo-com.svg)"
                        ;:shape "data:image/svg+xml,%3Csvg id='html5' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' x='0px' y='0px' width='50' height='50' viewBox='0 0 50 50' enable-background='new 0 0 50 50' xml:space='preserve'%3E%3Ctitle%3EHTML5 Logo%3C/title%3E%3Cpath id='shield' fill='%23E34F26' d='M6.5,44.9L2.5,0h44l-4,44.9L24.5,50'/%3E%3Cpath id='shield-light' fill='%23EF652A' d='M24.5,46.1l14.6-4l3.4-38.5h-18'/%3E%3Cpath id='five-shadow' fill='%23EBEBEB' d='M24.5,20.3h-7.3l-0.5-5.7h7.8V9.2h-0.1H10.7l0.1,1.5l1.4,15.2h12.4V20.3z M24.5,34.7 L24.5,34.7L18.3,33l-0.4-4.4H15h-2.5l0.7,8.7l11.3,3.1h0.1V34.7z'/%3E%3Cpath id='five' fill='%23FFFFFF' d='M24.5,20.3v5.6h6.8L30.6,33l-6.2,1.7v5.8l11.3-3.1l0.1-1l1.3-14.6l0.2-1.5h-1.6H24.5z M24.5,9.2 v3.4v2.1l0,0h13.4l0,0l0,0l0.1-1.2l0.3-2.8l0.1-1.5H24.5z'/%3E%3C/svg%3E"
                        ;"url(/r/indicator-svgrepo-com.svg)"
                        ;:color "red"
                        ;:color "#333333" 
                        :fillColor "rgba(255, 255, 255, .4)"
                       ; :width 16 
                        ;:selected true 
                        :allowOverlapX true ; https://stackoverflow.com/questions/53437956/highcharts-highstock-flags-series-issue#:~:text=All%20flags%20are%20not%20presented,set%20to%20false%20by%20default.
                        :zIndex 9999
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