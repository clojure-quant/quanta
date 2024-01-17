(ns ta.calendar.intraday
  (:require
   [tick.core :as t]
   [ta.calendar.day :refer [day-closed? next-open prior-open]]
  ))

(defn time-closed? [{:keys [open close] :as calendar} dt]
  (let [time (t/time dt)]
    (or (t/>= time close)
        (t/<= time open))))

(defn next-intraday [duration
                     {:keys [open] :as calendar} 
                     dt]
  (let [dt-next (t/>> dt duration)
        day-next (t/date dt-next)]
     (if (or (day-closed? calendar day-next)
             (time-closed? calendar dt-next))
       (next-open calendar dt)
       dt-next)))

(defn prior-intraday [duration
                     {:keys [open] :as calendar}
                     dt]
  (let [dt-prior (t/<< dt duration)
        day-prior (t/date dt-prior)]
    (if (or (day-closed? calendar day-prior)
            (time-closed? calendar dt-prior))
      (prior-open calendar dt)
      dt-prior)))

(comment 
  (require '[ta.calendar.calendars :refer [calendars]])
  (def dt (t/at (t/new-date 2023 1 5) (t/new-time 18 30 1)))
  dt
  (t/time dt)
  (time-closed? (:us calendars) dt)
  (day-closed? (:us calendars) dt)
  (next-open (:us calendars) dt)
  (next-intraday (t/new-duration 1 :hours) (:us calendars) dt)

  (def dt2 (t/at (t/new-date 2023 1 5) (t/new-time 11 0 0)))
  (t/day-of-week dt2)
  (time-closed? (:us calendars) dt2)
  (day-closed? (:us calendars) dt2)
  (next-open (:us calendars) dt2)
  (next-intraday (t/new-duration 1 :hours)  (:us calendars) dt2)
  (next-intraday  (t/new-duration 1 :minutes)  (:us calendars)  dt2)
 
  (prior-intraday (t/new-duration 1 :hours)  (:us calendars) dt2)
  (prior-intraday  (t/new-duration 1 :minutes)  (:us calendars)  dt2)

 ; 
  )