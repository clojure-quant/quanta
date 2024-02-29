(ns ta.live.env-watcher
  (:require 
   [taoensso.timbre :as timbre :refer [info warn error]]
   [ta.algo.env.protocol :as algo-env]
   [ta.algo.spec.inspect :refer [subscriptions calendar-subscriptions]]
   [ta.live.quote-manager :as quotes]
   [ta.live.bar-generator :as bargen]
   [ta.live.calendar-time :as ct]))

(defn process-subscription [state subscription]
  (let [subscription (select-keys subscription [:asset :feed])
        subscriptons-a (:subscriptions state)]
    (when (not (contains? @subscriptons-a subscription))
      (swap! subscriptons-a conj subscription)
      (info "watcher is subscribing: " subscription)
      (quotes/subscribe (:quote-manager state) subscription))))

(defn process-calendar-subscription [state calendar-subscription]
  (let [calendar-subscription (select-keys calendar-subscription [:asset :feed :calendar])
        calendars-a (:calendars state)]
    (when (not (contains? @calendars-a calendar-subscription))
      (swap! calendars-a conj calendar-subscription)
      (info "watcher is adding bar-generation for:  " calendar-subscription)
      (bargen/start-generating-bars-for (:bar-generator state) calendar-subscription))))

(defn process-calendar [state calendar]
  (let [time-a (:time state)]
    (when (not (contains? @time-a calendar))
      (swap! time-a conj calendar)
      (info "watcher is adding time-generator for:  " calendar)
      (ct/add-calendar (:time-generator state) calendar))))


(defn process-added-algo [state spec]
  (let [qs (subscriptions spec)
        cs (calendar-subscriptions spec)
        times (map :calendar cs)]
    (doall (map #(process-subscription state %) qs))
    (doall (map #(process-calendar-subscription state %) cs))
    (doall (map #(process-calendar state %) times))
    nil))

(defn start-env-watcher [env quote-manager bar-generator time-generator]
  (let [state {:subscriptions (atom #{})
               :calendars (atom #{})
               :time (atom #{})
               :time-generator time-generator
               :quote-manager quote-manager
               :bar-generator bar-generator}
        w (fn [spec]
            (info "watcher received:  spec")
            (process-added-algo state spec))]
    (algo-env/set-watcher env w)  
    state))


