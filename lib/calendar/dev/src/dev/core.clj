(ns dev.core
  (:require
    [tick.core :as t]
    [ta.calendar.calendars :refer [calendars]]
    [ta.calendar.interval :refer [intervals]]
    [ta.calendar.helper :refer [trading-open-time]]
    [quanta.calendar.core :refer [next-close current-close]]))

(current-close [:crypto :m]
                    (t/in (t/date-time "2024-02-08T23:59:30") "UTC"))
;=> #time/zoned-date-time"2024-02-08T23:59Z[UTC]"

(current-close [:crypto :m]
                    (t/in (t/date-time "2024-02-08T23:59:59") "UTC"))
;=> #time/zoned-date-time"2024-02-08T23:59Z[UTC]"

(current-close [:crypto :m]
                    (t/in (t/date-time "2024-02-09T00:00:00") "UTC"))
;=> #time/zoned-date-time"2024-02-09T00:00Z[UTC]"

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


(let [eu-cal (:eu calendars)
      sun-00-27 (t/in (t/date-time "2024-10-13T00:27") (:timezone eu-cal))
      cur-close-dt (current-close [:eu :d] sun-00-27)
      cur-close-instant (t/instant cur-close-dt)
      next-close-dt (next-close [:eu :d] cur-close-dt)
      next-close-instant (next-close [:eu :d] cur-close-instant)]
  {:sun-00-27 sun-00-27
   :cur-close-dt cur-close-dt
   :cur-close-instant cur-close-instant
   :next-close-dt next-close-dt
   :next-close-instant next-close-instant})

(let [dt-str "2024-02-08T23:59:59.999999999"
      inst (-> (str dt-str "Z") (t/instant))
      utc (t/in (t/date-time dt-str) "UTC")]
  {:inst inst
   :utc utc})

(t/date (t/instant "2024-02-08T23:59:59.999999999"))

(let [calendar (:crypto calendars)
      interval (:d intervals)
      current-close-dt (:current-close interval)]
  (current-close-dt calendar (t/in (t/date-time "2024-02-08T23:59:59.999999999") "UTC")))
;=> #time/zoned-date-time"2024-02-08T00:00Z[UTC]"

(let [calendar (:crypto calendars)
      interval (:d intervals)
      current-open-dt (:current-open interval)]
  (current-open-dt calendar (t/in (t/date-time "2024-02-08T23:59:59.999999999") "UTC")))
;=> #time/zoned-date-time"2024-02-08T00:00Z[UTC]"

(let [t (t/instant "2024-02-08T23:59:59.999999999Z")]
  (trading-open-time (:crypto calendars) t))
;=> #time/zoned-date-time"2024-02-08T00:00Z[UTC]"


(let [calendar (:crypto calendars)
      interval (:h4 intervals)
      current-close-dt (:current-close interval)]
  (current-close-dt calendar (t/in (t/date-time "2024-02-10T12:00:00") "UTC")))
; => #time/zoned-date-time"2024-02-10T12:00Z[UTC]"


(let [calendar (:crypto calendars)
      interval (:m intervals)
      current-open-dt (:current-open interval)]
  (current-open-dt calendar (t/in (t/date-time "2024-02-08T23:59:00") "UTC")))
;=> #time/zoned-date-time"2024-02-08T23:59Z[UTC]"

(let [calendar (:crypto calendars)
      interval (:d intervals)
      current-open-dt (:current-open interval)]
  (current-open-dt calendar (t/in (t/date-time "2024-02-08T12:00:00") "UTC")))
;=> #time/zoned-date-time"2024-02-08T00:00Z[UTC]"

(let [calendar (:crypto calendars)
      interval (:d intervals)
      next-close (:next-close interval)]
  (next-close calendar (t/in (t/date-time "2024-02-08T00:00:00") "UTC")))
;=> #time/zoned-date-time"2024-02-09T00:00Z[UTC]"


(let [calendar (:crypto calendars)
      interval (:d intervals)
      next-close (:next-close interval)]
  (next-close calendar (t/in (t/date-time "2024-02-07T23:59:59") "UTC")))
;=> #time/zoned-date-time"2024-02-08T00:00Z[UTC]"

(let [calendar (:crypto calendars)
      interval (:m intervals)
      next-close (:next-close interval)]
  (next-close calendar (t/in (t/date-time "2024-02-07T23:59:59") "UTC")))
;=> #time/zoned-date-time"2024-02-08T00:00Z[UTC]"