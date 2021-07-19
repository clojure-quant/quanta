(ns junk.stats)

(ns learning.math.stats
  (:require [com.stuartsierra.frequencies :as freq]))

(comment ;**********************************************

  (defn example-sequence []
    (repeatedly 10000 #(rand-int 500)))

  (println (first example-sequence))

  (frequencies example-sequence)

  (def freq-map (frequencies (example-sequence)))

  (freq/stats freq-map)

  (freq/stats freq-map :percentiles [10 20 80 90])) ;**********************************************