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

(comment 
  (m/?
    (first-match #(> % 3)
              (m/seed [1 2 3 4 5 6])))
  
 ; 
  )




