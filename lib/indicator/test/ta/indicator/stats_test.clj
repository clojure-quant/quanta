(ns ta.indicator.stats-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator.rolling :as roll]
            [tech.v3.datatype.functional :as dfn]))

(deftest test-mad-2
  (is (all-fuzzy=
       (ta4j/close ds :statistics/MeanDeviation 2)
       (roll/trailing-mad 2 (:close ds)))))

(deftest test-mad-3
  (is (all-fuzzy=
       (-> (ta4j/close ds :statistics/MeanDeviation 3) rest rest)
       (-> (roll/trailing-mad 3 (:close ds)) rest rest))))

(deftest test-mad-4
  (is (all-fuzzy=
       (-> (ta4j/close ds :statistics/MeanDeviation 4) rest rest rest)
       (-> (roll/trailing-mad 4 (:close ds)) rest rest rest))))



#_(deftest test-stddev
    (is (all-fuzzy= 0.1
                    (ta4j/close ds :statistics/StandardDeviation 4)
                    (->> (roll/trailing-stddev 4 ds)  (into []))
        ;(-> (ind/atr-mma {:n 4} ds) (round))
                    )))
(comment
  (ta4j/close ds :statistics/MeanDeviation 2)
  (roll/trailing-mad 2 (:close ds))

  (ta4j/close ds :statistics/Variance 2)
  ;; => (0.0 0.25 193.55555555555554 259.25 216.5 54.1875 85.6875 116.0 230.75 200.75 17.1875 17.1875 54.6875 92.1875 50.0)

  (roll/trailing-variance 2 (:close ds))

  (roll/trailing-variance-population 4 (:close ds))
 
  

  (ta4j/close ds :statistics/StandardDeviation 4)
  



 (ta4j/close ds :statistics/StandardDeviation 4)
  (roll/trailing-stddev 4 ds)


;  
  )
               

