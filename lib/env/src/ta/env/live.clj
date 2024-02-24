(ns ta.env.live
  (:require
   [manifold.stream :as s]
   [ta.engine.protocol :as engine]
   [ta.engine.javelin :refer [create-engine-javelin]]
   [ta.env.live.calendar-time :as ct]
   [ta.env.live.bar-generator2 :as bg]))

(defn create-live-env [{:keys [engine feeds bar-db] :as opts}]
  (assert engine "live-env needs :engine")
  (assert feeds "live-env needs :feeds")
  (assert bar-db "live-env needs :bar-db")
  (let [t (ct/create-live-calendar-time-generator)
        b (bg/create-bar-generator opts)]
    (s/consume
     (fn [calendar-time]
       (bg/finish-bar b calendar-time)
       (engine/set-calendar! engine calendar-time))
     (ct/get-time-stream t))
    {:engine engine
     :bar-generator b
     :time-generator t}))

(defn create-live-env-javelin [{:keys [bar-db] :as opts}]
  (let [engine (create-engine-javelin bar-db)
        opts (assoc opts :engine engine)]
    (create-live-env opts)))