(ns quanta.market.broker.util
  (:require
   [missionary.core :as m])
  (:import [missionary Cancelled]))

(defn first-match [predicate flow]
  (m/reduce (fn [_r v]
              (when (predicate v)
                (reduced v)))
            nil
            flow))

(defn next-value [flow]
  (first-match #(not (nil? %)) flow))

(defn always [flow]
  (m/reduce (fn [_r v]
              v)
            nil
            flow))

(defn current-value [flow]
  ; flows dont implement deref
  (m/eduction (take 1) flow))

(defn current-value-task [flow]
  (m/reduce (fn [_r v]
              v) nil
            (current-value flow)))

(comment
  (m/?
   (first-match #(> % 3)
                (m/seed [1 2 3 4 5 6])))

  (m/? (m/reduce println nil
                 (current-value (m/seed [1 2 3]))))

  (m/? (current-value-task (m/seed [1 2 3])))

; 
  )




