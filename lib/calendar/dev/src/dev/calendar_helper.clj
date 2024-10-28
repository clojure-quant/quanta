(ns dev.calendar-helper
  (:require [tick.core :as t]
            [ta.calendar.calendars :refer [calendars]]
            [ta.helper.date :refer [at-time same-date?]]
            [ta.calendar.helper :refer [next-day-at-midnight time-open?]]
            [dev.utils :refer [to-utc]]))

(def dt (to-utc "2024-02-08T23:59:00"))
(def next-day (t/>> dt (t/new-duration 1 :days)))

(at-time (t/date next-day)
         (t/new-time 0 0 0) "UTC")
;=> #time/zoned-date-time"2024-02-09T00:00Z[UTC]"

(next-day-at-midnight {:timezone "UTC"} dt)
;=> #time/zoned-date-time"2024-02-09T00:00Z[UTC]"

(time-open? (:eu calendars) (to-utc "2024-10-11T16:00:00"))
;=> true
(time-open? (:eu calendars) (t/instant))


(time-open? (:us calendars) (to-utc "2024-10-11T16:00:00"))
;=> true
(time-open? (:us calendars) (t/instant))
