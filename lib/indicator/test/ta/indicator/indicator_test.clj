(ns ta.indicator.indicator-test
  (:require [clojure.test :refer :all]
            [ta.indicator.util.fuzzy :refer [all-fuzzy= nthrest-fuzzy= fuzzy=]]
            [ta.indicator.util.ta4j :as ta4j]
            [ta.indicator.util.data :refer [ds ind-100-export-ds arma-export-ds]]
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
        (:lma ind-100-export-ds)
        (ind/lma 100 (:close ind-100-export-ds)))))

(deftest chebyshev1-test
  (is (nthrest-fuzzy=
        0.00000001
        110
        (vec (:chebyshev1 ind-100-export-ds))
        (ind/chebyshev1 100 (:close ind-100-export-ds)))))

(deftest chebyshev2-test
  (is (all-fuzzy=
        0.0001
        (vec (:chebyshev2 ind-100-export-ds))
        (ind/chebyshev2 100 (:close ind-100-export-ds)))))

(deftest ehlers-gaussian-test
  (is (nthrest-fuzzy=
        0.00000001
        150
        (vec (:ehlers-gaussian ind-100-export-ds))
        (ind/ehlers-gaussian 100 (:close ind-100-export-ds)))))

(deftest ehlers-supersmoother-test
  (is (nthrest-fuzzy=
        0.00000001
        250
        (vec (:ehlers-supersmoother ind-100-export-ds))
        (ind/ehlers-supersmoother 100 (:close ind-100-export-ds)))))

;

(comment 
  
   (ta4j/close ds :HMA 4)
   (ind/hma 4 (:close ds))

   (defn print-diff [l1 l2 tol]
     (doseq [i (range (count l1))]
       (let [v1 (nth l1 i)
             v2 (nth l2 i)]
         (if (not (fuzzy= tol v1 v2))
           (println "!= at index:" i ", v1:" v1 "v2:" v2 "diff" (- v1 v2))))))


   (print-diff (drop 10 (:lma ind-100-export-ds))
               (ind/lma 100 (drop 10 (:close ind-100-export-ds)))
               0.00000001)

   (print-diff (:chebyshev1 ind-100-export-ds)
               (ind/chebyshev1 100 (:close ind-100-export-ds))
               0.00000001)

   (print-diff (:chebyshev2 ind-100-export-ds)
               (ind/chebyshev2 100 (:close ind-100-export-ds))
               0.0001)

   (print-diff (:ehlers-supersmoother ind-100-export-ds)
               (ind/ehlers-supersmoother 100 (:close ind-100-export-ds))
               0.00000001)

   (print-diff (:ehlers-gaussian ind-100-export-ds)
               (ind/ehlers-gaussian 100 (:close ind-100-export-ds))
               0.00000001)

   (ind/arma 3 (:close ds) 3)
   (ind/a2rma 3 (:close ds) 3)

   (print-diff (:arma14 arma-export-ds)
               (ind/arma 14 (:close arma-export-ds) 3)
               0.00000001)

   (print-diff (:arma20 arma-export-ds)
               (ind/arma 20 (:close arma-export-ds) 2.5)
               0.00000001)

   (print-diff (:a2rma-14 arma-export-ds)
               (ind/a2rma 14 (:close arma-export-ds) 3)
               0.00000001)

   (print-diff (:a2rma-20 arma-export-ds)
               (ind/a2rma 20 (:close arma-export-ds) 2.5)
               0.00000001)

   (nth (:close ds) 0)

   (vec (:lma ind-100-export-ds))
   (vec (ind/lma 100 (:close ind-100-export-ds)))
   (ind/ehlers-supersmoother 100 (:close ind-100-export-ds))
   (ind/ehlers-gaussian 100 (:close ind-100-export-ds))
 ; 
  )