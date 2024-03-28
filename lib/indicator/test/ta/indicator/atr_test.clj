(ns ta.indicator.atr-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator :as ind]))

(deftest test-tr
  (is (all-fuzzy= 0.1
       (ta4j/bar ds :helpers/TR)
       (ind/tr ds))))

(deftest test-atr
  (is (all-fuzzy= 0.1
                  (ta4j/bar ds :ATR 4)
                  (->> (ind/atr {:n 4} ds) (into [])))))


(comment
  (ind/tr ds)
  (ta4j/bar ds :helpers/TR)
  

;  
  )
               

