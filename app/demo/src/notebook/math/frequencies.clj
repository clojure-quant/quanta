(ns notebook.math.frequencies
  (:require 
   [com.stuartsierra.frequencies :as freq]))


  (def example-sequence 
    (repeatedly 10000 #(rand-int 500)))

  (println (first example-sequence))

  (freq/frequencies example-sequence)

  (def freq-map (frequencies example-sequence))

  freq-map

  (freq/stats freq-map)

  (freq/stats freq-map :percentiles [10 20 80 90])) ;**********************************************