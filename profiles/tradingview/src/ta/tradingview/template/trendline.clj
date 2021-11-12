(ns ta.tradingview.template.trendline
  (:require
   [nano-id.core :refer [nano-id]]))

(defn trendline [{:keys [symbol a-p b-p a-t b-t]}]
  {:type "LineToolTrendLine"
   :id (nano-id 6)
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
           :fixedSize false
           }
   :points [{:time_t a-t, :offset 0, :price a-p}
            {:time_t b-t, :offset 0, :price b-p}]})