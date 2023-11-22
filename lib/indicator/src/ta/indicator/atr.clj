(ns ta.indicator.atr
  (:require 
    [tech.v3.datatype.functional :as fun]))



(defn atr [ds]
  (let [low (:low ds)
        high (:high ds)
        hl (fun/- high low)]
    (fun/mean hl)))


