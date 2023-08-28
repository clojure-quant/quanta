(ns demo.goldly.repl.tradingview.shape
  (:require
   [eval.cljs-eval :refer [eval-code!]]))

(eval-code!
 (+ 5 5))

;; SHAPES

;; the MD file is copied in lib/tradingview
;; https://github.com/bitblockart/tradingview-charting-library/blob/master/wiki/Shapes-and-Overrides.md

(eval-code!
 (tv/add-shape [{:time 1644364800}] {:shape "vertical_line"})) ; feb 9

(eval-code!
 (tv/add-shape [{:time 1652054400}]
               {:shape "vertical_line"
                :disableSave true ; prevents saving the shape on the chart
                :disableUndo true ; prevents adding of the action to the undo stack
                }))
(eval-code!
 (tv/add-shape [{:time 1655683200
                 :price 5000
                  ;:offset 1000
                 }]
               {:shape "vertical_line"}))

; june 20

;; https://emojipedia.org/emoji/
;; http://www.unicode.org/Public/UNIDATA/UnicodeData.txt
;; http://www.unicode.org/charts/

;; https://github.com/rosejn/replchart

(eval-code!
 (tv/add-shape [{:time 1644364800
                  ;:price 135.0
                 }]
               {;:shape "arrow_up" ; arrow_down arrow_left arrow_right price_label arrow_marker flag
                :shape "text"
                 ;:text "🚀"
                ; :text "🌕" ;U+1F315 :full_moon:
                :text "🌑" ; New Moon	U+1F311
                 ;:location=location.belowbar
                :color "#32CD32"
                :frozen true
                :title "MR BIG"
                 ;:fillBackground false
                :backgroundColor "rgba( 102, 123, 139, 1)"
                 ;textcolor=color.new(color.white, 0)
                :offset 1000
                 ;:size size.auto
                :channel "close " ; if price not set => open, high, low, close. 
                }))
(eval-code!
 (tv/add-shape
  [{:time 1625764800.0 :price 250.0}
   {:time 1649191891.0 :price 450.0}]
  {:shape "gannbox_square"}))

(eval-code!
 (tv/add-shape
  [{:time 1642044120.261 :price 250.0}
   {:time 1654214400
    :price 450.0}]
  {:shape "gannbox_square"}))

(eval-code!
 (let [to   (-> (.now js/Date) (/ 1000))
       from (- to  (* 100 24 3600)) ; 100 days ago
       to (+ to  (* 60 24 3600))]
   (tv/add-shape
    [{:time from :price 250.0}
     {:time to :price 450.0}]
    {:shape "gannbox_square"})))

(eval-code!
 (tv/get-shape-properties "hgVGwq"))

(eval-code!
 (tv/add-shape
  [{:time 1625764800 :price 45000}
   {:time 1649191891 :price 50000}
   {:time 1649291891 :price 55000}
   {:time 1649391891 :price 50000}
   {:time 1649491891 :price 40000}]
  {:shape "xabcd_pattern"}))

(eval-code!
 (tv/add-shape [{:price 49000}]
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
       from (- to  (* 100 24 3600)) ; 100 days ago
       to (+ to  (* 60 24 3600))]
   (println "adding trendline from:" from " to: " to)
   (tv/add-shape
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
   (tv/add-shape [{:time 1641748561
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
   (.log js/console shapes-js)
   ct))

; this removes all drawings from the current tradingview widget
(eval-code!
 (let [c (chart-active)]
   (.removeAllShapes c)))

widget.activeChart () .getAllShapes () .forEach (({name}) => console.log (name));
