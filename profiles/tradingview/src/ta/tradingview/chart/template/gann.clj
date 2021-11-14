(ns ta.tradingview.chart.template.gann)

(def gann
  {:type "LineToolGannComplex"
   :id  "Ix5dtc"
   :state {:symbol "symbol"
           :interval "D"
           :intervalsVisibilities {:minutesFrom 1 :daysTo 366  :secondsTo 59  :hoursTo 24  :months true  :days true  :seconds true  :daysFrom 1  :secondsFrom 1
                                   :hours true :ranges true  :hoursFrom 1  :minutes true  :minutesTo 59  :weeks true}
           :labelsStyle {:font "Verdana"
                         :fontSize 12
                         :bold false
                         :italic false}
           :arcsBackground {:fillBackground true
                            :transparency 80}
           :fillBackground false
           :_isActualInterval true
           :fanlines {:10 {:color "rgba( 165, 0, 255, 1)"  :visible false  :width 1  :x 1 :y 8}
                      :0 {:color "rgba( 165, 0, 255, 1)" :visible false  :width 1 :x 8 :y 1}
                      :4 {:color "rgba( 105, 158, 0, 1)" :visible true  :width 1  :x 2  :y 1}
                      :7 {:color "rgba( 0, 153, 101, 1)" :visible false  :width 1  :x 1  :y 3}
                      :1 {:color "rgba( 165, 0, 0, 1)" :visible false  :width 1  :x 5  :y 1}
                      :8 {:color "rgba( 0, 0, 153, 1)" :visible false  :width 1  :x 1  :y 4}
                      :9 {:color "rgba( 102, 0, 153, 1)" :visible false  :width 1  :x 1 :y 5}
                      :2 {:color "rgba( 128, 128, 128, 1)" :visible false  :width 1  :x 4  :y 1}
                      :5 {:color "rgba( 0, 155, 0, 1)" :visible true :width 1 :x 1 :y 1}
                      :3 {:color "rgba( 160, 107, 0, 1)" :visible false  :width 1  :x 3 :y 1}
                      :6 {:color "rgba( 0, 153, 101, 1)" :visible true  :width 1  :x 1  :y 2}}

           :showLabels true
           :arcs {:10 {:color "rgba( 0, 0, 153, 1)" :visible true :width 1 :x 5 :y 1}
                  :0 {:color "rgba( 160, 107, 0, 1)" :visible true  :width 1  :x 1  :y 0}
                  :4 {:color "rgba( 105, 158, 0, 1)" :visible true  :width 1  :x 2  :y 1}
                  :7 {:color "rgba( 0, 153, 101, 1)" :visible true  :width 1  :x 4  :y 0}
                  :1 {:color "rgba( 160, 107, 0, 1)" :visible true  :width 1  :x 1 :y 1}
                  :8 {:color "rgba( 0, 153, 101, 1)"  :visible true  :width 1 :x 4  :y 1}
                  :9 {:color "rgba( 0, 0, 153, 1)" :visible true  :width 1  :x 5 :y 0}
                  :2 {:color "rgba( 160, 107, 0, 1)" :visible true :width 1  :x 1.5  :y 0}
                  :5 {:color "rgba( 0, 155, 0, 1)"  :visible true :width 1 :x 3  :y 0}
                  :3 {:color "rgba( 105, 158, 0, 1)" :visible true :width 1 :x 2 :y 0}
                  :6 {:color "rgba( 0, 155, 0, 1)" :visible true :width 1 :x 3 :y 1}}
           :clonable true
           :levels {:0 [1 "rgba( 128, 128, 128, 1)" true]
                    :1 [1 "rgba( 160, 107, 0, 1)" true]
                    :2 [1 "rgba( 105, 158, 0, 1)" true]
                    :3 [1 "rgba( 0, 155, 0, 1)" true]
                    :4 [1 "rgba( 0, 153, 101, 1)" true]
                    :5 [1 "rgba( 128, 128, 128, 1)" true]}
           :scaleRatio 737.9710852623244
           :lastUpdateTime 1636584460200
           :visible true
           :frozen false
           :reverse false}
   :points [{:time_t 0 :offset 0 :price 0}
            {:time_t 0 :offset 0 :price 0}]
   :zorder 3
   :linkKey "tgEoPNzjMxMh"
   :ownerSource "pOQ6pA"
   :version 2})
