(ns ta.dataframe.align)

(defn align [calendar instruments]
  ;(let [trigger? (fn [_] (> (rand) 0.5))]
  (reduce
   (fn [acc date]
     (assoc acc date (into {}
                           (for [[ticker bars] instruments
                                 :let [bar (get bars date)]
                                 :when bar]
                             [ticker bar ; (assoc bar :trigger? (trigger? (:close bar)))
                              ]))))
   {}
   calendar));)

(defn series->indexed [series]
  (reduce (fn [r row]
            (assoc r (:date row) (dissoc row :date)))
          {} series))

(defn indexed->series [calendar indexed]
  (map (fn [date] (assoc (get indexed date) :date date)) calendar))

(defn load-aligned
  "use fn-load-bars to load series for all symbols
   each row is a map, with keys being the symbols and
   values the loaded series data for that time
   only those indices in the calendar are returned
   calendar ordering is obeyed"
  ([fn-load-bars symbols calendar]
   (let [load->indexed (comp series->indexed fn-load-bars)
         instruments (reduce (fn [r symbol]
                               (assoc r symbol (load->indexed symbol))) {} symbols)]
     (->> (align calendar instruments)
          (indexed->series calendar))))
  ([fn-load-bars symbols] ; take calendar from first symbol
   (load-aligned fn-load-bars symbols
                 (map :date (fn-load-bars (first symbols))))))

(comment

  (def instruments {:spy {"2020-01-01" {:close 10}
                          "2020-01-02" {:close 11}
                          "2020-01-03" {:close 11}}
                    :iwm {"2020-01-02" {:close 11}
                          "2020-01-03" {:close 11}}})

  (def calendar ["2020-01-01" "2020-01-02" "2020-01-03"])

  (align calendar instruments)

  (defn load-test [symbol]
    (case symbol
      :A [{:date :d1 :close 1}
          {:date :d2 :close 2}
          {:date :d3 :close 3}]
      :B [{:date :d1 :close 7}
          {:date :d3 :close 9}]
      :C [{:date :d1 :close 5}
          {:date :d3 :close 6}]))

  (series->indexed (load-test :A))
  (load-aligned load-test [:A :B :C] [:d1 :d2 :d3])
  (load-aligned load-test [:A :B :C])
  ; comment end
  )
