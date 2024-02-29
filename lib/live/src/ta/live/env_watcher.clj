(ns ta.live.env-watcher
  (:require 
   [ta.algo.env.protocol :as algo-env]
   [ta.algo.spec.inspect :refer [subscriptions calendar-subscriptions]]
   [ta.live.quote-manager :as quotes]
   [ta.live.bar-generator :as bargen]))

(defn process-subscription [state subscription]
  (let [subscription (select-keys subscription [:asset :feed])
        subscriptons-a (:subscriptions state)]
    (when (not (contains? @subscriptons-a subscription))
      (swap! subscriptons-a conj subscription)
      (quotes/subscribe (:quotes state) subscription))))

(defn process-calendar [state calendar-subscription]
  (let [calendar-subscription (select-keys calendar-subscription [:asset :feed :calendar])
        calendars-a (:calendars state)]
    (when (not (contains? @calendars-a calendar-subscription))
      (swap! calendars-a conj calendar-subscription)
      (bargen/start-generating-bars-for (:bar-gen state) calendar-subscription))))

(defn process-added-algo [state spec]
  (let [s (subscriptions spec)
        c (calendar-subscriptions spec)]
    (doall (map #(process-subscription state %) s))
    (doall (map #(process-calendar state %) c))
    nil))

(defn start-env-watcher [env quote-manager bar-generator]
  (let [state {:subscriptions (atom #{})
               :calendars (atom #{})
               :quotes quote-manager
               :bar-gen bar-generator}
        w (fn [spec]
            (process-added-algo state spec))]
    (algo-env/set-watcher env w)  
    state))


