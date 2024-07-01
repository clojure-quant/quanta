(ns quanta.studio.subscription
  (:require
   [de.otto.nom.core :as nom]
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warn error errorf]]
   [nano-id.core :refer [nano-id]]
   [modular.system]
   [modular.ws.core :refer [send-all!]]
   [ta.algo.env.protocol :as algo]
   [ta.algo.error-report :refer [save-error-report]]
   [ta.algo.compile :refer [compile-symbol]]
   [ta.engine.protocol :as engine]
   [quanta.studio.template :as t]
   [ta.viz.error :refer [error-render-spec]]))

(defonce subscriptions-a (atom {}))

(defn push-viz-result [id subscription-id result]
  (try
    (let [error? (nom/anomaly? result)
          result (if error?
                   (error-render-spec result)
                   result)]
      (info "pushing viz-result algo: " id " sub-id: " subscription-id (if error? " anomaly!" " success"))
      (send-all! [:interact/subscription {:subscription-id subscription-id :result result}]))
    (catch Exception ex
      (error "push-viz-result exception!"))))

(defn create-viz-fn [e {:keys [id] :as template} mode]
  ;(info "create-viz-fn: " viz)
  (let [{:keys [viz viz-options]} (get template mode)
        viz-fn (compile-symbol viz)]
    (if (nom/anomaly? viz-fn)
      viz-fn
      (fn [result]
        (if (nom/anomaly? result)
          result
          (try
            (info "calculating visualization:" id " .. ")
            ;(warn "result: " result)
            (let [r (if viz-options
                      (viz-fn viz-options result)
                      (viz-fn result))]
              (debug "calculating visualization:" id " DONE!")
              r)
            (catch Exception ex
              (let [filename (save-error-report "viz" template ex)]
                (error "algo-viz " id " exception. details: " filename)
                (nom/fail ::algo-calc {:message "algo viz exception!"
                                       :filename filename
                                       :location :visualize})))))))))

(defn subscribe
  "starts new algo
   on success: retrun subscription-id
   on error: nom/anomaly"
  [e {:keys [id algo key] :as template} mode]
  (let [subscription-id (nano-id 6)
        eng (algo/get-engine e)
        algo-results-a (algo/add-algo e algo)
        viz-fn (create-viz-fn e template mode)
        err (or (when (nom/anomaly? algo-results-a) algo-results-a)
                (when (nom/anomaly? viz-fn) viz-fn))]
    (if err
      (let [filename (save-error-report (str "subscription" id mode) err (:ex err))]
        (error "create-subscription" id algo mode " error! details: " filename)
        (push-viz-result id subscription-id err)
        subscription-id)
      (let [algo-result-a (if key (key algo-results-a)
                              algo-results-a)
            viz-result-a (engine/formula-cell eng viz-fn [algo-result-a])
            pusher-a (engine/formula-cell eng #(push-viz-result id subscription-id %) [viz-result-a])]
        ;_ (info "algo-result-a: " algo-result-a)
        (swap! subscriptions-a assoc subscription-id {:template template
                                                      :algo-result algo-result-a
                                                      :viz-result viz-result-a
                                                      :pusher pusher-a})
        subscription-id))))

(defn subscribe-kw [env-kw template-id template-options mode]
  (let [e (modular.system/system env-kw)
        template (t/load-with-options template-id template-options)]
    (info "subscribing template: " template-id)
    (subscribe e template mode)))

(defn subscribe-live [template-id template-options mode]
  (info "subscribe-live template:" template-id "mode: " mode)
  (subscribe-kw :live template-id template-options mode))

(defn unsubscribe [subscription-id]
  (when-let [s (get @subscriptions-a subscription-id)]
    (info "unsubscribing subscription-id: " subscription-id)
    (let [e (modular.system/system :live)
          eng (algo/get-engine e)
          {:keys [template algo-result viz-result pusher]} s]
      (engine/destroy-cell eng pusher)
      (engine/destroy-cell eng viz-result)
      (engine/destroy-cell eng algo-result)
      ; done!
      (swap! subscriptions-a dissoc subscription-id))))

(comment

  (def s (atom {:mood "perfect"
                :env {:mode :live}
                :benchmark ["MSFT"
                            "MO"]
                :asset "SPY"}))

  (swap! s assoc :asset "QQQ")
  (swap! s assoc-in [:env :mode] :backtest)
  (swap! s assoc-in [:benchmark 0] "AAPL")

; 
  )

