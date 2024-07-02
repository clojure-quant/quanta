(ns quanta.model.backtest
  (:require
   [tick.core :as t]
   [taoensso.timbre :refer [trace debug info warn error]]
   [ta.calendar.core :as cal]
   [ta.calendar.combined :refer [combined-event-seq]]
   [quanta.model.protocol :as mp]))

(defn- dt->event-seq [calendars dt]
  (let [prior-dates (map (fn [[calendar-kw interval-kw]]
                           (cal/prior-close calendar-kw interval-kw dt))
                         calendars)
        ;prior-dates-sorted (sort prior-dates)
        ]
    (info "date event seq: " prior-dates)
    (map (fn [cal dt]
           {:calendar cal :time dt}) calendars prior-dates)))

(defn fire-backtest-events [model window-or-dt]
  (let [calendars (mp/active-calendars model)
        ; 3 modes to create an event-seq
        ; a map represents a window
        ; a date represents the last date for all calendars
        ; no date represents the current time.
        event-seq (if (map? window-or-dt)
                    (combined-event-seq calendars window-or-dt)
                    (dt->event-seq calendars (or window-or-dt
                                                 (-> (t/now) (t/in "UTC")))))]
    (info "backtesting: " window-or-dt " ..")
    ; fire calendar events
    (doall (map #(mp/set-calendar! model %) event-seq))
    (info "backtesting window: " window-or-dt "finished!")))