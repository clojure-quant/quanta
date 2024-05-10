(ns ta.indicator.indicator-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy= nthrest-fuzzy= fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds] :as data]
            [ta.indicator :as ind]
            [ta.indicator.helper :refer [nil-or-nan?]]))

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
       (ta4j/bar ds :ATR 4)
       (ind/atr {:n 4} ds))))

(deftest test-hull-ma
  (is (nthrest-fuzzy=
       4
       (ta4j/close ds :HMA 4)
      (ind/hma 4 (:close ds)))))

(deftest lma-test
  (is (nthrest-fuzzy=
        0.00000001
        100
        (:lma data/ind-100-export-ds)
        (ind/lma 100 (:close data/ind-100-export-ds)))))

(deftest chebyshev1-test
  (is (nthrest-fuzzy=
        0.00000001
        110
        (vec (:chebyshev1 data/ind-100-export-ds))
        (ind/chebyshev1 100 (:close data/ind-100-export-ds)))))

(deftest chebyshev2-test
  (is (all-fuzzy=
        0.0001
        (vec (:chebyshev2 data/ind-100-export-ds))
        (ind/chebyshev2 100 (:close data/ind-100-export-ds)))))

(deftest ehlers-gaussian-test
  (is (nthrest-fuzzy=
        0.00000001
        150
        (vec (:ehlers-gaussian data/ind-100-export-ds))
        (ind/ehlers-gaussian 100 (:close data/ind-100-export-ds)))))

(deftest ehlers-supersmoother-test
  (is (nthrest-fuzzy=
        0.00000001
        250
        (vec (:ehlers-supersmoother data/ind-100-export-ds))
        (ind/ehlers-supersmoother 100 (:close data/ind-100-export-ds)))))

(deftest arma-test
  (testing "arma"
    (is (nthrest-fuzzy=
          0.00000001
          26
          (vec (:arma14-3 data/arma-bybit-export-ds))
          (ind/arma 14 (:close data/arma-bybit-export-ds) 3)))
    (is (nthrest-fuzzy=
          0.00000001
          26
          (vec (:arma14-2 data/arma-bybit-export-ds))
          (ind/arma 14 (:close data/arma-bybit-export-ds) 2)))
    (is (nthrest-fuzzy=
          0.00000001
          38
          (vec (:arma20-3 data/arma-bybit-export-ds))
          (ind/arma 20 (:close data/arma-bybit-export-ds) 3)))
    (is (nthrest-fuzzy=
          0.00000001
          58
          (vec (:arma30-2 data/arma-bybit-export-ds))
          (ind/arma 30 (:close data/arma-bybit-export-ds) 2)))))

(deftest a2rma-test
  (testing "a2rma"
    (is (nthrest-fuzzy=
          0.01
          14
          (vec (:a2rma14-3 data/a2rma-bybit-export-ds))
          (ind/a2rma 14 (:close data/a2rma-bybit-export-ds) 3)))
    (is (nthrest-fuzzy=
          0.001
          14
          (vec (:a2rma14-2 data/a2rma-bybit-export-ds))
          (ind/a2rma 14 (:close data/a2rma-bybit-export-ds) 2)))
    (is (nthrest-fuzzy=
          0.001
          20
          (vec (:a2rma20-3 data/a2rma-bybit-export-ds))
          (ind/a2rma 20 (:close data/a2rma-bybit-export-ds) 3)))
    (is (nthrest-fuzzy=
          0.001
          30
          (vec (:a2rma30-2 data/a2rma-bybit-export-ds))
          (ind/a2rma 30 (:close data/a2rma-bybit-export-ds) 2)))))

;

(comment 
  
   (ta4j/close ds :HMA 4)
   (ind/hma 4 (:close ds))

   (defn print-diff [l1 l2 tol]
     (doseq [i (range (count l1))]
       (let [v1 (nth l1 i)
             v2 (nth l2 i)]
         (if (or (nil-or-nan? v1) (nil-or-nan? v2))
           (when (not= v1 v2)
             (println "!= at index:" i ", v1:" v1 "v2:" v2))
           (when (not (fuzzy= tol v1 v2))
             (println "!= at index:" i ", v1:" v1 "v2:" v2 "diff" (- v1 v2)))))))


   (print-diff (drop 10 (:lma data/ind-100-export-ds))
               (ind/lma 100 (drop 10 (:close data/ind-100-export-ds)))
               0.00000001)

   (print-diff (:chebyshev1 data/ind-100-export-ds)
               (ind/chebyshev1 100 (:close data/ind-100-export-ds))
               0.00000001)

   (print-diff (:chebyshev2 data/ind-100-export-ds)
               (ind/chebyshev2 100 (:close data/ind-100-export-ds))
               0.0001)

   (print-diff (:ehlers-supersmoother data/ind-100-export-ds)
               (ind/ehlers-supersmoother 100 (:close data/ind-100-export-ds))
               0.00000001)

   (print-diff (:ehlers-gaussian data/ind-100-export-ds)
               (ind/ehlers-gaussian 100 (:close data/ind-100-export-ds))
               0.00000001)


   (print-diff (:arma14-3 data/arma-bybit-export-ds)
               (ind/arma 14 (:close data/arma-bybit-export-ds) 3)
               0.00000001)

   (print-diff (:arma20-3 data/arma-bybit-export-ds)
               (ind/arma 20 (:close data/arma-bybit-export-ds) 3)
               0.00000001)

   (print-diff (:a2rma14-3 data/a2rma-bybit-export-ds)
               (ind/a2rma 14 (:close data/a2rma-bybit-export-ds) 3)
               0.01)

   (print-diff (:a2rma20-3 data/a2rma-bybit-export-ds)
               (ind/a2rma 20 (:close data/a2rma-bybit-export-ds) 3)
               0.001)

   (nth (:close ds) 0)

   (vec (:lma data/ind-100-export-ds))
   (vec (ind/lma 100 (:close data/ind-100-export-ds)))
   (ind/ehlers-supersmoother 100 (:close data/ind-100-export-ds))
   (ind/ehlers-gaussian 100 (:close data/ind-100-export-ds))
   (ind/a2rma 14 (:close data/arma-bybit-export-ds) 3)
 ; 
  )