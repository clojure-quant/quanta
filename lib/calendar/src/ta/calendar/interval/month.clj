(ns ta.calendar.interval.month
  (:require
   [tick.core :as t]
   [ta.calendar.interval.day :as day]
   [ta.calendar.helper :refer [trading-close-time day-with-close? day1]]))

;; helper

(defn month-close [calendar date]
  (let [last-day (t/last-day-of-month date)]
    (if (day-with-close? calendar last-day)
      (trading-close-time calendar last-day)
      (day/prior-close calendar (t/at last-day "00:00:00")))))

(defn prev-month [date]
  (let [first-day (t/first-day-of-month date)
        first-day-dt (t/at first-day "00:00:00")]
    (t/<< first-day-dt day1)))

(defn prior-month-close [calendar date]
  (let [prev-month (prev-month date)]
    (month-close calendar (t/date prev-month))))

(defn next-month-close [calendar date]
  (let [next-month (t/first-day-of-next-month date)]
    (month-close calendar (t/date next-month))))

;; close

(defn next-close [{:keys [timezone] :as calendar} dt]
  (let [zoned-dt (t/in dt timezone)
        zoned-date (t/date zoned-dt)
        month-close-dt (month-close calendar zoned-date)]
    (if (t/< zoned-dt month-close-dt)
      month-close-dt
      (next-month-close calendar zoned-date))))

(defn prior-close [{:keys [timezone] :as calendar} dt]
  (let [zoned-dt (t/in dt timezone)
        zoned-date (t/date zoned-dt)
        month-close-dt (month-close calendar zoned-date)]
    (if (t/> zoned-dt month-close-dt)
      month-close-dt
      (prior-month-close calendar zoned-date))))

(defn current-close [{:keys [timezone] :as calendar} dt]
  (let [zoned-dt (t/in dt timezone)
        zoned-date (t/date zoned-dt)
        month-close-dt (month-close calendar zoned-date)]
    (if (t/>= zoned-dt month-close-dt)
      month-close-dt
      (prior-month-close calendar zoned-date))))

;; TODO next, prior current open

(comment
  (require '[ta.calendar.calendars :as cal])

  (next-close (:us cal/calendars) (t/in (t/date-time "2024-03-04T12:00:00") "America/New_York"))
  (next-close (:us cal/calendars) (t/in (t/date-time "2024-04-04T12:00:00") "America/New_York"))
  (next-close (:us cal/calendars) (t/in (t/date-time "2024-04-30T17:00:00") "America/New_York"))

  (prior-close (:us cal/calendars) (t/in (t/date-time "2024-03-04T12:00:00") "America/New_York"))
  (prior-close (:us cal/calendars) (t/in (t/date-time "2024-04-04T12:00:00") "America/New_York"))
  (prior-close (:us cal/calendars) (t/in (t/date-time "2024-04-30T17:00:00") "America/New_York"))
  (prior-close (:us cal/calendars) (t/in (t/date-time "2024-04-30T17:01:00") "America/New_York"))

  (current-close (:us cal/calendars) (t/in (t/date-time "2024-03-04T12:00:00") "America/New_York"))
  (current-close (:us cal/calendars) (t/in (t/date-time "2024-02-04T12:00:00") "America/New_York"))

  (current-close (:us cal/calendars) (t/in (t/date-time "2024-01-31T16:59:59") "America/New_York"))
  (current-close (:us cal/calendars) (t/in (t/date-time "2024-01-31T17:00:00") "America/New_York"))
  (current-close (:us cal/calendars) (t/in (t/date-time "2024-01-31T17:00:01") "America/New_York"))

  (month-close (:us cal/calendars) (t/date "2024-03-04"))

  (t/first-day-of-month (t/date "2024-03-04"))
  (t/last-day-of-month (t/date "2024-03-04"))
  (t/<< (t/date-time "2024-03-04T00:00:00") (t/new-duration 1 :days))
  (t/at (t/date "2024-03-04") "00:00:00")
  (prev-month (t/date "2024-03-04"))

;  
  )
