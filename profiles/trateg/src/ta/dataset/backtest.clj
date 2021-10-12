(ns ta.dataset.backtest
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [tick.alpha.api :as tick]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype.functional :as fun]
   [tech.v3.datatype :as dtype]
   [tech.v3.dataset.print :refer [print-range]]
   [tablecloth.api :as tablecloth]
   [fastmath.core :as math]
   [fastmath.stats :as stats]
   [ta.series.ta4j :as ta4j]
   [ta.warehouse :as wh]
   [ta.data.date :as dt]))

(defn running-index-vec [ds]
  (range 1 (inc (tablecloth/row-count ds))))

(defn add-running-index [ds]
  (tablecloth/add-column ds :index (running-index-vec ds)))

(defn drop-beginning [ds beginning-row-drop-count]
  (tablecloth/select-rows ds (range beginning-row-drop-count (tablecloth/row-count ds))))

(defn- p-row-index-in-range [index-begin index-end]
  (fn [{:keys [index]}]
    (and (>= index index-begin)
         (<= index index-end))))

(defn get-forward-window
  "Takes a look-forward window out of a dataframe.
  Returns nil if full window not possible
  Start is after the event"
  [df-study idx forward-size]
  ;(assert (and (df? df) (int? Idx) (int? forward-size))
  (info "get-forward-window " idx forward-size)
  (let [index-end (+ idx forward-size)]
    (when (< index-end (tablecloth/row-count df-study))
      (tablecloth/select-rows df-study (p-row-index-in-range (inc idx) index-end))
  ;          (select-cols df idx (+ idx forward-size))))
      )))
; study runner 

(defn make-filename [frequency symbol]
  (str symbol "-" frequency))

(defn make-study-filename [study-name frequency symbol]
  (str "study-" study-name "-" symbol "-" frequency))

(defn run-study [w symbol frequency algo options]
  (let [ds (wh/load-ts w (make-filename frequency symbol))
        ds-study (algo ds options)]
    ds-study))

(defn save-study [w ds-study symbol frequency study-name]
  (wh/save-ts w ds-study (make-study-filename study-name frequency symbol))
  (tablecloth/write! ds-study (str "../db/study-" study-name "-" symbol "-" frequency ".csv"))
  ds-study ; important to be here, as save-study is used often in a threading macro
  )

(defn load-study [w symbol frequency study-name]
  (let [ds (wh/load-ts w (make-study-filename study-name frequency symbol))]
    ds))

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