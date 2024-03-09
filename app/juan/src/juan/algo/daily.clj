(ns juan.algo.daily
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as fun]
   [ta.indicator :refer [atr prior]]
   [juan.algo.pivot-price :refer [add-pivots-price]]))


(defn add-atr-prior [env {:keys [atr-n spike-atr-prct-min]
                          :or {atr-n 10 
                               spike-atr-prct-min 0.8}} bar-ds]
  (let [atr-vec (atr {:n atr-n} bar-ds)
        atr-prct-vec (fun/* atr-vec spike-atr-prct-min)
        prior-vec (prior {:of :close} bar-ds)
        spike-upper (fun/+ prior-vec atr-prct-vec)
        spike-lower (fun/- prior-vec atr-prct-vec)
        ]
    (tc/add-columns bar-ds
                    {:atr atr-vec
                     :close-1 prior-vec
                     :spike-upper spike-upper
                     :spike-lower spike-lower
                     })))


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
  (atr {:atr-n 2} ds)
    
  (add-atr-prior nil {:atr-n 10} ds)


  (daily nil {:atr-n 5} ds)


    ; 
  )