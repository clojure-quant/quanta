(ns ta.indicator.indicator-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds]]
            [ta.indicator :as ind]))

;; TESTS

(deftest sma-test
  (is (all-fuzzy=
       (ind/sma {:n 2} (:close ds))
       (ta4j/close ds :SMA 2))))

(deftest wma-test
  (is (all-fuzzy=
       (ind/wma 2 (:close ds))
       (ta4j/close ds :WMA 2))))

(deftest ema-test
  (is (all-fuzzy=
       (ind/ema 2 (:close ds))
       (ta4j/close ds :EMA 2))))

(deftest mma-test
  (is (all-fuzzy=
       (ind/mma 2 (:close ds))
       (ta4j/close ds :MMA 2))))

(deftest macd-test
  (is (all-fuzzy=
       (ind/macd {:n 12 :m 26} (:close ds))
       (ta4j/close ds :MACD 12 26))))

(deftest rsi-test
  (is (all-fuzzy=
       (ind/rsi 2 (:close ds))
       (ta4j/close ds :RSI 2))))

(deftest test-atr
  (is (all-fuzzy=
       0.1
       (ta4j/bar ds :ATR 4)
       (ind/atr {:n 4} ds))))