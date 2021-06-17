
(ns ta.swings.until
  ;(:require [clojure.algo.generic.math-functions :as math])
  (:import [clojure.lang PersistentQueue]))
  




(defmacro indicator
  ([trans-fn] `(indicator [] ~trans-fn))
  ([bindings trans-fn]
   `(fn [xf#]
      (let ~bindings
        (fn
          ([] (xf#))
          ([result#] (xf# result#))
          ([result# input#]
           (if-let [r# (~trans-fn input#)]
             (if (reduced? r#)
               r# (xf# result# r#))
             result#)))))))


(defn hcomp [& xfs]
  (indicator
   [ixf (fn [_ input] input)
    ts (mapv #(% ixf) xfs)]
   (fn [x]
     (mapv #(% nil x) ts))))


(defn align []
  (indicator
   (fn [x]
     (when (every? some? x) x))))

(defn ahcomp [& xfs]
  (comp (apply hcomp xfs)
        (align)))

(defn sma [p]
  (indicator
   [values (volatile! PersistentQueue/EMPTY)
    sum (volatile! 0.0)]
   (fn [x]
     (vswap! sum + x)
     (vswap! values conj x)
     (when (> (count @values) p)
       (vswap! sum - (first @values))
       (vswap! values pop))
     (when (= (count @values) p)
       (/ @sum p)))))


(into [] sma [ 4 5 6 7 8 6 5 4 3 ])