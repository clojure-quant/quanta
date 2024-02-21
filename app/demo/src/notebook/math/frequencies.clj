(ns notebook.math.frequencies
  (:require
   [com.stuartsierra.frequencies :as freq]
   [ta.math.percentile :refer [percentile]]))


(def example-sequence
  (repeatedly 10000 #(rand-int 500)))

(println (first example-sequence))

(frequencies example-sequence)

(def freq-map (frequencies example-sequence))

freq-map

(freq/stats freq-map)

(freq/stats freq-map :percentiles [10 20 80 90])
(freq/stats freq-map :percentiles [80])




(percentile 80 example-sequence)

(percentile 50 example-sequence)
