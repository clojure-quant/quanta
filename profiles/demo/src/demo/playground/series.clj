(ns demo.playground.series
  (:require
   [taoensso.timbre :refer [trace debug info infof error]]
   [clojure.pprint :refer [print-table]]
   [tech.v3.dataset :as tds]
   [tech.v3.dataset.print :refer [print-range]]
   [tablecloth.api :as tablecloth]
   [ta.warehouse :as wh]
   [ta.dataset.date :refer [add-year-and-month-date-as-instant]]
   [demo.env.config :as c]))

(->
 (wh/load-symbol c/w-crypto "D" "BTCUSD")
 add-year-and-month-date-as-instant
 (tablecloth/group-by [:month :year])
 (tablecloth/aggregate {:min (fn [ds]
                               (->> ds
                                    :close
                                    (apply min)))
                        :max (fn [ds]
                               (->> ds
                                    :close
                                    (apply max)))}))

(->
 (wh/load-symbol c/w-crypto "D" "BTCUSD")
 add-year-and-month-date-as-instant
 (tablecloth/group-by [:month :year])
 (tablecloth/aggregate {:min (fn [ds]
                               (->> ds
                                    :close
                                    (apply min)))
                        :max (fn [ds]
                               (->> ds
                                    :close
                                    (apply max)))})
 (tablecloth/pivot->wider :month [:min :max])
 (print-range :all))

; this does not work
(->
 (wh/load-symbol c/w-crypto "D" "BTCUSD")
 add-year-and-month-date-as-instant
 (tablecloth/group-by [:month :year])
 (tablecloth/aggregate {:min (fn [ds]
                               (->> ds
                                    :close
                                    (apply min)))
                        :max (fn [ds]
                               (->> ds
                                    :close
                                    (apply max)))})
 (tablecloth/pivot->wider :year [:min :max])
 (print-range :all))