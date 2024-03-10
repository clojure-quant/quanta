(ns notebook.datascience.dataset-random
  (:require
   [tablecloth.api :as tc]
   [tick.core :as tick]
   [ta.backtest.date :refer [days-ago select-rows-since]]
   [ta.db.bars.random :refer [random-dataset random-datasets]]))

(random-dataset 3)

(-> (random-dataset 1000)
    (select-rows-since (days-ago 6)))

(def ds-1 (random-dataset 1000000))

(let [date (days-ago 6)]
  (-> ds-1
      (tc/select-rows
       (fn [row]
         (-> row
             :date
             (tick/>= date))))
      time))

(def datasets (random-datasets 12 1000))
(count datasets)
(first datasets)

; tech.v3.dataset.FastStruct
(-> (random-dataset 1000)
    (tc/rows :as-maps)
    first
    type)


