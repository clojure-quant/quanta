(ns demo.align)

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