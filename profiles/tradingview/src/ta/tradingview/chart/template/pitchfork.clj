(ns ta.tradingview.chart.template.pitchfork)

(def pitchfork
  {:type "LineToolPitchfork"
   :id "xy6qRv"
   :linkKey "IaufLUGXhE6H"
   :ownerSource "pOQ6pA"
   :zorder -1
   :state {:symbol "NasdaqNM:AMZN"
           :lastUpdateTime 1636558442000
           :clonable true
           :frozen false
           :visible true
           :interval "D"
           :_isActualInterval true
           :intervalsVisibilities {:minutesFrom 1, :daysTo 366, :secondsTo 59, :hoursTo 24, :months true, :days true, :seconds true, :daysFrom 1, :secondsFrom 1, :hours true, :ranges true, :hoursFrom 1, :minutes true, :minutesTo 59, :weeks true}
           :level0 [0.25 "rgba( 160, 107, 0, 1)" false 0 1]
           :level1 [0.382 "rgba( 105, 158, 0, 1)" false 0 1]
           :level2 [0.5 "rgba( 0, 155, 0, 1)" true 0 1]
           :level3 [0.618 "rgba( 0, 153, 101, 1)" false 0 1]
           :level4 [0.75 "rgba( 0, 101, 153, 1)" false 0 1]
           :level5 [1 "rgba( 0, 0, 153, 1)" true 0 1]
           :level6 [1.5 "rgba( 102, 0, 153, 1)" false 0 1]
           :level7 [1.75 "rgba( 153, 0, 102, 1)" false 0 1]
           :level8 [2 "rgba( 165, 0, 0, 1)" false 0 1]
           :median {:visible true, :color "rgba( 165, 0, 0, 1)", :linewidth 1, :linestyle 0}
           :fillBackground true
           :style 0
           :transparency 80}
   :points [{:time_t 1514903400, :offset 0, :price 1383.1908446757407}
            {:time_t 1517581800, :offset 0, :price 1523.6126711861293}
            {:time_t 1516113000, :offset 0, :price 1294.0812396146428}]})
