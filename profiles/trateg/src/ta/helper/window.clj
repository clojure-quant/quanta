(ns ta.helper.window
  (:require
   [tablecloth.api :as tc]
   [ta.warehouse :as wh]))

(defn drop-beginning [ds beginning-row-drop-count]
  (tc/select-rows ds (range beginning-row-drop-count (tc/row-count ds))))

(defn- p-row-index-in-range [index-begin index-end]
  (fn [{:keys [index]}]
    (and (>= index index-begin)
         (<= index index-end))))

(comment
; Forward-Window Selector - TODO: make this a unit test
  ((p-row-index-in-range 100 200) {:index 150})
  ((p-row-index-in-range 100 200) {:index 100})
  ((p-row-index-in-range 100 200) {:index 200})
  ((p-row-index-in-range 100 200) {:index 50})
  ((p-row-index-in-range 100 200) {:index 250})
;
  )
(defn get-forward-window
  "Takes a look-forward window out of a dataframe.
  Returns nil if full window not possible
  Start is after the event"
  [df-study idx forward-size]
  ;(assert (and (df? df) (int? Idx) (int? forward-size))
  ;(println "get-forward-window " idx forward-size)
  (let [index-end (+ idx forward-size)]
    (when (< index-end (tc/row-count df-study))
      (tc/select-rows df-study (p-row-index-in-range (inc idx) index-end))
  ;          (select-cols df idx (+ idx forward-size))))
      )))

; trailing true counter

(defn xf-trailing-true-counter [xf]
  (let [past-up-count (atom 0)]
    (fn
      ;; SET-UP
      ([]
       (xf)
       (reset! past-up-count 0))
     	;; PROCESS
      ([result input]
       (if input
         (swap! past-up-count inc)
         (reset! past-up-count 0))
       (xf result @past-up-count))
      ;; TEAR-DOWN
      ([result]
       (xf result)))))

(defn calc-trailing-true-counter [df column]
  (into [] xf-trailing-true-counter (get df column)))

(comment

  (into [] xf-trailing-true-counter
        [false true true true false true true false true])
;  
  )

;; statistics summary

(comment
   ; (dataset/->) 

;  
  )

(defn run-study [w symbol frequency algo options]
  (let [ds (wh/load-symbol w frequency symbol)
        ds-study (algo ds options)]
    ds-study))



