(ns ta.indicator.momentum-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator :refer [macd rsi]]))

;; TESTS

(deftest macd-test
  (is (all-fuzzy= 
        (macd {:n 12 :m 26} (:close ds))
        (ta4j/close ds :MACD 12 26))))

(deftest rsi-test
  (is (all-fuzzy= 
        (rsi 2 (:close ds))
        (ta4j/close ds :RSI 2))))