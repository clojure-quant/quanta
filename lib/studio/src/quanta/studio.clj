(ns quanta.studio
  (:require
   [de.otto.nom.core :as nom]
   [taoensso.timbre :refer [info warn error]]
   [extension :as ext]
   [clj-service.core :refer [expose-functions]]
   [ta.viz.error :refer [error-render-spec]]
   [ta.algo.env :refer [create-env-javelin]]
   [ta.algo.env.protocol :as algo-env]
   [quanta.model.backtest :refer [fire-backtest-events]]
   [quanta.studio.template :as template]
   [quanta.studio.model :as model]
   [quanta.studio.publish :refer [push-viz-result]]
   [ta.helper.date :refer [now]]))

(defn- requiring-resolve-safe [template-symbol]
  (try
    (requiring-resolve template-symbol)
    (catch Exception ex
      (error "could not resolve template symbol: " template-symbol " ex: " ex)
      nil)))

(defn add-template [this template-symbol]
  (if-let [template-var (requiring-resolve-safe template-symbol)]
    (let [template-val (var-get template-var)]
      (template/add this template-val))
    (throw (ex-info "quanta-template could not be resolved"
                    {:template template-symbol}))))

(defn- add-templates
  "adds templates from extensions"
  [this exts]
  (let [template-symbols (ext/get-extensions-for exts :quanta/template concat [] [])]
    (info "adding templates: " template-symbols)
    (doall (map #(add-template this %) template-symbols))))

(defn start-studio [{:keys [exts clj role bar-db env-live]}]
  (info "starting quanta-studio..")
  (let [this {:bar-db bar-db
              :templates (atom {})
              :subscriptions-a (atom {})
              :env-live env-live}]
    (add-templates this exts)
    (if clj
      (do
        (info "starting quanta-studio clj-services..")
        (expose-functions clj
                          {:name "quanta-studio"
                           :symbols ['quanta.studio.template/available-templates
                                     'quanta.studio.template/get-options
                                     'quanta.studio/backtest
                                     'quanta.studio/subscribe
                                     'quanta.studio/unsubscribe]
                           :permission role
                           :fixed-args [this]}))
      (warn "quanta-studio starting without clj-services, perhaps you want to pass :clj key"))
    (info "quanta-studio running!")
    this))

(defn log-viz-result [template-id task-id result]
  (info "viz-result template-id: " template-id " task-id: " task-id (if (nom/anomaly? result) " anomaly!" " success"))
      ; make sure we never retrun something. result ends up in a javelin cell
  nil)

(defn backtest
  "this runs a viz-task once and returns the viz-result.
   output is guaranteed to be always viz-spec format, so
   possible anomalies are converted to viz-spec"
  [{:keys [bar-db] :as this} template-id template-options mode]
  (let [template (template/load-with-options this template-id template-options)
        env (create-env-javelin bar-db)
        {:keys [viz-result] :as task} (model/create-algo-model env template mode log-viz-result)
        window-or-dt (now)
        model (algo-env/get-model env)
        result (if (nom/anomaly? task)
                 task
                 (do  (fire-backtest-events model window-or-dt)
                      @viz-result))]

    (if (nom/anomaly? result)
      (error-render-spec result)
      result)))

(defn subscribe
  "starts new algo via the web ui.
   this creates a viz-task once and then starts pushing all results to the websocket.
   returns task-id or nom/anomaly"
  [{:keys [env-live subscriptions-a] :as this} template-id template-options mode]
  (info "subscribe-live template:" template-id "mode: " mode)
  (if env-live
    (let [template (template/load-with-options this template-id template-options)
          result-fn push-viz-result
          {:keys [task-id] :as task} (model/create-algo-model env-live template mode result-fn)]
      (if (nom/anomaly? task)
        task
        (do (swap! subscriptions-a assoc task-id task)
            task-id)))
    (nom/fail ::subscribe {:message "cannot subscribe, :env-live is nil."})))

(defn get-subscription-state [{:keys [subscriptions-a] :as this} task-id]
  (if-let [m (get @subscriptions-a task-id)]
    (let [{:keys [viz-result]} m]
      @viz-result)
    (nom/fail ::subscribe {:message "subscription not found"})))

(defn unsubscribe [{:keys [env-live subscriptions-a] :as this} task-id]
  (when-let [m (get @subscriptions-a task-id)]
    (info "unsubscribing subscription-id: " task-id)
    (model/destroy-algo-model env-live m)
    ; done!
    (swap! subscriptions-a dissoc task-id)
    :success))