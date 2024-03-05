(ns ta.interact.subscription
  (:require
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warn error errorf]]
   [modular.system]
   [modular.ws.core :refer [send! send-all! send-response connected-uids]]
   [ta.algo.env.protocol :as algo]
   [ta.engine.protocol :as engine]
   [ta.algo.env.protocol :as env]
   [ta.interact.spec-db :as db]))

(defonce subscriptions-a (atom {}))
(defonce results-a (atom {}))
(defonce visualizations-a (atom {}))

(defn push-viz-result [topic result]
  (try
    (warn "sending viz result: " topic) ; putting result here will spam the log.
    ;(warn "result: " result)
    (send-all! [:interact/subscription {:topic topic :result result}])
    (catch Exception ex
      (error "error in sending viz-result!"))))

(defn- get-fn [fun]
  (if (symbol? fun)
    (requiring-resolve fun)
    fun))

(defn create-viz-fn [e {:keys [topic algo viz]}]
  ;(info "create-viz-fn: " viz)
  (let [viz-fn (get-fn viz)]
    (when viz-fn
      (fn [result]
        (try
          (warn "calculating visualization:" topic " .. ")
          ;(warn "result: " result)
          (let [r (viz-fn result)]
            (warn "calculating visualization:" topic " DONE!")
            r)
          (catch Exception ex
            (error "exception calculating visualization topic: " topic)
            (error ex)))))))

(defn subscribe [e {:keys [topic algo viz key] :as algo-viz-spec}]
  (let [eng (env/get-engine e)
        algo-results-a (algo/add-algo e algo)
        algo-result-a (if key (key algo-results-a)
                         algo-results-a)
        ;_ (info "algo-result-a: " algo-result-a)
        viz-fn (create-viz-fn e algo-viz-spec)]
    (if viz-fn
      (let [viz-result-a (engine/formula-cell eng viz-fn [algo-result-a])]
        (swap! subscriptions-a assoc topic algo-viz-spec)
        (swap! results-a assoc topic algo-result-a)
        (swap! visualizations-a assoc topic viz-result-a)
        (engine/formula-cell eng #(push-viz-result topic %) [viz-result-a])    
        #_(add-watch viz-result-a topic (fn [t a old new]
                                        (push-viz-result topic new))))
      (error "viz-fn not found! topic: " topic)))
  topic)

(defn subscribe-kw [env-kw topic]
  (let [e (modular.system/system env-kw)
        algo-viz-spec (db/get-viz-spec topic)]
    (info "subscribing algo-vizspec: " algo-viz-spec)
    (subscribe e algo-viz-spec)))

(defn subscribe-live [topic]
  (if-let [result (@visualizations-a topic)]
     (push-viz-result topic @result)
     (subscribe-kw :live topic)))
