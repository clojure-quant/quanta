(ns demo.playground.series
  (:require
   [tablecloth.api :as tc]
   [tech.v3.dataset.print :as print]
   [ta.warehouse :as wh]
   [ta.backtest.date :refer [add-year-and-month-date-as-instant]]))

(def ds-m-y-real
  (->
   (wh/load-symbol :crypto "D" "BTCUSD")
   add-year-and-month-date-as-instant
   (tc/group-by [:month :year])
   (tc/aggregate {:min (fn [ds]
                         (->> ds
                              :close
                              (apply min)))
                  :max (fn [ds]
                         (->> ds
                              :close
                              (apply max)))})))

(def ds-m-y
  (tc/dataset
   {:min [1.0 2 3 4 5 6 7 8 9 10 11 12]
    :max [10.0 12 13 41 5 6 7 8 9 10 11 12]
    :month (map #(java.time.Month/of %) [1 2 3 4 5 6 7 8 9 10 11 12])
    :year (map #(java.time.Year/of %) [2022 2022 2023 2023
                                       2022 2022 2023 2023
                                       2022 2022 2023 2023])}))

(->
 ds-m-y
 (tc/pivot->wider :month [:min :max] {:drop-missing? false})
 (print/print-range :all))

(->
 ds-m-y
 (tc/pivot->wider :year [:min :max])
 (print/print-range :all))

ds-m-y

