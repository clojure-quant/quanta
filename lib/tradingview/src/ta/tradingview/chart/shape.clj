(ns ta.tradingview.chart.shape
  (:require
   [taoensso.timbre :refer [info warn error]]
   [tech.v3.dataset :as tds]
   [ta.helper.date :refer [parse-date epoch-second->datetime ->epoch-second]]))

(defn col->shapes [ds col cell->shape]
  (let [r (tds/mapseq-reader ds)]
    (into []
          (map (fn [row]
                 (cell->shape (:epoch row) (col row))) r))))

(defn text [time text]
  {:points [{:time time
             :channel "high" ; if price not set => open, high, low, close. 
             }]
   :override {:shape "text"
              :text text
              :channel "high"
              ;:location=location.belowbar
              :color "#32CD32"
              :fillBackground false
              :backgroundColor "rgba( 102, 123, 139, 1)"
              ;textcolor=color.new(color.white, 0)
              ;:size size.auto
              }})
(defn line-horizontal [price]
  {:points [{:price price}]
   :override {:shape "horizontal_line"
              ;:lock true
              ;:disableSelection false ; true
              ;:showInObjectsTree true ; false
              :text "T1"
              :overrides {:showLabel true
                          :horzLabelsAlign "right"
                          :vertLabelsAlign "middle"
                          :textcolor "#19ff20"
                          :bold true
                          :linewidth "1"
                          :linecolor "#19ff20"}}})

; 1649791880

(defn marker [time price]
  {:points [{:time time
             :price price}]
   :override {:shape "arrow_up" ; arrow_down arrow_left arrow_right price_label arrow_marker flag
              :text "🚀"
                 ;:location=location.belowbar
              :color "#32CD32"
                 ;textcolor=color.new(color.white, 0)
              :offset 0
                 ;:size size.auto
              }})

(defn gann-square [t1 p1 t2 p2]
  {:points  [{:time t1 :price p1}
             {:time t2 :price p2}]
   :override {:shape "gannbox_square"}})

;  [{:time 1625764800 :price 45000}
;   {:time 1649191891 :price 50000}
;   {:time 1649291891 :price 55000}
;   {:time 1649391891 :price 50000}
;   {:time 1649491891 :price 40000}]
;  {:shape "xabcd_pattern"}))

(def shapes
  {"arrow_up" {:color	"#787878"
               :font	"Verdana"
               :fontsize	20
               :text ""}
   "arrow_down" {:color	"#787878"
                 :font	"Verdana"
                 :fontsize	20
                 :text ""}
   "arrow_left" {:color	"#787878"
                 :font	"Verdana"
                 :fontsize	20
                 :text ""}
   "arrow_right" {:color	"#787878"
                  :font	"Verdana"
                  :fontsize	20
                  :text ""}
   "trend_line" {:bold	false
                 :extendLeft false
                 :extendRight false
                 :linewidth 1,
                 :linecolor "#2962FF",
                 :showMiddlePoint false,
                 :font "Verdana",
                 :textcolor "#787878"
                 :linestyle 0,
                 :fontsize 14,
                 :vertLabelsAlign "bottom",
                 :italic false,
                 :frozen false,
                 :showDateTimeRange false,
                 :text ""
                 :overrides {:showLabel true}}
   "vertical_line" {:bold	false
                    :extendLeft false
                    :extendRight false
                    :linewidth 1,
                    :linecolor "#2962FF",
                    :showMiddlePoint false,

                    :font "Verdana",
                    :textcolor "#787878"
                    :linestyle 0,
                    :fontsize 14,
                    :italic false,
                    :showDateTimeRange false,
                    :text ""
                    :visible true
                    :overrides {:showLabel true,
                                :textOrientation "vertical",
                                :horzLabelsAlign "right",
                                :vertLabelsAlign "top"}}})

(defn sanitize-point [{:keys [time price] :as point}]
  (if (and time (not (number? time)))
    (update point :time ->epoch-second)
    point))

(defn sanitize-points [points]
  (->> (map sanitize-point points)
       (into [])))

(comment
  (sanitize-point {:time (parse-date "2023-01-30") :price 5})
  (sanitize-points [{:time (parse-date "2022-01-30") :price 50}
                    {:time (parse-date "2023-01-30") :price 100}])
  ;
  )
(defn- plot-shape [type points user-style]
  ; helper function to apply default styles to a shape
  (let [default-style (get shapes type)]
    {:points (sanitize-points points)
     :shape  (merge
              {:overrides (merge default-style user-style)}
              #_{:disableSave true
                 :lock true
                 :frozen false
                 :showInObjectsTree false}
              user-style
              {:shape type})}))

(def arrow-directions
  {:up "arrow_up"
   :down "arrow_down"
   :right "arrow_right"
   :left "arrow_left"})

(defn arrow
  ([direction point]
   (arrow direction point {}))
  ([direction point user-style]
   (plot-shape (get arrow-directions direction) [point] user-style)))

(defn trend-line
  ([point1 point2]
   (trend-line point1 point2 {}))
  ([point1 point2 user-style]
   (plot-shape "trend_line" [point1 point2] user-style)))

(defn line-vertical
  ([time]
   (line-vertical time {}))
  ([time user-style]
   (plot-shape "vertical_line" [{:time time}] user-style)))


