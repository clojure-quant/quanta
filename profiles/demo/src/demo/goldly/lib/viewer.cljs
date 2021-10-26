(def empty-viewer-state {:notebooks {}
                         :scratchpad nil})

(defonce viewer-state
  (atom empty-viewer-state)) ; start empty.

(defn viewer-notebooks []
  (-> @viewer-state
      :notebooks
      keys))

(defn add-plot-to-notebook [state {:keys [ns-nb data] :as viewer-op}]
  (println "adding plot to notebook: " ns-nb)
  (let [nb (or (get-in state [:notebooks ns-nb])
               [])]
    (assoc-in state [:notebooks ns-nb] (conj nb data))))

(defn set-scratchpad-notebook [state {:keys [ns-nb data] :as viewer-op}]
  (println "adding plot to notebook: " ns-nb)
  (let [nb {:ns ns-nb
            :plots [data]}]
    (assoc state :scratchpad nb)))

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


