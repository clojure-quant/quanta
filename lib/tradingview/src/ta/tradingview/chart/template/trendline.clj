(ns ta.tradingview.chart.template.trendline)

(def trendline
  {:type "LineToolTrendLine"
   :ownerSource "pOQ6pA"
   :linkKey "IPJgHK9obb7d"
   :zorder 2
   :state {:symbol symbol
           :interval "D"
           :lastUpdateTime 0
           :clonable true
           :visible true
           :frozen false
           :_isActualInterval true
           ; specific to trendline
           :linewidth 1
           :intervalsVisibilities {:minutesFrom 1, :daysTo 366, :secondsTo 59, :hoursTo 24, :months true, :days true, :seconds true, :daysFrom 1, :secondsFrom 1, :hours true, :ranges true, :hoursFrom 1, :minutes true, :minutesTo 59, :weeks true}
           :bold false,
           :linecolor "rgba( 21, 153, 128, 1)"
           :showMiddlePoint false,
           :leftEnd 0,
           :extendRight false
           :rightEnd 0
           :showPriceRange false
           :alwaysShowStats false,
           :snapTo45Degrees true
           :showBarsRange false
           :font "Verdana",
           :textcolor "rgba( 21, 119, 96, 1)"
           :linestyle 0,
           :showDistance false,
           :showAngle false
           :fontsize 12,
           :statsPosition 2
           :italic false
           :showDateTimeRange false,
           :extendLeft false
           :fixedSize false}
   :points [{:time_t 0, :offset 0, :price 0}
            {:time_t 0, :offset 0, :price 0}]})