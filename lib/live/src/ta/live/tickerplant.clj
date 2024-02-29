(ns ta.live.tickerplant
  (:require
   [manifold.stream :as s]
   [ta.engine.protocol :as engine]
   [ta.algo.env.protocol :as algo-env]
   [ta.live.calendar-time :as ct]
   [ta.live.bar-generator :as bg]))

(defn start-tickerplant [{:keys [algo-env feeds] :as opts}]
  (assert feeds "tickerplant needs :feeds")
  (assert algo-env "tickerplant needs :algo-env")
  (let [eng (algo-env/get-engine algo-env)
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

