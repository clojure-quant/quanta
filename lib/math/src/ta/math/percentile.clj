(ns ta.math.percentile
  (:require
   [com.stuartsierra.frequencies :as freq]))



(defn percentile [percentile xs]
  (let [freq-map (frequencies xs)
        stats (freq/stats freq-map :percentiles [percentile])]
    (get-in stats [:percentiles percentile])))