(ns quanta.studio.template
  (:require
   [de.otto.nom.core :as nom]
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warn error errorf]]
   [com.rpl.specter :as specter]))

(defn add
  "adds a template to the template-db
   templates are used in the browser so traders can add
   and configure algos easily."
  [{:keys [templates]} {:keys [id algo] :as template-spec}]
  (assert id "missing mandatory parameter :id")
  (assert algo "missing mandatory parameter :algo")
  (swap! templates assoc id template-spec))

(defn available-templates
  "returns all template-ids. 
   used in the browser to select a template"
  [{:keys [templates]}]
  (-> @templates keys sort))

(defn load-template
  "returns the template for a template-id"
  ; note: get is used, because template-id might be a string.
  [{:keys [templates]} template-id]
  (-> @templates (get template-id)))

(defn get-default-value [template path]
  (debug "getting default value template: " (:id template) " path: " path)
  (let [algo (:algo template)
        [k v]  (cond
                 (keyword path)
                 [path (get algo path)]

                 (vector? path)
                 [path (get-in algo path)]

                 :else
                 [path nil])]
    ;(info "getting default value algo: " algo " path: " path)
    [k v]))

(defn get-default-values [template options]
  (info "getting default values options: " options)
  (let [paths (map :path options)]
    ;(info "paths: " paths)
    (->> (map #(get-default-value template %) paths)
         (into {}))
    #_(:algo template)))

(defn get-options
  "returns the options (what a user can edit) for a template-id"
  [this template-id]
  (info "getting options for template: " template-id)
  (let [template (load-template this template-id)
        options (or (:options template) [])
        options (if (vector? options)
                  options
                  (options))]
    {:options options
     :current (get-default-values template options)}))

(defn apply-options [template options]
  ; if all paths are keys, this is really simple.
  ; (update template :algo merge options)
  ; but if we can have hierarchical paths, then we 
  ; need to set them via specter, so type gets
  ; preserved. 
  (assoc template :algo
         (reduce
          (fn [r [path v]]
            (let [path (if (keyword? path)
                         [path]
                         path)]
              (debug "setting path: " path " to val: " v)
              (specter/setval path v r)))
          (:algo template)
          options)))

(defn load-with-options [this template-id options]
  (let [template (load-template this template-id)
        template (apply-options template options)]
    (info "template " template-id " options: " (:algo template))
    ;(warn "full template: " template)
    template))

(defn- get-fn [fun]
  (if (symbol? fun)
    (requiring-resolve fun)
    fun))

(defn show-result [template result viz-mode]
  (let [{:keys [viz viz-options]} (get template viz-mode)
        viz-fn (get-fn viz)]
    (if viz-fn
      (if (nom/anomaly? result)
        result
        (try
          (let [r (if viz-options
                    (viz-fn viz-options result)
                    (viz-fn result))]
            r)
          (catch Exception ex
            (error "viz calc exception: " ex)
            (nom/fail ::algo-calc {:message "algo viz exception!"
                                   :location :visualize}))))
      (nom/fail ::unknown-viz {:message (str "algo viz not found: " viz-mode)}))))

(comment

  (def paths [:a [:b :c] :d])
  (def data [{:a 1 :b {:c 22 :x 5} :d 55}
             {:x 1 :i {:y 2 :x 5} :d 55}])

  ; option-ui => algo
  (specter/select [0 :b :c] data)
  (specter/setval [0 :b :c] 555 data)

  (specter/setval [0 :b :c] 555 [])

  (specter/select [0 :b :c] data)

  (defn no-path? [p]
    (info "no-path: " p)
    (not (contains? paths p)))

  (defn path? [p]
    (info "path: " p)
    (contains? paths p))

  (no-path? :d)

  (specter/setval [:a specter/ALL] 4 data)

  (specter/transform [0 :b :c]
                     specter/NONE
                     data)

  (specter/select [:a :b] data)

  (def template (load-template :juan-fx))

  (load-with-options :juan-fx {[1 :atr-n] 20})

  (assoc-in template [:algo 1 :atr-n] 50)
  (assoc-in template [:algo 1 :atr-n] 50)

  {:asset "ETHUSDT",
   [1 :atr-n] 30,
   [1 :asset] "NZD/USD",
   [3 :asset] "NZD/USD"}

  (specter/setval [1 :asset]  "NZD/USD"
                  [:day {:feed :fx
                         :asset "EUR/USD"}
                   :minute {:type :trailing-bar, :asset "EUR/USD", :import :kibot-http,
                            :trailing-n 1440, :max-open-close-over-low-high 0.3, :volume-sma-n 30}
                   :signal {:formula [:day :minute], :spike-atr-prct-min 0.5, :pivot-max-diff 0.001,
                            :algo 'juan.algo.combined/daily-intraday-combined}])

; 
  )


