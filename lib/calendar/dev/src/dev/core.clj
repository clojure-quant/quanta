(ns dev.core
  (:require
    [tick.core :as t]
    [ta.calendar.calendars :refer [calendars]]
    [ta.calendar.interval :refer [intervals]]
    [ta.calendar.core :refer [close->open-dt open->close-dt current-close2]]
    [dev.utils :refer [to-utc]]))

(current-close2 [:crypto :m]
                    (t/in (t/date-time "2024-02-08T23:59:30") "UTC"))
;=> #time/zoned-date-time"2024-02-08T23:59Z[UTC]"

(current-close2 [:crypto :m]
                    (t/in (t/date-time "2024-02-08T23:59:59") "UTC"))
;=> #time/zoned-date-time"2024-02-08T23:59Z[UTC]"

(current-close2 [:crypto :m]
                    (t/in (t/date-time "2024-02-09T00:00:00") "UTC"))
;=> #time/zoned-date-time"2024-02-08T23:59:59.999999999Z[UTC]"

(let [calendar (:us calendars)
      interval (:m intervals)
      prior-open-dt (:prior-open interval)]
  (prior-open-dt calendar (t/in (t/date-time "2024-02-06T17:00") "America/New_York")))
;=> #time/zoned-date-time"2024-02-06T16:59-05:00[America/New_York]"

; TODO: bug
(let [calendar (:forex calendars)
      interval (:h intervals)
      prior-open-dt (:prior-open interval)]
  (prior-open-dt calendar (t/in (t/date-time "2024-02-08T16:30") "America/New_York")))
;=> #time/zoned-date-time"2024-02-02T15:30-05:00[America/New_York]"

; TODO: bug
(let [calendar (:forex calendars)
      interval (:h intervals)
      prior-open-dt (:prior-open interval)]
  (prior-open-dt calendar (t/in (t/date-time "2024-02-06T17:00") "America/New_York")))
;=> #time/zoned-date-time"2024-02-02T15:30-05:00[America/New_York]"

; TODO bug
(let [calendar (:forex calendars)
      interval (:h intervals)
      next-open-dt (:next-open interval)]
  (next-open-dt calendar (t/in (t/date-time "2024-02-06T15:30") "America/New_York")))
;=> #time/zoned-date-time"2024-02-07T17:00-05:00[America/New_York]"

; TODO bug
(let [calendar (:forex calendars)
      interval (:h intervals)
      prior-close-dt (:prior-close interval)]
  (prior-close-dt calendar (t/in (t/date-time "2024-02-06T17:00") "America/New_York")))
;=> #time/zoned-date-time"2024-02-06T16:00-05:00[America/New_York]"

(let [calendar (:forex calendars)
      interval (:h intervals)
      prior-close-dt (:prior-close interval)]
  (prior-close-dt calendar (t/in (t/date-time "2024-02-06T16:00") "America/New_York")))
;=> #time/zoned-date-time"2024-02-06T15:00-05:00[America/New_York]"

(let [calendar (:us calendars)
      interval (:d intervals)
      next-close-dt (:next-close interval)]
  (next-close-dt calendar (t/in (t/date-time "2024-02-06T15:30") "America/New_York")))
;=> #time/zoned-date-time"2024-02-06T17:00-05:00[America/New_York]"
