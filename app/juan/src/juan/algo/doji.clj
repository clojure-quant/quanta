(ns juan.algo.doji
  (:require
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [ta.indicator :as ind]))

(defn doji-signal-one [{:keys [max-open-close-over-low-high]} open close open-close-over-low-high volume volume-sma]
  #_(println "doji-signal-one "
           "open: " open "close: " close
           "open-close-over-low-high"  open-close-over-low-high
           "volume: " volume
           "volume-sma: " volume-sma)
  (if (or (> open-close-over-low-high max-open-close-over-low-high)
          (< volume volume-sma))
    :flat
    (cond
      (>= close open)
      :long

      (<= close open)
      :short

      :else
      :flat)))

(comment
  (doji-signal-one {:max-open-close-over-low-high 0.5} 100 101 0.3 100000 50000)
   ;; => :buy
  (doji-signal-one {:max-open-close-over-low-high 0.5} 101 100 0.3 100000 50000)
   ;; => :sell

  ; test for open-close-over-low-high above threshold.
  (doji-signal-one {:max-open-close-over-low-high 0.5} 101 100 0.7 100000 50000)
   ;; => :flat

   ; test for volume below average-volume
  (doji-signal-one {:max-open-close-over-low-high 0.5} 101 100 0.3 50000 100000)
   ;; => :flat

;
  )

(defn doji-signal
  "doji is a bar with 
   1. high volume
   2. Big range + small movement.
   input: options + bar-ds
   output: column with doji-signal"
  [env {:keys [max-open-close-over-low-high volume-sma-n] :as opts} bar-ds]
  (assert max-open-close-over-low-high "doji needs max-open-close-over-low-high parameter")
  (assert volume-sma-n "doji needs volume-sma-n parameter")
  (let [low-high (fun/- (:high bar-ds) (:low bar-ds))
        open-close (fun/- (:close bar-ds) (:open bar-ds))
        open-close-over-low-high (fun// open-close low-high)
        volume-sma (ind/sma {:n volume-sma-n :of :volume} bar-ds)
        signal (dtype/emap (partial doji-signal-one opts) :object
                           (:open bar-ds)
                           (:close bar-ds)
                           open-close-over-low-high
                           (:volume bar-ds)
                           volume-sma)]
    (tc/add-columns bar-ds
                    {:open-close open-close
                     :open-close-over-low-high open-close-over-low-high
                     :volume-sma volume-sma
                     :doji-signal signal})))


(comment
  (require '[tablecloth.api :as tc])
  (def ds (tc/dataset [{:open 100.0 :high 120.0 :low 90.0 :close 101.0 :volume 100}
                       {:open 100.0 :high 120.0 :low 90.0 :close 110.0 :volume 100}
                       {:open 100.0 :high 120.0 :low 90.0 :close 101.0 :volume 102}
                       {:open 100.0 :high 120.0 :low 90.0 :close 101.0 :volume 104}
                       {:open 100.0 :high 120.0 :low 90.0 :close 101.0 :volume 106}]))
  ds

  (doji-signal nil {:max-open-close-over-low-high 0.3
                    :volume-sma-n 2} ds)

 ; 
  )