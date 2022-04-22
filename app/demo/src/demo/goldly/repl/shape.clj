(ns demo.goldly.repl.shape
  (:require
   [goldly.scratchpad :refer [eval-code!]]))


(eval-code!
 (+ 5 5))

;; SHAPES

;; the MD file is copied in lib/tradingview
;; https://github.com/bitblockart/tradingview-charting-library/blob/master/wiki/Shapes-and-Overrides.md



(eval-code!
 (tv/add-shapes [{:time 1644364800}] {:shape "vertical_line"})) ; feb 9


(eval-code!
 (tv/add-shapes [{:time 1652054400}] {:shape "vertical_line"}))


(eval-code!
 (tv/add-shapes [{:time 1655683200}] {:shape "vertical_line"}))

; june 20




(eval-code!
 (tv/add-shapes [{:time 1649791880
                  :price 40000}]
                {:shape "arrow_up" ; arrow_down arrow_left arrow_right price_label arrow_marker flag
                 :text "🚀"
                 ;:location=location.belowbar
                 :color "#32CD32"
                 ;textcolor=color.new(color.white, 0)
                 :offset 0
                 ;:size size.auto
                 }))


; plotshape(buy == 1, text=, style=shape., 

(eval-code!
 (tv/add-shapes
  [{:time 1625764800 :price 45000}
   {:time 1649191891 :price 50000}]
  {:shape "gannbox_square"}))

(eval-code!
 (tv/add-shapes
  [{:time 1625764800 :price 45000}
   {:time 1649191891 :price 50000}
   {:time 1649291891 :price 55000}
   {:time 1649391891 :price 50000}
   {:time 1649491891 :price 40000}]
  {:shape "xabcd_pattern"}))

(eval-code!
 (tv/add-shapes [{:price 49000}]
                {:shape "horizontal_line"
                 :lock true
                 :disableSelection true
                 :showInObjectsTree false
                 :text "T1"
                 :overrides {:showLabel true
                             :horzLabelsAlign "right"
                             :vertLabelsAlign "middle"
                             :textcolor "#19ff20"
                             :bold true
                             :linewidth "1"
                             :linecolor "#19ff20"}}))


(eval-code!
  (let [to   (-> (.now js/Date) (/ 1000))
        from (- to  (* 500 24 3600)) ; 500 days ago
        ]
    (println "adding trendline from:" from " to: " to)
    (tv/add-shapes
     [{:time from :price 300}
      {:time to :price 500}]
     {:shape "trend_line"
      ;:lock true
      :disableSelection true
      :disableSave true
      :disableUndo true
      :text "mega trend"})))

(eval-code!
 (do
   (let [c (chart-active)
         panes (.getPanes c)]
     (.log js/console panes))
   (tv/add-shapes [{:time 1641748561
                  ;:price 40000
                    }]
                  {:shape "arrow_up" ; arrow_down arrow_left arrow_right price_label arrow_marker flag
                   :text "🚀"
                   :location "above_bar" ;"belowbar"
                   :color "#32CD32"
                 ;textcolor=color.new(color.white, 0)
                   :offset 300
                 ;:size size.auto
                   })))

(eval-code!
 (let [c (chart-active)
       shapes-js (.getAllShapes c)
       ct (.-length shapes-js)
        ;shapes (js->clj shapes-js)
       ]
    ;(count shapes)
   ct))


 ; this removes all drawings from the current tradingview widget
(eval-code!
 (let [c (chart-active)]
   (.removeAllShapes c)))
