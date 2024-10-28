(ns dev.date
  (:require
    [tick.core :as t]
    [ta.helper.date :as d]))

; !!!!! LOCAL timezone is used

(t/date (t/instant "2024-02-08T23:59:59.999999999Z"))
;=> #time/date"2024-02-08"
(t/date (t/instant "2024-02-08T00:00:00Z"))
;=> #time/date"2024-02-07"

(t/in (t/date-time (t/instant "2024-02-08T00:00:00Z")) "UTC")
;=> #time/zoned-date-time"2024-02-07T19:00Z[UTC]"

(t/date (t/zoned-date-time (t/instant "2024-02-08T00:00:00Z")))
;=> #time/date"2024-02-07"

(t/zoned-date-time (t/instant "2024-02-08T00:00:00Z"))
;=> #time/zoned-date-time"2024-02-07T21:00-05:00"

(t/date (t/zoned-date-time (t/instant "2024-02-08T00:00:00Z")))
; => #time/date"2024-02-07"

;
; CORRECT timezone
;
(t/in (t/instant "2024-02-08T00:00:00Z") "America/New_York")
; => #time/zoned-date-time"2024-02-07T19:00-05:00[America/New_York]"

(t/in (t/instant "2024-02-08T00:00:00Z") "UTC")
; => #time/zoned-date-time"2024-02-08T00:00Z[UTC]"

(t/date (t/zoned-date-time "2024-02-08T00:00:00Z"))
; => #time/date"2024-02-08"


(t/day-of-month (t/instant "2024-02-08T23:59:59.999999999Z"))
(t/year (t/instant "2024-02-08T23:59:59.999999999Z"))
(t/year-month (t/instant "2024-02-08T23:59:59.999999999Z"))

(t/last-day-of-month (t/in (t/instant "2024-02-08T00:00:00Z") "UTC"))

(t/day-of-week (t/instant "2024-02-08T00:00:00Z"))


(t/date? (t/date (t/zoned-date-time "2024-02-08T00:00:00Z")))
(t/date-time? (t/zoned-date-time "2024-02-08T00:00:00Z"))
(t/zoned-date-time? (t/zoned-date-time "2024-02-08T00:00:00Z"))

(t/at (t/date (t/in (t/instant "2024-02-08T00:00:00Z") "UTC")) (t/new-time 12 00))
