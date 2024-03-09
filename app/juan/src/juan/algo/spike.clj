(ns juan.algo.spike
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]))

(defn spike-signal-bar [atr-band-upper atr-band-lower close]
  (cond
    (not (or atr-band-upper atr-band-lower close)) :flat
    (> close atr-band-upper) :short
    (< close atr-band-lower) :long
    :else :flat))

(defn spike-signal [combined-ds]
  (let [{:keys [daily-atr-upper daily-atr-lower close]} combined-ds]
    (assert daily-atr-upper "spike-signal needs col :daily-atr-upper")
    (assert daily-atr-lower "spike-signal needs col :daily-atr-lower")    
    (assert close "spike-signal needs col :close")    
    (dtype/emap spike-signal-bar :keyword daily-atr-upper daily-atr-lower close)))


(comment 
  (require '[tech.v3.dataset :as tds])
  (def ds (tds/->dataset {:daily-atr-lower [1.0 2.0 3.0 4.0]
                          :daily-atr-upper [1.5 2.1 4.1 5.1]
                          :close [0.5 2.3 3.5 4.0]}))
  
  (spike-signal ds)
    
  
 ; 
  )