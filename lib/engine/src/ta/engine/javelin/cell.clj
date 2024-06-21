(ns ta.engine.javelin.cell
  (:require
   [de.otto.nom.core :as nom]
   [taoensso.timbre :refer [trace debug info warn error]]
   [javelin.core-clj :refer [cell= cell lift destroy-cell!]]
   [ta.engine.javelin.calendar :refer [get-calendar]]))

(defn safe-formula-fn [formula-fn]
  (fn [& args]
    ;(warn "calculating formula args: " args)
    (try
      (apply formula-fn args)
      (catch AssertionError ex
        (nom/fail ::error {:message "algo assert failure"}))
      (catch Exception ex
        (nom/fail ::error {:message "algo exception"})))))

(defn calendar-cell
  "returns a cell that calculates the strategy
   throws once if required parameters are missing"
  [eng time-fn calendar]
  (assert calendar)
  (assert time-fn)
  (let [time-c (get-calendar eng calendar)
        time-fn-wrapped (safe-formula-fn time-fn)
        c (cell= (time-fn-wrapped time-c))] ; nom/execute
    c))

(defn formula-cell
  "returns a cell that calculates the strategy
   throws once if required parameters are missing"
  [eng formula-fn cell-seq]
  (assert cell-seq)
  (assert formula-fn)
  (let [formula-fn-wrapped (safe-formula-fn formula-fn)
        f (lift formula-fn-wrapped)
        c (apply f cell-seq)]
    c))

(defn value-cell
  "returns a cell that has a value
   it's value can be changed with atom like syntax."
  [eng v]
  (let [c (cell v)] ; nom/execute
    c))

(defn destroy-cell [eng c]
  (destroy-cell! c))
