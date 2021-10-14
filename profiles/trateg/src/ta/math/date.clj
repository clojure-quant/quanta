



(defn dt-now []
  (t/date (t/now)))

(defn random-ts [size]
  (let [pseries (random-series size)
        last (dt-now)]
    (reverse (map-indexed (fn [i v]
                   ;(println i v)
                            (let [dt (t/- last (t/new-period (inc i) :days))]
                              {:date  dt
                               :close v}))
                          (reverse pseries)))))
