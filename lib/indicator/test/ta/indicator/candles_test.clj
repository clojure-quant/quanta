(ns ta.indicator.candles-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator.candles :as indc]))


;; TESTS

#_(deftest test-doji
  (is (all-fuzzy=
       0.1
       (ta4j/bar ds :volume/VWAP 2)
        (vind/vwap 2 ds))))


(comment
  (:close ds)

   (ta4j/bar-bool ds :candles/Doji 2 1)
   ;; => (true false false true true 
   ;;  false false false true true 
  ;;  true true false true false)

  
   (indc/doji 2 1 ds)
   ;; => [false false false true true 
   ;;    false false true true false 
  ;;     true true true false false]



;
)