(ns ta.indicator.stats-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy= nthrest-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator.rolling :as roll]
            [tech.v3.datatype.functional :as dfn]))

(deftest test-mad-2
  (is (all-fuzzy=
       (ta4j/close ds :statistics/MeanDeviation 2)
       (roll/trailing-mad 2 (:close ds)))))

(deftest test-mad-3
  (is (nthrest-fuzzy= 2
         (ta4j/close ds :statistics/MeanDeviation 3)
         (roll/trailing-mad 3 (:close ds)))))

(deftest test-mad-4
  (is (nthrest-fuzzy= 4
        (ta4j/close ds :statistics/MeanDeviation 4) 
        (roll/trailing-mad 4 (:close ds)))))

(deftest test-variance
  (is (nthrest-fuzzy= 3
         (ta4j/close ds :statistics/Variance 4)
         (roll/trailing-variance 4 (:close ds)))))


(deftest test-stddev
    (is (nthrest-fuzzy= 3
           (ta4j/close ds :statistics/StandardDeviation 3)
           (roll/trailing-stddev 3 (:close ds)))))

(comment
  (:close ds)

  (ta4j/close ds :statistics/MeanDeviation 2)
  (roll/trailing-mad 2 (:close ds))

  (ta4j/close ds :statistics/Variance 4)
  (roll/trailing-variance 4 (:close ds))

  

  (roll/trailing-stddev 3 (:close ds))
  (ta4j/close ds :statistics/StandardDeviation 3)


  


;  
  )
               

