(ns ta.viz.ds.highchart.spec.series
  (:require
   [ta.viz.chart-spec :refer [chart->series]]
   [ta.viz.ds.highchart.spec.color :refer [set-color]]))

;; FLAGS
;; A flag series consists of flags marking events or points of interests. 
;; Used alone flag series will make no sense. 
;; Flags can be placed on either the series of the chart or on the axis.

;; ADD-TYPE

(defn type->str [t]
  (if (string? t)
    t
    (name t)))

(defn one-series [{:keys [type column axis color title]
                   :or {color "blue"
                        title (str column)}}]
  (let [series {:type (type->str type)
                :id (str column)
                :name title
                :yAxis axis
                :zIndex 1000
                :animation false
                :dataGrouping {:enabled false}}
        series (cond
                 (= type :flags)
                 (assoc series
                        :shape "squarepin" ; "circlepin"
                        :fillColor "rgba(255, 255, 255, .4)"
                        ; :width 16 
                        ;:selected true 
                        :allowOverlapX true ; https://stackoverflow.com/questions/53437956/highcharts-highstock-flags-series-issue#:~:text=All%20flags%20are%20not%20presented,set%20to%20false%20by%20default.
                        :zIndex 9999
                        ; default placement: close on :bar series.
                        :onSeries ":bar" ; ":close"
                        :onKey "close")

                 (= type :step)
                 (assoc series
                        :type "line" ; step plot is a line plot
                        :animation false
                        :step true
                        :color (set-color color))

                 (= type :point)
                 (assoc series
                        :type "scatter" ; https://api.highcharts.com/highcharts/series.scatter
                        ;:lineWidth 0
                        :animation false
                        :marker {:enabled true
                                 :symbol "circle" ; ; "triangle" "square"
                                 :radius 2
                                 :color (set-color color)})

                 (= type :line)
                 (assoc series :color (set-color color) :animation false)

                 (= type :column)
                 (assoc series
                        :color (set-color color)
                        :animation false)

                 (= type :range)
                 (assoc series
                        :type "arearange"
                        :color (set-color color)
                        :animation false)

                 (or (= type :ohlc) (= type :candlestick) (= type :hollowcandlestick))
                 (assoc series
                        :animation false)

                 :else
                 series)]
    series))

(defn series [panes]
  (let [series-seq (chart->series panes)]
    (->> (map one-series series-seq)
         (into []))))

(comment

  (series [{:open :line}])
  (series [{:open :step}])
  (series [{:open :point}])
  (series [{[:low :high] :range}])
  (series [{[:low :high] :candlestick}])

  (str [:low :high])

 ; 
  )