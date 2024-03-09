(ns juan.algo.daily
  (:require
   [tablecloth.api :as tc]
   [ta.indicator :as ind]
   [juan.algo.pivot-price :refer [add-pivots-price]]))


(defn daily
  "we calculate daily atr-level, prior-close, pivots-price, pivots-volume"
  [env opts forex-bar-ds]
  (->> forex-bar-ds
       (ind/add-atr-band opts)
       (add-pivots-price env opts)))

(comment
  (require '[tech.v3.dataset :as tds])
  (def ds (tds/->dataset {:close (map #(Math/sin (double %))
                                      (range 0 200 0.1))
                          :high (map #(+ % 1.0 (rand 5.0)) (map #(Math/sin (double %))
                                              (range 0 200 0.1)))
                          :low (map #(Math/sin (double %))
                                    (range 0 200 0.1))
                          :date (range 0 200 0.1)}))

  ds
  (ind/tr ds)
  (ind/atr {:n 2} ds)
    
  (ind/add-atr-band {:atr-n 10 :atr-m 0.3} ds)

  (daily nil {:atr-n 5} ds)


    ; 
  )