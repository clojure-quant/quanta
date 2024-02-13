(ns ta.env.javelin.algo
  (:require
    [javelin.core-clj :refer [cell cell=]]
    [ta.env.algo.bar-strategy :refer [trailing-window-barstrategy]]
    [ta.env.javelin.calendar :refer [get-calendar]]
   ))

(defn add-bar-strategy [env opts]
  (let [calendar (:calendar opts)
        _ (assert calendar)
        time-c (get-calendar env calendar) 
        bs (cell= (trailing-window-barstrategy env opts time-c))] ; nom/execute
   bs))

(defn add-bar-strategies [env strategies]
  (doall (map #(add-bar-strategy env %) strategies)))


;(defn combine [combiner & cells]
;  (cell= (apply combiner cells))]))

    

