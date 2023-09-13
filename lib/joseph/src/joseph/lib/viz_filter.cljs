(ns joseph.lib.viz-filter
  (:require
   [clojure.string :as str]))



(def all "*")


(defn filter-viz-eq [key value list]
  (let [disabled? (or (nil? value) 
                      (str/blank? value) 
                      (= all value))]
    (if disabled? 
      list
      (filter #(= value (key %)) list))))


(defn unique [key list]
  (->> list
       (map key)
       (into #{})
       (into [])))