(ns demo.playground.dataset-group
  (:require
   [tablecloth.api :as tc]
   [tech.v3.dataset.print :as print]
   [ta.warehouse.random :refer [random-datasets]]
   [ta.warehouse.overview :refer [concatenate-datasets overview-view]]))

(def concatenated-dataset
  (-> (random-datasets 3 10)
      (concatenate-datasets)))

concatenated-dataset

(-> concatenated-dataset
    (tc/pivot->wider :symbol :close))

(-> concatenated-dataset
    (tc/pivot->wider :symbol :close)
    tc/last)

(-> concatenated-dataset
    (tc/group-by :symbol))

(-> concatenated-dataset
    (tc/group-by :symbol)
    (print/print-policy :repl))

(-> concatenated-dataset
    (tc/group-by :symbol)
    (tc/random 3)
    (print/print-policy :repl))

(-> concatenated-dataset
    (tc/group-by [:symbol])
    (tc/random 3)
    tc/grouped?)

(-> concatenated-dataset
    (tc/group-by :symbol)
    (tc/aggregate {:min (fn [ds]
                          (->> ds
                               :close
                               (apply min)))
                   :max (fn [ds]
                          (->> ds
                               :close
                               (apply max)))}))

(-> concatenated-dataset
    (tc/group-by [:symbol :year])
    (tc/aggregate {:min (fn [ds]
                          (->> ds
                               :close
                               (apply min)))
                   :max (fn [ds]
                          (->> ds
                               :close
                               (apply max)))}))

(-> concatenated-dataset
    (tc/group-by [:symbol :year])
    (tc/aggregate {:min (fn [ds]
                          (->> ds
                               :close
                               (apply min)))
                   :max (fn [ds]
                          (->> ds
                               :close
                               (apply max)))})
    (tc/pivot->wider :symbol [:min :max]))

(-> concatenated-dataset
    (overview-view {:grouping-columns [:symbol :year :month]})
    (print/print-range :all))
