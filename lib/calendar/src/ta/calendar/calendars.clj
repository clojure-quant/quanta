(ns ta.calendar.calendars
  (:require
   [tick.core :as t]))

(def week-5
  (sorted-set
   t/MONDAY t/TUESDAY t/WEDNESDAY
   t/THURSDAY t/FRIDAY))

(def week-6
  (sorted-set
   t/MONDAY t/TUESDAY t/WEDNESDAY
   t/THURSDAY t/FRIDAY t/SATURDAY))

(def week-6-sunday
  (sorted-set
   t/SUNDAY t/MONDAY t/TUESDAY
   t/WEDNESDAY t/THURSDAY t/FRIDAY))

(def week-7
  (sorted-set
   t/MONDAY t/TUESDAY t/WEDNESDAY
   t/THURSDAY t/FRIDAY t/SATURDAY t/SUNDAY))

(def calendars
  {:forex {:open (t/new-time 17 0 0)
           :close (t/new-time 16 30 0)
           :week week-6-sunday
           :timezone "America/New_York"}
   :forex-no-asia {:open (t/new-time 3 0 0)
                   :close (t/new-time 16 0 0)
                   :week week-5
                   :timezone "America/New_York"}
   :crypto {:open (t/new-time 0 0 0)
            :close (t/new-time 0 0 0)
            :week week-7
            :timezone "UTC"}
   :us {:open (t/new-time 9 0 0)
        :close (t/new-time 17 0 0)
        :week week-5
        :timezone "America/New_York"}
   :us24 {:open (t/new-time 0 0 0)
          :close (t/new-time 0 0 0)
          :week week-5
          :timezone "America/New_York"}
   :eu {:open (t/new-time 9 0 0)
        :close (t/new-time 17 0 0)
        :week week-5
        :timezone "Europe/Berlin"}

   :jp {:open (t/new-time 9 0 0)
        :close (t/new-time 17 0 0)
        :week week-5
        :timezone "Asia/Tokyo"}

   :test-short {:open (t/new-time 9 0 0)
                :close (t/new-time 11 0 0)
                :week week-5
                :timezone "Asia/Tokyo"}

   :test-equal {:open (t/new-time 0 0 0)
                :close (t/new-time 0 0 0)
                :week week-5
                :timezone "Asia/Tokyo"}})

(defn calendar-exists? [calendar-kw]
  (contains? calendars calendar-kw))

(defn get-calendar [calendar-kw]
  (get calendars calendar-kw))

(defn get-calendar-list []
  (keys calendars))

(comment
  (contains? week-5 t/MONDAY)
  (contains? week-5 t/SUNDAY)
  (contains? week-7 t/SUNDAY)

  (calendar-exists? :us)
  (calendar-exists? :us555)

  (get-calendar-list)

;
  )