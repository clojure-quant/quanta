(ns ta.algo.backtest
  (:require
   [tick.core :as t]
   [taoensso.timbre :refer [trace debug info warn error]]
   [ta.calendar.core :as cal]
   [ta.calendar.combined :refer [combined-event-seq]]
   [quanta.model.protocol :as eng]
   [ta.algo.env.protocol :as algo-env]
   [ta.algo.env :as algo-env-impl]))

(defn- dt->event-seq [calendars dt]
  (let [prior-dates (map (fn [[calendar-kw interval-kw]]
                           (cal/prior-close calendar-kw interval-kw dt))
                         calendars)
        ;prior-dates-sorted (sort prior-dates)
        ]
    (map (fn [cal dt]
           {:calendar cal :time dt}) calendars prior-dates)))

(defn- fire-calendar-events [engine window-or-dt]
  (let [calendars (eng/active-calendars engine)
        ; 3 modes to create an event-seq
        ; a map represents a window
        ; a date represents the last date for all calendars
        ; no date represents the current time.
        event-seq (if (map? window-or-dt) 
                    (combined-event-seq calendars window-or-dt)
                    (dt->event-seq calendars (or window-or-dt 
                                              (-> (t/now)(t/in "UTC")))))
        ]
    (info "backtesting: " window-or-dt " ..")
    ; fire calendar events
    (doall (map #(eng/set-calendar! engine %) event-seq))
    (info "backtesting window: " window-or-dt "finished!")))

(defn backtest-algo
  "runs an algo with data powered by bar-db.
   returns the result of the strategy."
  [bar-db algo-spec window-or-dt]
  (let [env (algo-env-impl/create-env-javelin bar-db)
        strategy (algo-env/add-algo env algo-spec) 
        engine (algo-env/get-engine env)]
    (fire-calendar-events engine window-or-dt)
    strategy))