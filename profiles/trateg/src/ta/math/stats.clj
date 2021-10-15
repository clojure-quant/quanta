(ns ta.math.stats
  (:require
   [tech.v3.datatype.functional :as fun]
   [tech.v3.datatype :as dtype]))


(defn standardize [xs]
  (-> xs
      (fun/- (fun/mean xs))
      (fun// (fun/standard-deviation xs))))

(defn rand-numbers [n]
  (dtype/clone
   (dtype/make-reader :float32 n (rand))))
