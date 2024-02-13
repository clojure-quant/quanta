(ns ta.calendar.calendars
  (:require
   [tick.core :as t]))

(def week-5
  #{t/MONDAY t/TUESDAY t/WEDNESDAY
    t/THURSDAY t/FRIDAY})

(def week-6
  #{t/MONDAY t/TUESDAY t/WEDNESDAY
    t/THURSDAY t/FRIDAY
    t/SATURDAY})

(def week-6-sunday
  #{t/SUNDAY t/MONDAY t/TUESDAY
    t/WEDNESDAY t/THURSDAY t/FRIDAY})

(def week-7
  #{t/MONDAY t/TUESDAY t/WEDNESDAY
    t/THURSDAY t/FRIDAY
    t/SATURDAY t/SUNDAY})


(def calendars
  {:forex {:open (t/new-time 17 0 0)
           :close (t/new-time 16 30 0)
           :week week-6-sunday
           :timezone "America/New_York"}
   :crypto {:open (t/new-time 0 0 0)
            :close (t/new-time 23 59 59)
            :week week-7
            :timezone "UTC"}
   :us1 {:open (t/new-time 9 0 0) 
        :close (t/new-time 17 0 0) 
        :week week-5
        :timezone "America/New_York"}
   :us {:open (t/new-time 0 0 0)
        :close (t/new-time 23 59 59)
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


   })

(defn intraday? [{:keys [open close] :as calendar}]
  (t/< open close))

(defn overnight? [{:keys [open close] :as calendar}]
  (t/>= open close))






(comment
  (contains? week-5 t/MONDAY)
  (contains? week-5 t/SUNDAY)
  (contains? week-7 t/SUNDAY)

;
)