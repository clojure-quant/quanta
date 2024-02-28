(ns ta.calendar.day
  (:require
   [tick.core :as t]
   [ta.helper.date :refer [at-time]]
   [ta.calendar.helper :refer [before-trading-hours? after-trading-hours?
                               trading-open-time trading-close-time
                               time-open? time-closed?
                               day-open? day-closed?
                               day1]]))

; next

(defn next-day [{:keys [open timezone] :as calendar} dt]
  (let [dt-next (t/>> dt day1)
        day-next (t/date dt-next)]
    (if (day-closed? calendar day-next)
      (next-day calendar dt-next)
      (at-time day-next open timezone)
      )))

(defn next-open [{:keys [open timezone] :as calendar} dt]
  (let [next-dt (next-day calendar dt)
        next-day (t/date next-dt)]
    (at-time next-day open timezone)))

(defn next-close [{:keys [close timezone] :as calendar} dt]
  (let [next-dt (next-day calendar dt)
        next-day (t/date next-dt)]
    (at-time next-day close timezone)))


; prior

(defn prior-day [{:keys [open timezone] :as calendar} dt]
  (let [dt-prior (t/<< dt day1)
        day-prior (t/date dt-prior)]
    (if (day-closed? calendar day-prior)
      (prior-day calendar dt-prior)
      (at-time day-prior open timezone))))

(defn prior-open [{:keys [open timezone] :as calendar} dt]
  (let [prior-dt (prior-day calendar dt)
        prior-day (t/date prior-dt)]
    (at-time prior-day open timezone)))

(defn prior-close [{:keys [close timezone] :as calendar} dt]
  (let [prior-dt (prior-day calendar dt)
        prior-day (t/date prior-dt)]
    (at-time prior-day close timezone)))


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

  (next-day us (now-calendar us))
  (next-open us (now-calendar us))
  (next-close us (now-calendar us))

  (prior-day us (now-calendar us))
  (prior-open us (now-calendar us))
  (prior-close us (now-calendar us))

  (current-close (:us calendars) (t/in (t/date-time "2024-02-26T16:00:00") "America/New_York"))
  (current-close (:us calendars) (t/in (t/date-time "2024-02-26T17:00:00") "America/New_York"))
  (current-close (:us calendars) (t/in (t/date-time "2024-02-26T17:01:00") "America/New_York"))

  (current-close (:us calendars) (t/in (t/date-time "2024-02-25T12:00:00") "America/New_York"))
  (current-close (:forex calendars) (t/in (t/date-time "2024-02-25T12:00:00") "America/New_York"))

  ;
  )