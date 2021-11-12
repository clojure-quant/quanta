(ns ta.tradingview.template.gann
  (:require
   [nano-id.core :refer [nano-id]]))

(defn gann-v0 [{:keys [symbol a-p b-p a-t b-t]}]
  {:type "LineToolGannComplex"
   :id "HirvcH"
   :state {:symbol symbol
           :zorder 2
           :linkKey "7ezN7K4XBLS8"
           :ownerSource "pOQ6pA"
           :version 2
           :scaleRatio 11.095765351911353
           :lastUpdateTime 0
           :interval "D"
           :clonable true
           :visible true
           :frozen false
           :reverse false
           :showLabels true
           :intervalsVisibilities {:minutesFrom 1, :daysTo 366, :secondsTo 59, :hoursTo 24, :months true, :days true, :seconds true, :daysFrom 1, :secondsFrom 1, :hours true, :ranges true, :hoursFrom 1, :minutes true, :minutesTo 59, :weeks true}
           :labelsStyle {:font "Verdana", :fontSize 12, :bold false, :italic false}
           :arcsBackground {:fillBackground true, :transparency 80}
           :fillBackground false, :_isActualInterval true
           :fanlines {:0 {:color "rgba( 165, 0, 255, 1)", :visible false, :width 1, :x 8, :y 1}
                      :1 {:color "rgba( 165, 0, 0, 1)", :visible false, :width 1, :x 5, :y 1}
                      :2 {:color "rgba( 128, 128, 128, 1)", :visible false, :width 1, :x 4, :y 1}
                      :3 {:color "rgba( 160, 107, 0, 1)", :visible false, :width 1, :x 3, :y 1}
                      :4 {:color "rgba( 105, 158, 0, 1)", :visible true, :width 1, :x 2, :y 1}
                      :5 {:color "rgba( 0, 155, 0, 1)", :visible true, :width 1, :x 1, :y 1}
                      :6 {:color "rgba( 0, 153, 101, 1)", :visible true, :width 1, :x 1, :y 2}
                      :7 {:color "rgba( 0, 153, 101, 1)", :visible false, :width 1, :x 1, :y 3}
                      :8 {:color "rgba( 0, 0, 153, 1)", :visible false, :width 1, :x 1, :y 4}
                      :9 {:color "rgba( 102, 0, 153, 1)", :visible false, :width 1, :x 1, :y 5}
                      :10 {:color "rgba( 165, 0, 255, 1)", :visible false, :width 1, :x 1, :y 8}}
           :arcs {:0 {:color "rgba( 160, 107, 0, 1)", :visible true, :width 1, :x 1, :y 0}
                  :1 {:color "rgba( 160, 107, 0, 1)", :visible true, :width 1, :x 1, :y 1}
                  :2 {:color "rgba( 160, 107, 0, 1)", :visible true, :width 1, :x 1.5, :y 0}
                  :3 {:color "rgba( 105, 158, 0, 1)", :visible true, :width 1, :x 2, :y 0}
                  :4 {:color "rgba( 105, 158, 0, 1)", :visible true, :width 1, :x 2, :y 1}
                  :5 {:color "rgba( 0, 155, 0, 1)", :visible true, :width 1, :x 3, :y 0}
                  :6 {:color "rgba( 0, 155, 0, 1)", :visible true, :width 1, :x 3, :y 1}
                  :7 {:color "rgba( 0, 153, 101, 1)", :visible true, :width 1, :x 4, :y 0}
                  :8 {:color "rgba( 0, 153, 101, 1)", :visible true, :width 1, :x 4, :y 1}
                  :9 {:color "rgba( 0, 0, 153, 1)", :visible true, :width 1, :x 5, :y 0}
                  :10 {:color "rgba( 0, 0, 153, 1)" :visible true, :width 1, :x 5, :y 1}}
           :levels {:0 [1 "rgba( 128, 128, 128, 1)" true]
                    :1 [1 "rgba( 160, 107, 0, 1)" true]
                    :2 [1 "rgba( 105, 158, 0, 1)" true]
                    :3 [1 "rgba( 0, 155, 0, 1)" true]
                    :4 [1 "rgba( 0, 153, 101, 1)" true]
                    :5 [1 "rgba( 128, 128, 128, 1)" true]}}
   :points [{:time_t a-t, :offset 0, :price a-p}
            {:time_t b-t, :offset 0, :price b-p}]})


(defn gann [{:keys [symbol a-p b-p a-t b-t]}]
  {:type "LineToolGannComplex"
   :id (nano-id 6) ; "Ix5dtc"
   :state {:intervalsVisibilities {:minutesFrom 1 :daysTo 366  :secondsTo 59  :hoursTo 24  :months true  :days true  :seconds true  :daysFrom 1  :secondsFrom 1
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
           :symbol symbol
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
           :interval "D"
           :visible true
           :frozen false
           :reverse false}
   :points [{:time_t a-t :offset 0 :price a-p}
            {:time_t b-t :offset 0 :price b-p}]
   :zorder 3
   :linkKey "tgEoPNzjMxMh"
   :ownerSource "pOQ6pA"
   :version 2})

(defn gann-vertical [p-0 d-p n a-t b-t]
  (into []
        (for [i (range n)]
          (gann
           {:symbol "NasdaqNM:AMZN"
            :a-p (+ p-0 (* i d-p))  :a-t a-t
            :b-p (+ p-0 (* (inc i) d-p))  :b-t b-t}))))