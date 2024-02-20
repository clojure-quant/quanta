(ns ta.math.stats
  (:require
   [tech.v3.datatype.functional :as fun]
   [tech.v3.datatype :as dtype]))

(defn mean [coll]
  (/ (reduce + coll) (count coll)))

;;for sample (not population)
(defn standard-deviation [coll]
  (let [avg     (mean coll)
        squares (map #(Math/pow (- % avg) 2) coll)]
    (-> (reduce + squares)
        (/ (dec (count coll)))
        Math/sqrt)))

(defn standardize [xs]
  (-> xs
      (fun/- (fun/mean xs))
      (fun// (fun/standard-deviation xs))))

(defn rand-numbers [n]
  (dtype/clone
   (dtype/make-reader :float32 n (rand))))
