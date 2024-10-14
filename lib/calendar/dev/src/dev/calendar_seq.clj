(ns dev.calendar-seq
  (:require
    [tick.core :as t]
    [quanta.calendar.core :refer [trailing-window calendar-seq calendar-seq-prior
                              fixed-window
                              close->open-dt open->close-dt]]
    [ta.calendar.calendars :refer [calendars]]
    [dev.utils :refer [to-utc]]))


(->> (t/in (t/date-time "2024-10-01T16:20:00") "America/New_York")
     (calendar-seq [:forex :d])
     (take 15))
;=>
;(#time/zoned-date-time"2024-09-30T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-02T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-03T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-04T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-07T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-08T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-09T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-10T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-11T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-14T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-15T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-16T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-17T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-18T16:30-04:00[America/New_York]")

(->> (t/in (t/date-time "2024-10-01T17:32:00") "America/New_York")
     (calendar-seq [:forex :h])
     (take 15))
;=>
;(#time/zoned-date-time"2024-10-01T16:30-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T18:00-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T19:00-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T20:00-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T21:00-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T22:00-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T23:00-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-02T00:00-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-02T01:00-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-02T02:00-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-02T03:00-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-02T04:00-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-02T05:00-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-02T06:00-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-02T07:00-04:00[America/New_York]")

(->> (t/in (t/date-time "2024-10-01T17:32:00") "America/New_York")
     (calendar-seq [:forex :m])
     (take 15))
;=>
;(#time/zoned-date-time"2024-10-01T17:32-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T17:33-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T17:34-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T17:35-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T17:36-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T17:37-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T17:38-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T17:39-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T17:40-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T17:41-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T17:42-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T17:43-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T17:44-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T17:45-04:00[America/New_York]"
;  #time/zoned-date-time"2024-10-01T17:46-04:00[America/New_York]")

(->> (t/in (t/date-time "2024-02-09T00:00:00") "America/New_York")
     (calendar-seq [:forex :h4])
     (take 15))
;=>
;(#time/zoned-date-time"2024-02-08T21:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-09T01:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-09T05:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-09T09:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-09T13:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T21:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-12T01:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-12T05:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-12T09:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-12T13:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-12T21:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-13T01:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-13T05:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-13T09:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-13T13:00-05:00[America/New_York]")

(->> (t/in (t/date-time "2024-02-11T06:00:00") "America/New_York")
     (calendar-seq [:forex :m15])
     (take 15))
;=>
;(#time/zoned-date-time"2024-02-09T16:30-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T17:15-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T17:30-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T17:45-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T18:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T18:15-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T18:30-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T18:45-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T19:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T19:15-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T19:30-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T19:45-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T20:00-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T20:15-05:00[America/New_York]"
;  #time/zoned-date-time"2024-02-11T20:30-05:00[America/New_York]")

(->> (t/in (t/date-time "2024-10-01T23:59:59.999999999") "UTC")
     (calendar-seq-prior [:crypto :d])
     (take 20))