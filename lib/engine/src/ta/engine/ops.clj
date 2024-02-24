(ns ta.engine.ops
 (:require 
  [ta.engine.protocol :as ep]))

(defn add-ops 
  "construct a graph of cells (that can be value-cells, formula-cells, calendar-cells),
   from a sequence for operations.   
   returns a cell (if one op is passed), or a map of cells, where keys are the ids used.
   for valid operations see notebook.playground.engine.ops"
  [engine ops]
  (let [db (atom {})
        get-cells (fn [ids] 
                    (map (fn [id] 
                           (get @db id)) ids))
        add-op (fn [[id {:keys [calendar time-fn
                                formula formula-fn
                                value]}]]
                 (assert (or calendar formula value))
                 (let [r (cond
                            calendar 
                            (ep/calendar-cell engine time-fn calendar)
                            formula 
                            (ep/formula-cell engine formula-fn (get-cells formula))
                            value 
                            (ep/value-cell engine value))]
                   (swap! db assoc id r)
                  [id r]))
        result-map (->> (map add-op (partition 2 ops))
                        (into {}))]
    (if (= 2 (count ops)) ; 2 means [:id {:value 5}]
        (-> result-map vals first)
       result-map)))

