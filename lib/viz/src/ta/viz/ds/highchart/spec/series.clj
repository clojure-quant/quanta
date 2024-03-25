(ns ta.viz.ds.highchart.spec.series
  (:require
   [ta.viz.chart-spec :refer [chart->series]]))

;; FLAGS
;; A flag series consists of flags marking events or points of interests. 
;; Used alone flag series will make no sense. 
;; Flags can be placed on either the series of the chart or on the axis.

(defn add-extra-opts-flags [series]
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
         ; default placement: close on :bar series.
         ;:onSeries ":close"
         :onSeries ":bar"
         :onKey "close"))

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
                 (add-extra-opts-flags series)

                 (= type :step)
                 (assoc series :type "line" ; step plot is a line plot
                        :animation false
                        :step true
                        :color color)

                 (= type :point)
                 (assoc series  :type "scatter" ; https://api.highcharts.com/highcharts/series.scatter
                                ;:lineWidth 0
                        :animation false
                        :marker {:enabled true
                                 :symbol "circle" ; ; "triangle" "square"
                                 :radius 2
                                 :color color})

                 (= type :line)
                 (assoc series :color color :animation false)

                 (= type :column)
                 (assoc series :color color :animation false)

                 (= type :range)
                 (assoc series :type "arearange"
                        :color color
                        :animation false)

                 (or (= type :ohlc) (= type :candlestick) (= type :hollowcandlestick))
                 (assoc series :animation false)

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