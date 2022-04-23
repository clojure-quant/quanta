(ns ta.tradingview.shape.core
  (:require
   [taoensso.timbre :refer [info warn error]]
   [tech.v3.dataset :as tds]
   [ta.algo.manager :refer [algo-run-window]]))

(defn algo-col->shapes [algo symbol frequency options epoch-start epoch-end col cell->shape]
  (let [ds (algo-run-window algo symbol frequency options epoch-start epoch-end)
        r (tds/mapseq-reader ds)]
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

(defn line-vertical [time]
  {:points [{:time time}]
   :override {:shape "vertical_line"}})

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
