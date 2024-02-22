(ns juan.algo.daily
  (:require 
     [juan.algo.atr-prior :refer [add-atr-prior]]
     [juan.algo.pivot-price :refer [add-pivots-price]]))

(defn daily 
  "we calculate daily atr-level, prior-close, pivots-price, pivots-volume"
  [env opts forex-bar-ds]
 (->> forex-bar-ds
     (add-atr-prior env opts)
     (add-pivots-price env opts)))



   (comment
     (require '[tech.v3.dataset :as tds])
     (def ds (tds/->dataset {:close (map #(Math/sin (double %))
                                         (range 0 200 0.1))
                             :high (map inc (map #(Math/sin (double %))
                                                 (range 0 200 0.1)))
                             :low (map #(Math/sin (double %))
                                       (range 0 200 0.1))
                             :date (range 0 200 0.1)}))
   
     ds
   
     (daily nil {:atr-n 5} ds)
   
    
    ; 
     )