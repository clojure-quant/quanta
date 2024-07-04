(ns ta.calendar.interval.year
  (:require
   [tick.core :as t]
   [ta.helper.date :refer [at-time]]
   [ta.calendar.interval.day :as day]
   [ta.calendar.helper :refer [trading-close-time day-with-close? day1]]))

;; helper

(defn year-close [calendar date]
  (let [last-day (t/last-day-of-year date)]
    (if (day-with-close? calendar last-day)
      (trading-close-time calendar last-day)
      (day/prior-close calendar (t/at last-day "00:00:00")))))

(defn prior-year [date]
  (let [first-day (t/first-day-of-year date)
        first-day-dt (t/at first-day "00:00:00")]
    (t/<< first-day-dt day1)))

(defn prior-year-close [calendar date]
  (let [prior-year (prior-year date)]
    (year-close calendar (t/date prior-year))))

(defn next-year-close [calendar date]
  (let [next-year (t/first-day-of-next-year date)]
    (year-close calendar (t/date next-year))))

;; close

(defn next-close [{:keys [timezone] :as calendar} dt]
  (let [zoned-dt (t/in dt timezone)
        zoned-date (t/date zoned-dt)
        year-close-dt (year-close calendar zoned-date)]
    (if (t/< zoned-dt year-close-dt)
      year-close-dt
      (next-year-close calendar zoned-date))))

(defn prior-close [{:keys [timezone] :as calendar} dt]
  (let [zoned-dt (t/in dt timezone)
        zoned-date (t/date zoned-dt)
        year-close-dt (year-close calendar zoned-date)]
    (if (t/> zoned-dt year-close-dt)
      year-close-dt
      (prior-year-close calendar zoned-date))))

(defn current-close [{:keys [timezone] :as calendar} dt]
  (let [zoned-dt (t/in dt timezone)
        zoned-date (t/date zoned-dt)
        year-close-dt (year-close calendar zoned-date)]
    (if (t/>= zoned-dt year-close-dt)
      year-close-dt
      (prior-year-close calendar zoned-date))))

(comment
  (require '[ta.calendar.calendars :as cal])

  (next-close (:us cal/calendars) (t/in (t/date-time "2024-03-04T12:00:00") "America/New_York"))
  (next-close (:us cal/calendars) (t/in (t/date-time "2024-12-31T17:00:00") "America/New_York"))

  (prior-close (:us cal/calendars) (t/in (t/date-time "2024-03-04T12:00:00") "America/New_York"))
  (prior-close (:us cal/calendars) (t/in (t/date-time "2024-12-31T17:00:00") "America/New_York"))
  (prior-close (:us cal/calendars) (t/in (t/date-time "2024-12-31T17:01:00") "America/New_York"))

  (current-close (:us cal/calendars) (t/in (t/date-time "2024-03-04T12:00:00") "America/New_York"))
  (current-close (:us cal/calendars) (t/in (t/date-time "2024-12-31T17:00:00") "America/New_York"))
  (current-close (:us cal/calendars) (t/in (t/date-time "2024-12-31T17:01:00") "America/New_York")))