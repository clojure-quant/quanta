(ns ta.indicator.helper-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator :as ind]))

;; TESTS

(deftest test-tr
  (is (all-fuzzy=
       0.1
       (ta4j/bar ds :helpers/TR)
       (ind/tr ds))))

(deftest hl2-test
  (is (all-fuzzy=
       (ind/hl2 ds)
       (ta4j/bar ds :helpers/MedianPrice))))

(deftest hlc3-test
  (is (all-fuzzy=
       (ind/hlc3 ds)
       (ta4j/bar ds :helpers/TypicalPrice))))

(comment
  (ta4j/bar ds :helpers/MedianPrice)
  (ta4j/bar ds :helpers/TypicalPrice)

  (ind/tr ds)
  (ta4j/bar ds :helpers/TR)



; 
  )