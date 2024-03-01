(ns ta.live.tickerplant
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [manifold.stream :as s]
   [ta.engine.protocol :as engine]
   [ta.algo.env.protocol :as algo-env]
   [ta.live.calendar-time :as ct]
   [ta.live.bar-generator :as bg]
   [ta.live.quote-manager :as qm]
   [ta.live.env-watcher :as watcher]
   [ta.live.bar-generator.db :as db]))

(defn start-tickerplant [{:keys [algo-env quote-manager]}]
  (assert quote-manager "tickerplant needs :quote-manager")
  (assert algo-env "tickerplant needs :algo-env")
  (let [eng (algo-env/get-engine algo-env)
        quote-stream (qm/get-quote-stream quote-manager)
        t (ct/create-live-calendar-time-generator)
        b (bg/create-bar-generator quote-stream (algo-env/get-bar-db algo-env))
        w (watcher/start-env-watcher algo-env quote-manager b t)]
    (s/consume
     (fn [calendar-time]
       ; this logic is crucial.
       ; first the bar-generator has to finish the bars.
       ; then the algo-env can do the calculations (so they will get up to date bars)
       (warn "finishing bars for: " calendar-time)
       (try 
         (bg/finish-bar b calendar-time)
         (catch Exception ex
           (error "exception in finishing bars " calendar-time)))
       (try 
         (engine/set-calendar! eng calendar-time)  
         (catch Exception ex
           (error "exception in setting calendar time: " calendar-time))))
     (ct/get-time-stream t))
    {:engine eng
     :time-generator t
     :bar-generator b
     :watcher w}))

(defn current-bars [state calendar]
  (let [db (bg/get-db (:bar-generator state))
        bar-a-seq (db/get-bar-atoms-for-calendar db calendar)]
    (map deref bar-a-seq)))

