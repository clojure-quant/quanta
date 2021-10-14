(ns ta.trade.signal
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [tick.alpha.api :as tick]
   [tech.v3.dataset :as tds]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype.functional :as fun]
   [tech.v3.datatype :as dtype]
   [tech.v3.dataset.print :refer [print-range]]
   [tablecloth.api :as tablecloth]
   [fastmath.core :as math]
   [fastmath.stats :as stats]
   [ta.series.ta4j :as ta4j]
   [ta.dataset.backtest :as backtest]
   [ta.warehouse :as wh]
   [ta.data.date :as dt]
   [ta.xf.ago :refer [xf-ago-pair]]))

; signal :buy :sell :hold  
; one or more buy signals mean we do want to be long.
; vice versa sell signal.

(defn xf-signal->position [xf]
  (let [position (atom :none)]
    (fn
      ;; SET-UP
      ([]
       (reset! position :none)
       (xf))
     	;; PROCESS
      ([result input]
       (xf result (case input
                    :buy (reset! position :long) ;reset! returns the new value
                    :sell (reset! position :short)
                    @position)))
      ;; TEAR-DOWN
      ([result]
       (xf result)))))

(defn position-change->trade [[position-prior position-current]]
  (if (= position-prior position-current)
    nil
    (case position-current
      :long :buy
      :short :sell
      nil)))

(defn signal->position [signal-seq]
  (into [] xf-signal->position
        signal-seq))

(defn signal->trade [signal-seq]
  (into [] (comp
            xf-signal->position
            xf-ago-pair
            (map position-change->trade))
        signal-seq))

(defn xf-trade->trade-no [xf]
  (let [no (atom 0)]
    (fn
      ;; SET-UP
      ([]
       (reset! no 0)
       (xf))
     	;; PROCESS
      ([result input]
       (when (or (= input :buy) (= input :sell))
         (swap! no inc))
       (xf result @no))
      ;; TEAR-DOWN
      ([result]
       (xf result)))))

(defn trade->trade-no [trade-seq]
  (into [] xf-trade->trade-no
        trade-seq))


; this function is also in ta.dataset.backtest
(defn running-index-vec [ds]
  (range 1 (inc (tablecloth/row-count ds))))

(defn trade-signal [ds]
  (let [signal (:signal ds)
        trade (signal->trade signal)
        trade-no (trade->trade-no trade)
        position (signal->position signal)]
    (tablecloth/add-columns ds {:index (running-index-vec ds)
                                :signal signal
                                :trade trade
                                :trade-no trade-no
                                :position position})))


(comment

  (into [] xf-signal->position
        [:none
         :buy :buy :buy :none nil nil :buy :none :none
         :sell :none])

  (into [] (comp xf-ago-pair
                 (map position-change->trade))
        [:none :long :long :long :long :long :long :long :long :long :short :short])

  (into [] (comp
            xf-signal->position
            xf-ago-pair
            (map position-change->trade))
        [:none
         :buy :buy :buy :none nil nil :buy :none :none
         :sell :none])

  (signal->position [:none
                     :buy :buy :buy :none nil nil :buy :none :none
                     :sell :none])


  (signal->trade [:none
                  :buy :buy :buy :none nil nil :buy :none :none
                  :sell :none])

  (-> [:none
       :buy :buy :buy :none nil nil :buy :none :none
       :sell :none]
      signal->trade
      trade->trade-no)


  (-> (signal->trade [:none
                      :buy :buy :buy :none nil nil :buy :none :none
                      :sell :none])
      trade->trade-no)



  (signal->trade [:neutral :long :long])

;  
  )