(ns ta.interact.template
  (:require 
    [taoensso.timbre :as log :refer [tracef debug debugf info infof warn error errorf]]))

(defonce db (atom {}))

(defn add
  "adds a template to the template-db
   templates are used in the browser so traders can add
   and configure algos easily."
[{:keys [id algo viz] :as template-spec}]
  (assert id "missing mandatory parameter :id")
  (assert algo "missing mandatory parameter :algo")
  (assert viz "missing mandatory parameter :viz")
  (swap! db assoc id template-spec))

(defn available-templates 
  "returns all template-ids. 
   used in the browser to select a template"
  []
  (-> @db keys sort))

(defn load-template
  "returns the template for a template-id"
  ; note: get is used, because template-id might be a string.
  [template-id]
  (-> @db (get template-id)))

(defn get-default-value [template path]
  ;(info "getting default value template: " template " path: " path)
  (let [algo (:algo template)]
    ;(info "getting default value algo: " algo " path: " path)
    [path (get algo path)]))

(defn get-default-values [template options]
  (info "getting default values options: " options)
  (let [paths (map :path options)]
    ;(info "paths: " paths)
    (->> (map #(get-default-value template %) paths)
         (into {}))))

(defn get-options 
  "returns the options (what a user can edit) for a template-id"  
  [template-id]
  (info "getting options for template: " template-id)
  (let [template (load-template template-id)
        options (or (:options template) [])
        options (if (vector? options)
                  options
                  (options))]
    {:options options
     :current (get-default-values template options)}))


(defn apply-options [template options]
  (update template :algo merge options))


(defn load-with-options [template-id options]
  (warn "applying options to template: " template-id " options: "options)
  (let [template (load-template template-id)
        template (apply-options template options)]
    (warn "applied options: " (:algo template))
    ; todo: implement!
    template))


