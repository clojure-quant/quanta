(ns joseph.lib.select
  (:require
    [goldly :refer [eventhandler]]))



(defn select-string [{:keys [items value on-change]}]
  (into [:select {:value value
                  :on-change (eventhandler 
                                (fn [value _e]
                                  (on-change value)
                                  ))}]
        (map (fn [s] [:option {:value s} s]) items)))
