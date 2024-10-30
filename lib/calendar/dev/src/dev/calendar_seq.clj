(ns dev.calendar-seq
  (:require
    [tick.core :as t]
    [quanta.calendar.core :refer [calendar-seq calendar-seq-prior calendar-seq-prior-open
                                  fixed-window-open fixed-window]]))


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
     (take 10))
;=>
;(#time/zoned-date-time"2024-10-01T00:00Z[UTC]"
;     #time/zoned-date-time"2024-09-30T00:00Z[UTC]"
;     #time/zoned-date-time"2024-09-29T00:00Z[UTC]"
;     #time/zoned-date-time"2024-09-28T00:00Z[UTC]"
;     #time/zoned-date-time"2024-09-27T00:00Z[UTC]"
;     #time/zoned-date-time"2024-09-26T00:00Z[UTC]"
;     #time/zoned-date-time"2024-09-25T00:00Z[UTC]"
;     #time/zoned-date-time"2024-09-24T00:00Z[UTC]"
;     #time/zoned-date-time"2024-09-23T00:00Z[UTC]"
;     #time/zoned-date-time"2024-09-22T00:00Z[UTC]")


(-> (fixed-window [:crypto :h] {:start (t/instant "2024-01-01T01:00:00Z")
                                :end (t/instant "2024-01-02T00:00:00Z")})
    count)
; => 24

(-> (fixed-window [:crypto :h] {:start (t/instant "2024-01-01T01:00:00Z")
                                :end (t/instant "2024-01-01T23:59:59.999999999Z")})
    count)
; => 23


(-> (fixed-window [:crypto :d] {:start (t/instant "2024-01-01T00:00:00Z")
                                :end (t/instant "2024-01-07T00:00:00Z")}))
;=>
;(#time/zoned-date-time"2024-01-07T00:00Z[UTC]"
;     #time/zoned-date-time"2024-01-06T00:00Z[UTC]"
;     #time/zoned-date-time"2024-01-05T00:00Z[UTC]"
;     #time/zoned-date-time"2024-01-04T00:00Z[UTC]"
;     #time/zoned-date-time"2024-01-03T00:00Z[UTC]"
;     #time/zoned-date-time"2024-01-02T00:00Z[UTC]"
;     #time/zoned-date-time"2024-01-01T00:00Z[UTC]")

(-> (fixed-window-open [:crypto :d] {:start (t/instant "2024-09-01T00:00:00Z")
                                     :end (t/instant "2024-09-08T00:00:00Z")}))
;=>
;(#time/zoned-date-time"2024-09-08T00:00Z[UTC]"
;  #time/zoned-date-time"2024-09-07T00:00Z[UTC]"
;  #time/zoned-date-time"2024-09-06T00:00Z[UTC]"
;  #time/zoned-date-time"2024-09-05T00:00Z[UTC]"
;  #time/zoned-date-time"2024-09-04T00:00Z[UTC]"
;  #time/zoned-date-time"2024-09-03T00:00Z[UTC]"
;  #time/zoned-date-time"2024-09-02T00:00Z[UTC]"
;  #time/zoned-date-time"2024-09-01T00:00Z[UTC]")

(-> (fixed-window-open [:crypto :d] {:start (t/instant "2024-01-01T23:59:59.999999999Z")
                                     :end (t/instant "2024-01-05T23:59:59.999999999Z")}))
; NOTE: start is not valid bar open time.
;=>
;(#time/zoned-date-time"2024-01-05T00:00Z[UTC]"
;  #time/zoned-date-time"2024-01-04T00:00Z[UTC]"
;  #time/zoned-date-time"2024-01-03T00:00Z[UTC]"
;  #time/zoned-date-time"2024-01-02T00:00Z[UTC]")

(->> (t/in (t/instant "2024-01-02T00:00:00Z") "UTC")
     (calendar-seq-prior [:crypto :d])
     (take 2))
;=> (#time/zoned-date-time"2024-01-02T00:00Z[UTC]"
;    #time/zoned-date-time"2024-01-01T00:00Z[UTC]")

(->> (t/in (t/instant "2024-01-02T00:00:00Z") "UTC")
     (calendar-seq-prior-open [:crypto :d])
     (take 2))
;=> (#time/zoned-date-time"2024-01-02T00:00Z[UTC]"
;    #time/zoned-date-time"2024-01-01T00:00Z[UTC]")

(->> (t/in (t/date-time "2024-01-02T23:59:59.999999999") "UTC")
     (calendar-seq-prior-open [:crypto :d])
     (take 1))
;=> (#time/zoned-date-time"2024-01-02T00:00Z[UTC]")

(->> (t/in (t/date-time "2024-10-01T00:05:00") "UTC")
     (calendar-seq-prior [:crypto :m])
     (take 10))
;=>
;(#time/zoned-date-time"2024-10-01T00:05Z[UTC]"
;     #time/zoned-date-time"2024-10-01T00:04Z[UTC]"
;     #time/zoned-date-time"2024-10-01T00:03Z[UTC]"
;     #time/zoned-date-time"2024-10-01T00:02Z[UTC]"
;     #time/zoned-date-time"2024-10-01T00:01Z[UTC]"
;     #time/zoned-date-time"2024-10-01T00:00Z[UTC]"
;     #time/zoned-date-time"2024-09-30T23:59Z[UTC]"
;     #time/zoned-date-time"2024-09-30T23:58Z[UTC]"
;     #time/zoned-date-time"2024-09-30T23:57Z[UTC]"
;     #time/zoned-date-time"2024-09-30T23:56Z[UTC]")

(-> (fixed-window [:crypto :m] {:start (t/instant "2024-09-30T23:56:00Z")
                                :end (t/instant "2024-10-01T00:05:00Z")}))
;=>
;(#time/zoned-date-time"2024-10-01T00:05Z[UTC]"
;     #time/zoned-date-time"2024-10-01T00:04Z[UTC]"
;     #time/zoned-date-time"2024-10-01T00:03Z[UTC]"
;     #time/zoned-date-time"2024-10-01T00:02Z[UTC]"
;     #time/zoned-date-time"2024-10-01T00:01Z[UTC]"
;     #time/zoned-date-time"2024-10-01T00:00Z[UTC]"
;     #time/zoned-date-time"2024-09-30T23:59Z[UTC]"
;     #time/zoned-date-time"2024-09-30T23:58Z[UTC]"
;     #time/zoned-date-time"2024-09-30T23:57Z[UTC]"
;     #time/zoned-date-time"2024-09-30T23:56Z[UTC]")

(fixed-window-open [:crypto :m] {:start (t/in (t/date-time "2024-02-05T23:55:00") "UTC")
                                 :end (t/in (t/date-time "2024-02-05T23:59:00") "UTC")})
;=>
;(#time/zoned-date-time"2024-02-05T23:59Z[UTC]"
;     #time/zoned-date-time"2024-02-05T23:58Z[UTC]"
;     #time/zoned-date-time"2024-02-05T23:57Z[UTC]"
;     #time/zoned-date-time"2024-02-05T23:56Z[UTC]"
;     #time/zoned-date-time"2024-02-05T23:55Z[UTC]")

(->> (t/in (t/date-time "2024-02-05T23:59:00") "UTC")
     (calendar-seq-prior-open [:crypto :m])
     (take 5))
;=>
;(#time/zoned-date-time"2024-02-05T23:59Z[UTC]"
;     #time/zoned-date-time"2024-02-05T23:58Z[UTC]"
;     #time/zoned-date-time"2024-02-05T23:57Z[UTC]"
;     #time/zoned-date-time"2024-02-05T23:56Z[UTC]"
;     #time/zoned-date-time"2024-02-05T23:55Z[UTC]")

