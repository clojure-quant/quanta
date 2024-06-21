(ns ta.env.tools.label-monitor
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [manifold.stream :as s]
   [manifold.bus :as mbus]
   [ta.quote.last-msg-summary :as sum]))

(defn get-label-for-result [msg]
  (get-in msg [:algo-opts :label]))
            
(defn create-and-link-label-bus [result-stream]
  (let [bus (mbus/event-bus)]
    (s/consume (fn [msg]
                 (when-let [label (get-label-for-result msg)] 
                   (info "publishing result for label: " label)
                   (mbus/publish! bus label msg)))
               result-stream)
    bus))

(defn get-algo-id-for-result [msg]
  (get-in msg [:algo-opts :id]))

(defn result-aggregator [bus label]
  (let [s (mbus/subscribe bus label)
        state (sum/create-last-summary s get-algo-id-for-result)]
    state))


(defn label-monitor-start [env]
  (info "creating label-monitor ..")
  (let [result-stream (:live-results-stream env)
        bus (create-and-link-label-bus result-stream)]
    {:bus bus
     :labels (atom {})}))

(defn monitor-label [{:keys [labels bus] :as state} label result-seq-transformer]
  (info "monitoring label: " label)
  (swap! labels assoc label
         {:label label
          :agg (result-aggregator bus label)
          :transformer result-seq-transformer}))

(defn snapshot [{:keys [labels] :as state} label]
  (let [{:keys [agg transformer]} (get @labels label)
        result (sum/current-summary agg)]
    (transformer result)))




(comment
  (require '[modular.system])
  (def live (modular.system/system :live))
  live
  (:live-results-stream live)

   ; 1. create label monitor
  (def state (label-monitor-start live))
  state

  ; 2. subscribe to label with result transformer
   (require '[ta.algo.ds :refer [last-ds-row]])
  (monitor-label state :sma-crossover-1m last-ds-row)

  ; 3. get current transformed result.
  (snapshot state :sma-crossover-1m)



;
  )

