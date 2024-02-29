(ns ta.env.live
  (:require
   [manifold.stream :as s]
   [ta.engine.protocol :as engine]
   [ta.algo.env :as algo-env]
   [ta.env.live.calendar-time :as ct]
   [ta.env.live.bar-generator2 :as bg]))

(defn start-tickerplant [{:keys [algo-env feeds] :as opts}]
  (assert feeds "tickerplant needs :feeds")
  (let [algo-env (or algo-env 
                     (algo-env/create-env-javelin))
        eng (algo-env/get-engine algo-env)
        t (ct/create-live-calendar-time-generator)
        b (bg/create-bar-generator opts)]
    (s/consume
     (fn [calendar-time]
       (bg/finish-bar b calendar-time)
       (engine/set-calendar! eng calendar-time))
     (ct/get-time-stream t))
    {:time-generator t
     :bar-generator b
     :engine eng}))

