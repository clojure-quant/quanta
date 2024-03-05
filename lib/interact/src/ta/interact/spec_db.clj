(ns ta.interact.spec-db)

(defonce db (atom {}))

(defn add-spec [{:keys [topic] :as algo-viz-spec}]
  (assert topic "missing mandatory parameter :label")
  (swap! db assoc topic algo-viz-spec))

(defn available-topics []
  (-> @db keys sort))

(defn get-viz-spec [topic]
  (-> @db (get topic)))
