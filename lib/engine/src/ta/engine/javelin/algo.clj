(ns ta.engine.javelin.algo
  (:require
    [ta.algo.create :as a] 
    [javelin.core-clj :refer [cell=]]
    [ta.engine.javelin.calendar :refer [get-calendar]]))

(defn add-algo
  "returns a cell that calculates the strategy
   throws once if required parameters are missing"
  [env {:keys [type calendar] :as spec}]
  (assert calendar)
  (assert type)
  (let [time-c (get-calendar env calendar) 
        calc-fn (a/create-algo spec)
        ts (cell= (calc-fn env spec time-c))] ; nom/execute
    ts))

(defn add-algos [env spec-seq]
  (doall (map #(add-algo env %) spec-seq)))


;(defn combine [combiner & cells]
;  (cell= (apply combiner cells))]))

    

