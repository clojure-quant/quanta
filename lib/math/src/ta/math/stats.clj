(ns ta.math.stats
  (:require
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.datatype :as dtype]))

(defn mean [coll]
  (/ (reduce + coll) (count coll)))


(defn variance-sample [coll]
  (let [avg     (mean coll)
        squares (map #(Math/pow (- % avg) 2) coll)]
    (-> (reduce + squares)
        (/ (dec (count coll))))))

(defn variance-population [coll]
  (let [avg     (mean coll)
        squares (map #(Math/pow (- % avg) 2) coll)]
    (-> (reduce + squares)
        (/ (count coll))
        Math/sqrt)))

;;for sample (not population)
(defn standard-deviation [coll]
  (let [avg     (mean coll)
        squares (map #(Math/pow (- % avg) 2) coll)]
    (-> (reduce + squares)
        (/ (dec (count coll)))
        Math/sqrt)))

(defn standard-deviation-population [coll]
  (let [avg     (mean coll)
        squares (map #(Math/pow (- % avg) 2) coll)]
    (-> (reduce + squares)
        (/  (count coll))
        Math/sqrt)))



(defn standardize [xs]
  (-> xs
      (dfn/- (dfn/mean xs))
      (dfn// (dfn/standard-deviation xs))))

(defn rand-numbers [n]
  (dtype/clone
   (dtype/make-reader :float32 n (rand))))


(defn mad 
  "mad = mean absolute deviation.
   the mean of the absolute deviation of the mean.
   more applicable to reality than the standard deviation"
  [vec]
  (let [m (dfn/mean vec)
        ad (-> (dfn/- vec m) (dfn/abs))]
    (dfn/mean ad)))

(comment 
  (mad [1 2 3]) 
  ; mean = 2.
  ; diffs: 1 0 1
  ; mad (2/3)
  
  (dfn/variance [1 2 3 4 5])
  (variance-sample [1 2 3 4 5])
  (variance-population [1 2 3 4 5])

  
  (dfn/standard-deviation [1 2 3 4 5])
  (standard-deviation [1 2 3 4 5])
  (standard-deviation-population [1 2 3 4 5])
 

;
  )
  