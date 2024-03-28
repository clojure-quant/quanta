(ns ta.indicator.ma-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator :refer [sma wma ema mma hl2]]))

;; TESTS

(deftest sma-test
  (is (all-fuzzy= 
        (sma {:n 2} (:close ds))
        (ta4j/close ds :SMA 2))))

(deftest wma-test
  (is (all-fuzzy= 
        (wma 2 (:close ds))
        (ta4j/close ds :WMA 2))))

(deftest ema-test
  (is (all-fuzzy= 
        (ema 2 (:close ds))
        (ta4j/close ds :EMA 2))))

(deftest mma-test
  (is (all-fuzzy= 
         (mma 2 (:close ds))
         (ta4j/close ds :MMA 2))))

(deftest hl2-test
  (is (all-fuzzy=
       (hl2 ds)
       (ta4j/bar ds :helpers/MedianPrice))))

(comment 
  (ta4j/close ds :SMA 2)
  
  
 ; 
  )