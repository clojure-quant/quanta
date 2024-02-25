(ns juan.algo.spike
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]))

(defn spike-signal-bar [spike-atr-prct-min daily-atr intraday-change]
  (let [spike-atr-prct (when (and intraday-change daily-atr)
                         (/ (* 100.0 intraday-change) daily-atr))
        spike-signal (when (and spike-atr-prct spike-atr-prct-min)
                       (cond
                         (> spike-atr-prct spike-atr-prct-min) :short
                         (< spike-atr-prct (- 0 spike-atr-prct-min)) :long
                         :else :flat))]
    spike-signal))

(defn spike-signal [spec combined-ds]
  (let [spike-atr-prct-min (:spike-atr-prct-min spec)
        _ (assert spike-atr-prct-min "spike-signal misses parameter: :spike-atr-prct-min")
        daily-close (:daily-close combined-ds)
        daily-atr (:daily-atr combined-ds)
        intraday-close (:close combined-ds)
        intraday-change (fun/- intraday-close daily-close)
        spike-signal (dtype/emap (partial spike-signal-bar spike-atr-prct-min) :object daily-atr intraday-change)]
    spike-signal))


(comment 
    (require '[tech.v3.dataset :as tds])
  (def ds (tds/->dataset {:daily-close [1.0 2.0 3.0 4.0]
                          :daily-atr [0.1 0.1 0.1 0.1]
                          :close [0.5 2.3 3.5 4.0]}))
  
  (spike-signal {:spike-atr-prct-min 0.5} ds)
    
  
 ; 
  )