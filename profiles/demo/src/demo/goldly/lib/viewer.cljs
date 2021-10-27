(defonce empty-viewer-state {:notebooks {:scratchpad {:ns-nb "empty"
                                                      :plots []}}
                             :current :scratchpad})

(defonce viewer-state
  (r/atom empty-viewer-state)) ; start empty.



(defn add-plot-to-notebook [state {:keys [ns-nb data] :as viewer-op}]
  (println "adding plot to notebook: " ns-nb)
  (let [nb (or (get-in state [:notebooks ns-nb])
               {:ns ns-nb
                :plots []})
        nb (assoc nb :plots (conj (:plots nb) data))]
    (assoc-in state [:notebooks ns-nb] nb)))

(defn set-scratchpad-notebook [state {:keys [ns-nb data] :as viewer-op}]
  (println "calculating scratchpad: " ns-nb)
  (let [nb {:ns ns-nb
            :plots [data]}]
    (assoc-in state [:notebooks :scratchpad] nb)))

; {:ns-nb demo.playground.cljplot
;  :op :plot
;  :data {:resources [[1 :png]]
;         :form [img [1 :png]]}}

(defn process-viewer-op [{:keys [op] :as viewer-op}]
  (case op
    :clear (reset! viewer-state empty-viewer-state)
    :plot  (do
              ; scratchpad just contains the last plot
             (swap! viewer-state set-scratchpad-notebook viewer-op)
              ; notebooks contain a big list of plots          
             (swap! viewer-state add-plot-to-notebook viewer-op))
    (println "unknown viewer op:" op)))

(rf/reg-event-fx
 :viewer/update
 (fn [{:keys [db]} [_ viewer-op]]
   (println "viewer-op received: " viewer-op)
   (process-viewer-op viewer-op)
   nil))

(defn get-notebooks-once []
  (when (empty? (get-in @viewer-state [:notebooks :data]))
    (get-edn "/api/notebook/ns" viewer-state [:notebooks])))


