(ns juan.algo.atr-prior
  (:require
   [tablecloth.api :as tc]
   [ta.indicator :refer [atr prior]]))

(defn add-atr-prior [env {:keys [atr-n]} bar-ds]
  (let [atr-vec (atr {:n atr-n} bar-ds)
        prior-vec (prior {:of :close} bar-ds)]
    (tc/add-columns bar-ds
                    {:atr atr-vec
                     :close-1 prior-vec})))



(comment
  (require '[tech.v3.dataset :as tds])
  (def ds (tds/->dataset {:low (map #(Math/sin (double %))
                                    (range 0 200 0.1))
                          :high (map inc (map #(Math/sin (double %))
                                              (range 0 200 0.1)))
                          :close (map #(Math/sin (double %))
                                      (range 0 200 0.1))}))


  ds
  (atr {:n 2} ds)


  (add-atr-prior nil {:atr-n 10} ds)

 ; 
  )