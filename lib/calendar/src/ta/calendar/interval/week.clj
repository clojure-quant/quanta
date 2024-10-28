(ns ta.calendar.interval.week
  (:require
   [tick.core :as t]
   [ta.calendar.interval.day :as day]))

;; helper

(defn prior-day-of-week-iter [calendar dt]
  (let [cur-dt (day/prior-close-dt calendar dt)
        prior-fn (partial day/prior-close-dt calendar)]
    (iterate prior-fn cur-dt)))

(defn next-day-of-week-iter [calendar dt]
  (let [cur-dt (day/next-close-dt calendar dt)
        next-fn (partial day/next-close-dt calendar)]
    (iterate next-fn cur-dt)))

;; close

(defn next-close [{:keys [timezone] :as calendar} dt]
  (let [zoned-dt (t/in dt timezone)
        last-day-of-week (last (:week calendar))]
    (->> (next-day-of-week-iter calendar zoned-dt)
         (drop-while #(not= last-day-of-week (t/day-of-week %)))
         (first))))

(defn prior-close [{:keys [timezone] :as calendar} dt]
  (let [zoned-dt (t/in dt timezone)
        last-day-of-week (last (:week calendar))]
    (->> (prior-day-of-week-iter calendar zoned-dt)
         (drop-while #(not= last-day-of-week (t/day-of-week %)))
         (first))))

(defn current-close [{:keys [timezone] :as calendar} dt]
  (let [zoned-dt (t/in dt timezone)
        cur-dt (day/current-close calendar zoned-dt)
        last-day-of-week (last (:week calendar))]
    (if (and (= (t/day-of-week cur-dt) last-day-of-week)
             (t/= (t/date cur-dt) (t/date dt)))
      cur-dt
      (prior-close calendar zoned-dt))))

(comment
  (require '[ta.calendar.calendars :as cal])
  (def us (:us cal/calendars))
  us

  (next-close us (t/in (t/date-time "2024-03-01T16:00:00") "America/New_York"))
  (next-close us (t/in (t/date-time "2024-03-01T17:00:00") "America/New_York"))
  (next-close us (t/in (t/date-time "2024-03-01T17:01:00") "America/New_York"))

  (current-close us (t/in (t/date-time "2024-03-01T16:00:00") "America/New_York"))
  (current-close us (t/in (t/date-time "2024-03-01T17:00:00") "America/New_York"))
  (current-close us (t/in (t/date-time "2024-03-01T17:01:00") "America/New_York"))

  (prior-close us (t/in (t/date-time "2024-03-01T17:00:00") "America/New_York"))
  (prior-close us (t/in (t/date-time "2024-03-01T17:01:00") "America/New_York"))
  (prior-close us (t/in (t/date-time "2024-03-02T00:01:00") "America/New_York"))

  (prior-close us (t/in (t/date-time "2024-03-04T12:00:00") "America/New_York"))
  (prior-close us (t/in (t/date-time "2024-03-05T12:00:00") "America/New_York"))
  (prior-close us (t/in (t/date-time "2024-03-06T12:00:00") "America/New_York"))
  (prior-close us (t/in (t/date-time "2024-03-07T12:00:00") "America/New_York"))
  (prior-close us (t/in (t/date-time "2024-03-08T12:00:00") "America/New_York"))
  (prior-close us (t/in (t/date-time "2024-03-09T12:00:00") "America/New_York"))

  (take 1 (prior-day-of-week-iter us (t/in (t/date-time "2024-03-04T12:00:00") "America/New_York")))
  (day/current-close (:us cal/calendars) (t/in (t/date-time "2024-03-04T12:00:00") "America/New_York"))

  (last (:week us))

  (= t/FRIDAY t/FRIDAY))