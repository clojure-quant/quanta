(ns ta.helper.random
  (:import
   [java.util Random]))

; Random Variable Initialization
(def rnd (new Random))

(defn random-float
  [min max]
  (+ min (* (.nextDouble rnd) (- max min))))

(comment
  (random-float 10.0 100.0)
;
  )

(defn zero-mean-random-value []
  (- (rand 0.005) 0.0025))

(defn random-return-value []
   ;; mimicing a normal distribution
  (apply + (repeatedly 10 zero-mean-random-value)))

(comment
  (zero-mean-random-value)
  (random-return-value)
;
  )

(defn random-return-multiplyer []
  (+ 1.0 (random-return-value)))

(defn random-return-series [n]
  (take (dec n) (repeatedly random-return-multiplyer)))

(comment
  (random-return-series 5)
;
  )
(defn integrate-returns [returns]
  (into []
        (reductions * 100.0 returns)))

(comment
  (integrate-returns (random-return-series 1000))
 ;
  )
(defn random-series
  [n]
  (integrate-returns (random-return-series n)))

(comment

  (-> (random-series 100)
      count))
