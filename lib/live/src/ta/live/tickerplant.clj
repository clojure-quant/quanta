(ns ta.live.tickerplant
  (:require
   [manifold.stream :as s]
   [ta.engine.protocol :as engine]
   [ta.algo.env.protocol :as algo-env]
   [ta.live.calendar-time :as ct]
   [ta.live.bar-generator :as bg]
   [ta.live.quote-manager :as qm]
   [ta.live.env-watcher :as watcher]))

(defn start-tickerplant [{:keys [algo-env feeds]}]
  (assert feeds "tickerplant needs :feeds")
  (assert algo-env "tickerplant needs :algo-env")
  (let [eng (algo-env/get-engine algo-env)
        q (qm/create-quote-manager feeds)
        quote-stream (qm/get-quote-stream q)   
        t (ct/create-live-calendar-time-generator)
        b (bg/create-bar-generator quote-stream (algo-env/get-bar-db algo-env))
        w (watcher/start-env-watcher algo-env q b)]
    (s/consume
     (fn [calendar-time]
       (bg/finish-bar b calendar-time)
       (engine/set-calendar! eng calendar-time))
     (ct/get-time-stream t))
    {:engine eng
     :quote-manager q
     :time-generator t
     :bar-generator b
     :watcher w}))



