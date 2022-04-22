(ns ta.tradingview.shape.core
  (:require
   [taoensso.timbre :refer [info warn error]]))

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
             {:time t1 :price p2}]
   :override {:shape "gannbox_square"}})


;  [{:time 1625764800 :price 45000}
;   {:time 1649191891 :price 50000}
;   {:time 1649291891 :price 55000}
;   {:time 1649391891 :price 50000}
;   {:time 1649491891 :price 40000}]
;  {:shape "xabcd_pattern"}))
