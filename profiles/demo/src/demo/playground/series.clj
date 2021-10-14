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

(def ds-m-y
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
                                      (apply max)))})))

(def ds-m-y
  (tablecloth/dataset
   {:min [1.0 2 3 4 5 6 7 8 9 10 11 12]
    :max [10.0 12 13 41 5 6 7 8 9 10 11 12]
    :month (map #(java.time.Month/of %) [1 2 3 4 5 6 7 8 9 10 11 12])
    :year (map #(java.time.Year/of %) [2022 2022 2023 2023
                                       2022 2022 2023 2023
                                       2022 2022 2023 2023])}))

(->
 ds-m-y
 (tablecloth/pivot->wider :month [:min :max])
 (print-range :all))

(->
 ds-m-y
 (tablecloth/pivot->wider :year [:min :max])
 (print-range :all))

ds-m-y

; calculate via map

(defn calc-add [close upper lower]
  (+ close upper lower))

(dtype/emap calc-add :float64
            (:a d) (:b d) (:c d))