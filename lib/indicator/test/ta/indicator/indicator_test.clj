(ns ta.indicator.indicator-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy= nthrest-fuzzy=]]
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

(deftest test-hull-ma
  (is (nthrest-fuzzy=
       4
       (ta4j/close ds :HMA 4)
      (ind/hma 4 (:close ds)))))

(comment 
  
   (ta4j/close ds :HMA 4)
   ;; => (100.0
   ;;     100.44444444444444
   ;;     117.11111111111111
   ;;     137.42222222222222
   ;;     142.62222222222223
   ;;     147.8
   ;;     157.77777777777777
   ;;     144.1
   ;;     120.53333333333333
   ;;     120.04444444444445
   ;;     126.06666666666666
   ;;     122.33333333333333
   ;;     112.38888888888889
   ;;     101.0
   ;;     102.61111111111111)

   

   (ind/hma 4 (:close ds))


  
 ; 
  )