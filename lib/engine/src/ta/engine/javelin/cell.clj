(ns ta.engine.javelin.cell
  (:require
   [javelin.core-clj :refer [cell= cell lift destroy-cell!]]
   [ta.engine.javelin.calendar :refer [get-calendar]]))

(defn calendar-cell
  "returns a cell that calculates the strategy
   throws once if required parameters are missing"
  [eng time-fn calendar]
  (assert calendar)
  (assert time-fn)
  (let [time-c (get-calendar eng calendar)
        c (cell= (time-fn time-c))] ; nom/execute
    c))


(defn formula-cell
  "returns a cell that calculates the strategy
   throws once if required parameters are missing"
  [eng formula-fn cell-seq]
  (assert cell-seq)
  (assert formula-fn)
  (let [f (lift formula-fn)
        c (apply f cell-seq)] ; nom/execute
    c))

(defn value-cell
  "returns a cell that has a value
   it's value can be changed with atom like syntax."
  [eng v]
  (let [c (cell v)] ; nom/execute
    c))

(defn destroy-cell [eng c]
 (destroy-cell! c))
