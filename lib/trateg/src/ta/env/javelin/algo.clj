(ns ta.env.javelin.algo
  (:require
    [javelin.core-clj :refer [cell=]]
    [ta.env.algo.bar-strategy :refer [create-trailing-barstrategy]]
    [ta.env.javelin.calendar :refer [get-calendar]]))

(defn add-bar-strategy 
  "returns a cell that calculates the strategy
   throws once if required parameters are missing"
  [env spec]
  (let [algo (:algo spec)
        _ (assert algo)
        calendar (:calendar spec)
        _ (assert calendar)
        time-c (get-calendar env calendar) 
        calc-fn (create-trailing-barstrategy spec)
        bs (cell= (calc-fn env spec time-c))] ; nom/execute
   bs))

(defn add-bar-strategies [env strategies]
  (doall (map #(add-bar-strategy env %) strategies)))


;(defn combine [combiner & cells]
;  (cell= (apply combiner cells))]))

    

