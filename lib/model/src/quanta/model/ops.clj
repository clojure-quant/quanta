(ns quanta.model.ops
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [quanta.model.protocol :as p]))

(defn add-ops
  "construct a graph of cells (that can be value-cells, formula-cells, calendar-cells),
   from a sequence for operations.   
   returns a cell (if one op is passed), or a map of cells, where keys are the ids used.
   for valid operations see notebook.playground.engine.ops"
  [model ops]
  (let [db (atom {})
        get-cells (fn [ids]
                    (map (fn [id]
                           (get @db id)) ids))
        add-op (fn [[id {:keys [calendar time-fn
                                formula formula-fn
                                value] :as opts}]]
                 (info "adding id: " id " opts: " opts)
                 (assert (or calendar formula value))
                 (let [r (cond
                           calendar
                           (p/calendar-cell model time-fn calendar)
                           formula
                           (p/formula-cell model formula-fn (get-cells formula))
                           value
                           (p/value-cell model value))]
                   (swap! db assoc id r)
                   [id r]))
        result-map (->> (map add-op ops)
                        (into {}))]
    (if (= 1 (count ops))
      (-> result-map vals first)
      result-map)))

