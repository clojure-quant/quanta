(ns quanta.notebook.calendar-window
  (:require 
   [tick.core :as t]
   [ta.calendar.core :as cal]))



  (cal/now-calendar :us)
  (cal/now-calendar :eu)

  (cal/next-close :us :d (cal/now-calendar :us))
  (cal/next-close :us :h (cal/now-calendar :us))

  (cal/prior-close :us :d (cal/now-calendar :us))
  (cal/prior-close :us :h (cal/now-calendar :us))

  (cal/current-close :us :d)
  (cal/current-close :us :h)
  (cal/current-close :us :m)

  (take 5 (cal/calendar-seq :us :d))
  (take 5 (cal/calendar-seq-instant [:us :d]))

  (take 5 (cal/calendar-seq :eu :d))
  (take 5 (cal/calendar-seq :forex :m))

  (take 30 (cal/calendar-seq :us :d))
  (take 100 (cal/calendar-seq :eu :h))
  (take 100 (cal/calendar-seq-prior [:eu :h] (t/date-time "2023-01-01T00:00:00")))

  (take 5 (cal/calendar-seq :eu :h))

  (cal/trailing-window [:us :d] 5)
  (cal/trailing-window [:us :d] 10)
  (cal/trailing-window [:us :h] 5)

  (cal/get-bar-duration [:us :d])
  (cal/get-bar-duration [:us :m])

  (-> (cal/fixed-window [:us :d] {:start (t/date-time "2023-01-01T00:00:00")
                              :end (t/date-time "2023-02-01T00:00:00")})
      cal/calendar-seq->range)

  (-> (cal/trailing-window :us :d 5)
      cal/calendar-seq->range)

  (cal/trailing-range [:us :d] 2)
  (cal/trailing-range [:us :m] 10 (t/in (t/date-time "2023-02-01T12:00:00") "America/New_York"))

