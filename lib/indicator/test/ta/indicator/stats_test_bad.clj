(ns ta.indicator.stats-test-bad
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator.rolling :as roll]
            ))




(deftest test-stddev
  (is (all-fuzzy= 0.1
        (ta4j/close ds :statistics/StandardDeviation 4)
        (->> (roll/trailing-stddev 4 ds) (into []))
        ;(-> (ind/atr-mma {:n 4} ds) (round))
       )))


(comment 
 (roll/trailing-stddev 4 ds)
 ;; => #tech.v3.dataset.column<float64>[15]
 ;;    :out
 ;;    [0.000, 0.5000, 14.84, 18.59, 16.99, 8.500, 10.69, 12.44, 
 ;;     17.54, 16.36, 4.787, 4.787, 8.539, 11.09, 8.165]
  
 (ta4j/close ds :statistics/StandardDeviation 4)
 ;; => (0.0
 ;;     0.5
 ;;     13.912424503139471
 ;;     16.101242188104617
 ;;     14.713938969562161
 ;;     7.361215932167728
 ;;     9.256754290786809
 ;;     10.770329614269007
 ;;     15.190457530963313
 ;;     14.168627315304754
 ;;     4.14578098794425
 ;;     4.14578098794425
 ;;     7.39509972887452
 ;;     9.60143218483576
 ;;     7.0710678118654755)

  
;  
  )
               

