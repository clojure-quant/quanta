(ns ta.env.tools.result-monitor
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [tablecloth.api :as tc]
   [manifold.stream :as s]
   [manifold.bus :as mbus]
   [ta.env.tools.last-msg-summary :as sum]))

(defn create-and-link-topic-bus [result-stream]
  (let [bus (mbus/event-bus)]
    (s/consume (fn [{:keys [topic] :as msg}]
                 (when topic
                   (mbus/publish! bus topic msg)))
               result-stream)
    bus))


(defn result-aggregator [bus topic]
  (let [s (mbus/subscribe bus topic)
        state (sum/create-last-summary s :id)]
    state))


(defn result-monitor-start [env]
  (info "creating result-monitor ..")
  (let [result-stream (:live-results-stream env)
        bus (create-and-link-topic-bus result-stream)]
    {:bus bus
     :topics (atom {})}))

(defn monitor-topic [{:keys [topics bus] :as state} topic result-seq-transformer]
  (info "monitoring topic: " topic)
  (swap! topics assoc topic
         {:topic topic
          :agg (result-aggregator bus topic)
          :transformer result-seq-transformer}))

(defn snapshot [{:keys [topics] :as state} topic]
  (let [{:keys [agg transformer]} (get @topics topic)
        result (sum/current-summary agg)]
    (transformer result)))


(defn last-result-row [ds-algo]
  (tc/select-rows ds-algo [(dec (tc/row-count ds-algo))]))

(defn last-ds-row [results]
  (->> results
       (map :result)
       (map last-result-row)
       (apply tc/concat)))

(comment
  (require '[modular.system])
  (def live (modular.system/system :live))
  live
  (:live-results-stream live)

   ; 1. create result monitor
  (def state (result-monitor-start live))
  state

  ; 2. subscribe to topic with result transformer
  (monitor-topic state :sma-crossover-1m last-ds-row)

  ; 3. get current transformed result.
  (snapshot state :sma-crossover-1m)



;
  )

