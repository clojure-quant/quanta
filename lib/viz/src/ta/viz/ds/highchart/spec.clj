(ns ta.viz.ds.highchart.spec
  (:require
   [ta.viz.chart-spec :refer [chart->series axes-count]]
   [ta.viz.ds.highchart.spec.default :refer [chart-default]]
   [ta.viz.ds.highchart.spec.axes :refer [y-axis]]
   [ta.viz.ds.highchart.spec.series :refer [series]]))

; todo:
; 1. pivot-points
;    https://www.highcharts.com/demo/stock/macd-pivot-points
; 2. vector (arrow links)
;    https://api.highcharts.com/highstock/series.vector

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
                   :other-height 100} chart-spec)

 ; 
  )