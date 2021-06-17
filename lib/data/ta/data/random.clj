(ns ta.data.random
  (:require [ta.swings.date :refer [dt-now]]
            [tick.alpha.api :as t])
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

(defn random-walk
  "Returns a lazy seq of new values randomly adjusted from x
   with a slight upward bias."
  [x]
  (lazy-seq
   (cons x (random-walk
            (* x (- 1.026
                      ;; mimicing a normal distribution
                    (apply + (repeatedly 10 #(rand 0.005)))))))))


(defn random-ts [size]
  (let [pseries (random-series size)
        last (dt-now)]
    (reverse (map-indexed (fn [i v]
                   ;(println i v)
                            (let [dt (t/- last (t/new-period (inc i) :days))]
                              {:date  dt
                               :close v}))
                          (reverse pseries)))))



(defn process-until [xf source]
  (let [r (atom nil)
        d (atom (first (take 1 source)))
        s (atom (rest source))
        before? (fn [dt]
                  ;(println "before: " (:date @d))
                  (and @d (t/<= (:date @d) dt)))
        set-r (fn [& [R d]]
                ;(println "set R: " R " d:" d)
                (when d
                  ;(println "d: " d)
                  (reset! r d)))
        x (xf set-r)]
    (x)
    (fn [dt]
      (if dt
        (do (while (before? dt)
              ;(println "process: " @d)
              (x @r @d)
              (reset! d (first (take 1 @s)))
              (reset! s (rest @s))) 
            @r)
        @r))))


(comment
  (random-float -100 100)
  (repeatedly 3 #(random-float -3.0 3.0))

  (random-series 10)
  (count (random-series 100))


  (random-ts 3))