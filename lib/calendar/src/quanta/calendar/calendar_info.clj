(ns quanta.calendar.calendar-info
  (:require
   [tick.core :as t]
   [quanta.calendar.core :as cal]
   [ta.calendar.calendars :as caldb]
   [ta.calendar.helper :as calhelp]))

(defn market-info [market-kw]
  (let [cal (caldb/get-calendar market-kw)
        dt (t/instant)
        dt-cal (-> dt
                   (t/zoned-date-time)
                   (t/in (:timezone cal)))
        open? (calhelp/time-open? cal dt-cal)
        business? (calhelp/day-open? cal dt-cal)]
    {:calendar market-kw
     :open open?
     :business business?
     :calendar-time (t/date-time dt-cal)}))

(defn gather-calendar [[calendar-kw interval-kw] dt]
  (let [current-close-dt (cal/current-close [calendar-kw interval-kw] dt)]
    (assoc (market-info calendar-kw)
           :calendar [calendar-kw interval-kw]
       ;:prior (t/instant (cal/prior-close calendar-kw interval-kw dt))
           :current (t/instant current-close-dt)
           :next (t/instant (cal/next-close [calendar-kw interval-kw] current-close-dt)))))

(comment
  (market-info :crypto)
  (market-info :eu)

  (gather-calendar [:crypto :m] (t/instant))

;
  )
