(ns ta.interact.subscription
  (:require 
    [taoensso.timbre :as log :refer [tracef debug debugf info infof warn error errorf]]
    [modular.system]
    [modular.ws.core :refer [send! send-all! send-response connected-uids]]
    [ta.algo.env.protocol :as algo] 
    [ta.engine.protocol :as engine]
    [ta.algo.env.protocol :as env]
    [ta.interact.spec-db :as db]))

(defonce results-a (atom nil))
(defonce visualizations-a (atom {}))

(defn push-viz-result [topic result]
  (try
    (warn "sending viz result: " topic) ; putting result here will spam the log.
    (swap! visualizations-a assoc topic result)
    (send-all! [:interact/subscription {:topic topic :result result}])
    (catch Exception ex
       (error "error in sending viz-result!"))))
    
(defn- get-fn [fun]
  (if (symbol? fun)
    (requiring-resolve fun)
    fun))

; @x

(defn create-viz-fn [e {:keys [topic algo viz]}]
  ;(info "create-viz-fn: " viz)
  (let [viz-fn (get-fn viz)]
    (fn [result]
      ;(info "calculating visualization opts:" result)
      (swap! results-a assoc topic result)
      (try 
        (viz-fn result)
        (catch Exception ex
          (error "exception calculating visualization!")
          (error ex))))))


(defn subscribe [e {:keys [topic algo viz] :as algo-viz-spec}]
  (let [eng (env/get-engine e)
        algo-result-a (algo/add-algo e algo)
        ;_ (info "algo-result-a: " algo-result-a)
        viz-fn (create-viz-fn e algo-viz-spec)
        viz-result-a (engine/formula-cell eng viz-fn [algo-result-a])]
     (add-watch viz-result-a topic (fn [t a old new]
                                     (push-viz-result topic new)))
     topic))

(defn subscribe-kw [env-kw topic]
  (let [e (modular.system/system env-kw)
        algo-viz-spec (db/get-viz-spec topic)]
    (info "subscribing algo-vizspec: " algo-viz-spec)
    (subscribe e algo-viz-spec)))

(defn subscribe-live [topic]
  (subscribe-kw :live topic))
