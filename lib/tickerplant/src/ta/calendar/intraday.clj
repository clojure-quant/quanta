(ns ta.calendar.intraday
  (:require
   [tick.core :as t]
   [ta.calendar.calendars :refer [intraday? overnight?]]
   [ta.calendar.day :refer [day-closed? day-open? next-open prior-open prior-close]]
  ))

(defn time-open? [{:keys [open close] :as calendar} dt]
  (let [time (t/time dt)]
    (cond
      (intraday? calendar) (and (t/>= time open)
                                (t/<= time close))
      (overnight? calendar) (let [day-before (t/<< dt (t/new-duration 1 :days))
                                  day-after (t/>> dt (t/new-duration 1 :days))]
                              (or (and (t/<= time close) (day-open? calendar day-before))
                                  (and (t/>= time open) (day-open? calendar day-after)))))))

(defn time-closed? [calendar dt]
  (not (time-open? calendar dt)))

(defn before-trading-hours? [{:keys [open close] :as calendar} dt]
  (let [time (t/time dt)
        day-before (t/<< dt (t/new-duration 1 :days))]
    (cond
      (intraday? calendar) (t/< time open)
      (overnight? calendar) (and (t/< time open)
                                 (day-closed? calendar day-before)))))

(defn after-trading-hours? [{:keys [open close] :as calendar} dt]
  (let [time (t/time dt)
        day-after (t/>> dt (t/new-duration 1 :days))]
    (cond
      (intraday? calendar) (t/> time close)
      (overnight? calendar) (and (t/> time close)
                                 (day-closed? calendar day-after)))))

(defn trading-open-time [{:keys [open timezone] :as calendar} d]
  (-> d
      (t/at open)
      (t/in timezone)))

(defn trading-close-time [{:keys [close timezone] :as calendar} d]
  (-> d
      (t/at close)
      (t/in timezone)))

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
      (prior-close calendar dt)
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