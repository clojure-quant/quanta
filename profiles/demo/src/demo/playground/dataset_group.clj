
(-> concatenated-dataset
    (tablecloth/pivot->wider :symbol :close))

(-> concatenated-dataset
    (tablecloth/pivot->wider :symbol :close)
    tablecloth/last)

(-> concatenated-dataset
    (tablecloth/group-by :symbol))

(-> concatenated-dataset
    (tablecloth/group-by :symbol)
    (print/print-policy :repl))

(-> concatenated-dataset
    (tablecloth/group-by :symbol)
    (tablecloth/random 3)
    (print/print-policy :repl))

(-> concatenated-dataset
    (tablecloth/group-by [:symbol])
    (tablecloth/random 3)
    (print/print-policy :repl))

(-> concatenated-dataset
    (tablecloth/group-by [:symbol])
    (tablecloth/random 3)
    tablecloth/grouped?)

(-> concatenated-dataset
    (tablecloth/group-by :symbol)
    (tablecloth/aggregate {:min (fn [ds]
                                  (->> ds
                                       :close
                                       (apply min)))
                           :max (fn [ds]
                                  (->> ds
                                       :close
                                       (apply max)))}))

(-> concatenated-dataset
    (tablecloth/group-by [:symbol :year])
    (tablecloth/aggregate {:min (fn [ds]
                                  (->> ds
                                       :close
                                       (apply min)))
                           :max (fn [ds]
                                  (->> ds
                                       :close
                                       (apply max)))}))

(-> concatenated-dataset
    (tablecloth/group-by [:symbol :year])
    (tablecloth/aggregate {:min (fn [ds]
                                  (->> ds
                                       :close
                                       (apply min)))
                           :max (fn [ds]
                                  (->> ds
                                       :close
                                       (apply max)))})
    (tablecloth/pivot->wider :symbol [:min :max]))

(-> concatenated-dataset
    (experiments-helpers/symbols-overview {:grouping-columns [:symbol :year :month]})
    (print/print-range :all))
