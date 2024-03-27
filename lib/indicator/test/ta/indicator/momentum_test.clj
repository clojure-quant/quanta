(ns ta.indicator.momentum-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator :refer [macd rsi]]))

;; TESTS

(deftest macd-test
  (let [macd-ds (map double (macd {:n 12 :m 26} (:close ds)))
        macd-ta4j (ta4j/close ds :MACD 12 26)]
    (is (all-fuzzy= macd-ds macd-ta4j))))

(deftest rsi-test
  (let [rsi-ds (map double (rsi 2 (:close ds)))
        rsi-ta4j (ta4j/close ds :RSI 2)]
    (is (all-fuzzy= rsi-ds rsi-ta4j))))