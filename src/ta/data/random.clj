(ns ta.data.random
  (:import
   [java.util Random]))

; Random Variable Initialization
(def rnd (new Random))

(defn random-float
  [min max]
  (+ min (* (.nextDouble rnd) (- max min))))

(defn random-series
  "creates a random series starting at 10, each bar changing randomly
   [-1 .. +1] "
  [size]
  (into []
        (reductions + 10
                    (take (- size 1) (repeatedly #(random-float -1.0 1.0))))))

(comment
  (random-float -100 100)
  (repeatedly 3 #(random-float -3.0 3.0))

  (random-series 1000)
  (count (random-series 100)))