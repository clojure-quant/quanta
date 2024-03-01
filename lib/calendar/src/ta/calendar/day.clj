(ns ta.calendar.day
  (:require
   [tick.core :as t]
   [ta.helper.date :refer [at-time]]
   [ta.calendar.helper :refer [before-trading-hours? after-trading-hours?
                               trading-open-time trading-close-time
                               time-open? time-closed?
                               day-open? day-closed?
                               day-with-open? day-with-close?
                               day1]]))

; next

(defn next-open [{:keys [open timezone] :as calendar} dt]
  "returns the next open day at open time"
  (let [next-dt (t/>> dt day1)
        next-day (t/date next-dt)]
    (if (day-with-open? calendar next-dt)
      (at-time next-day open timezone)
      (next-open calendar next-dt))))

(defn next-close [{:keys [close timezone] :as calendar} dt]
  "returns the next open day at close time"
  (let [next-dt (t/>> dt day1)
        next-day (t/date next-dt)]
    (if (day-with-close? calendar next-dt)
      (at-time next-day close timezone)
      (next-close calendar next-dt))))


; prior

(defn prior-open [{:keys [open timezone] :as calendar} dt]
  "returns the prior open day at open time"
  (let [prior-dt (t/<< dt day1)
        prior-day (t/date prior-dt)]
    (if (day-with-open? calendar prior-dt)
      (at-time prior-day open timezone)
      (prior-open calendar prior-dt))))

(defn prior-close [{:keys [close timezone] :as calendar} dt]
  "returns the prior open day at close time"
  (let [prior-dt (t/<< dt day1)
        prior-day (t/date prior-dt)]
    (if (day-with-close? calendar prior-dt)
      (at-time prior-day close timezone)
      (prior-close calendar prior-dt))))


; close

(defn next-close-dt [calendar dt]
  "like next-close, but also can return the close time of the same day when dt is before close time.
   (excluding the close interval boundary)"
  (if (and (day-open? calendar dt) (not (after-trading-hours? calendar dt true)))
    (trading-close-time calendar (t/date dt))
    (next-close calendar dt)))

(defn prior-close-dt [calendar dt]
  "like prior-close, but also can return the close time of the same day when dt is after trading-hours.
   (excluding the close interval boundary)"
  (if (and (day-open? calendar dt) (after-trading-hours? calendar dt))
    (trading-close-time calendar (t/date dt))
    (prior-close calendar dt)))

(defn current-close [calendar dt]
  "current close (including the close interval boundary)"
  (if (after-trading-hours? calendar dt true)
    (trading-close-time calendar (t/date dt))
    (prior-close calendar dt)))

; open

(defn current-open [calendar dt]
  "current open (including the open interval boundary)"
  (if (before-trading-hours? calendar dt true)
    (trading-open-time calendar (t/date dt))
    (->> (prior-close calendar dt)
         (current-open calendar))))

; TODO: next open, prior open

; upcomming (current / next)

;(defn upcoming-close [calendar dt]
;  "like next-close, but also can return the close time of the same day when dt is before trading-hours close time"
;  (if (and (day-open? calendar dt) (not (after-trading-hours? calendar dt)))
;    (trading-close-time calendar (t/date dt))
;    (next-close calendar dt)))
;
;(defn upcoming-open [calendar dt]
;  "like next-open, but also can return the open time of the same day when dt is before trading-hours"
;  (if (and (day-open? calendar dt) (before-trading-hours? calendar dt))
;    (trading-open-time calendar (t/date dt))
;    (next-open calendar dt)))


(comment 
  (require '[ta.calendar.calendars :refer [calendars]])
  (def us (:us calendars))
  us

  (require '[ta.calendar.interval :refer [now-calendar]])
  (now-calendar us)

  ;(next-day us (now-calendar us))
  (next-open us (now-calendar us))
  (next-close us (now-calendar us))

  ;(prior-day us (now-calendar us))
  (prior-open us (now-calendar us))
  (prior-close us (now-calendar us))

  (next-open us (t/in (t/date-time "2024-02-09T16:00:00") "America/New_York")) ; overnight: friday => sunday

  (current-close (:us calendars) (t/in (t/date-time "2024-02-26T16:00:00") "America/New_York"))
  (current-close (:us calendars) (t/in (t/date-time "2024-02-26T17:00:00") "America/New_York"))
  (current-close (:us calendars) (t/in (t/date-time "2024-02-26T17:01:00") "America/New_York"))

  (current-close (:us calendars) (t/in (t/date-time "2024-02-25T12:00:00") "America/New_York"))
  (current-close (:forex calendars) (t/in (t/date-time "2024-02-25T12:00:00") "America/New_York"))

  (prior-close-dt (:forex calendars) (t/in (t/date-time "2024-02-05T12:34:56") "America/New_York"))
  (prior-close-dt (:forex calendars) (t/in (t/date-time "2024-02-08T23:00:00") "America/New_York"))

  ;
  )