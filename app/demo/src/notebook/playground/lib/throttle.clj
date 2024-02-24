(ns notebook.playground.lib.throttle
  (:require
   [throttler.core]))

(def plust (throttler.core/throttle-fn + 5 :minute))

  ; this should be fast
(time
 (map #(plust 1 %) (range 2)))

; shouldtake a minute
(time
 (map #(plust 1 %) (range 7)))