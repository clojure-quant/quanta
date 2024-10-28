(ns ta.calendar.helper
  (:require
   [tick.core :as t]
   [ta.helper.date :refer [at-time same-date? now-in-zone]]))

(def day1 (t/new-duration 1 :days))

(defn now-calendar [{:keys [timezone] :as calendar}]
  (now-in-zone timezone))

(defn dt->calendar-dt [{:keys [timezone] :as calendar} dt]
  (t/in dt timezone))

(defn day-open? [{:keys [week] :as calendar} dt]
  (let [day (t/day-of-week dt)]
    (contains? week day)))

(defn day-closed? [calendar dt]
  (not (day-open? calendar dt)))

(defn intraday? [{:keys [open close] :as calendar}]
  (t/< open close))

(defn overnight? [{:keys [open close] :as calendar}]
  (t/>= open close))

(defn time-open?
  "expecting a zoned dt in the same timezone as the calendar timezone"
  [{:keys [open close] :as calendar} dt]
  (let [time (t/time dt)]
    (cond
      (day-closed? calendar dt) false
      (intraday? calendar) (and (t/>= time open)
                                (t/<= time close))
      (overnight? calendar) (let [day-before (t/<< dt (t/new-duration 1 :days))
                                  day-after (t/>> dt (t/new-duration 1 :days))]
                              (or (and (t/<= time close) (day-open? calendar day-before))
                                  (and (t/>= time open) (day-open? calendar day-after)))))))

(defn time-closed? [calendar dt]
  (not (time-open? calendar dt)))

(defn day-with-close?
  "checks if the given day closes"
  [calendar dt]
  (if (intraday? calendar)
    (day-open? calendar dt)
    ; overnight
    (let [day-before (t/<< dt (t/new-duration 1 :days))]
      (and (day-open? calendar dt)
           (day-open? calendar day-before)))))

(defn day-with-open?
  "checks if the given day opens"
  [calendar dt]
  (if (intraday? calendar)
    (day-open? calendar dt)
    ; overnight
    (let [day-after (t/>> dt (t/new-duration 1 :days))]
      (and (day-open? calendar dt)
           (day-open? calendar day-after)))))

(defn before-trading-hours?
  "default behavoir: checks if dt < calendar open time inside the current trading day
   customization:
   - open and close can be custom values
   - the open time boundary can be included with the include-open? flag"
  ; default
  ([{:keys [open close] :as calendar} dt]
   (before-trading-hours? calendar dt open close false))
  ; include open flag
  ([{:keys [open close] :as calendar} dt include-open?]
   (before-trading-hours? calendar dt open close include-open?))
  ; custom open close
  ([calendar dt open close]
   (before-trading-hours? calendar dt open close false))
  ; base
  ([calendar dt open close include-open?]
   (let [lt (if include-open? t/<= t/<)
         time (t/time dt)]
     (cond
       (day-closed? calendar dt) false             ; no trading day
        ;; |...[... day ...]...|
       (intraday? calendar)  (lt time open)
        ;; |... old day ...]...[... new day ...|    ; with previous and next trading day part
        ;; |...................[... new day ...|    ; no previous trading day part
       (overnight? calendar) (let [day-after (t/>> dt (t/new-duration 1 :days))]
                               (and (lt time open)
                                    (day-open? calendar day-after)))))))

(defn after-trading-hours?
  "default behavoir: checks if dt > calendar close time inside the current trading day
   customization:
   - open and close can be custom values
   - the close time boundary can be included with the include-close? flag"
  ; default
  ([{:keys [open close] :as calendar} dt]
   (after-trading-hours? calendar dt open close false))
  ; include close flag
  ([{:keys [open close] :as calendar} dt include-close?]
   (after-trading-hours? calendar dt open close include-close?))
  ; custom open close
  ([calendar dt open close]
   (after-trading-hours? calendar dt open close false))
  ; base
  ([calendar dt open close include-close?]
   (let [gt (if include-close? t/>= t/>)
         time (t/time dt)]
     (cond
       (day-closed? calendar dt) false             ; no trading day
        ;; |...[... day ...]...|
       (intraday? calendar) (gt time close)
        ;; |... old day ...]...[... new day ...|    ; with previous and next trading day part
        ;; |... old day ...]...................|    ; no next trading day part
       (overnight? calendar) (let [day-before (t/<< dt (t/new-duration 1 :days))]
                               (and (gt time close)
                                    (day-open? calendar day-before)))))))

(defn day-has-prior-close?
  "overnight: if day-before is open then the day has a close on 00:00 (earliest time at a day) and should return always true"
  [calendar dt first-close]
  (let [time (t/time dt)]
    (cond
      (day-closed? calendar dt) false
      (intraday? calendar) (t/>= time first-close)
      (overnight? calendar) (let [day-before (t/<< dt (t/new-duration 1 :days))
                                  day-after (t/>> dt (t/new-duration 1 :days))]
                              (or (day-open? calendar day-before)
                                  (and (t/>= time first-close)
                                       (day-open? calendar day-after)))))))

(defn day-has-next-close?
  "NOTE: dt has to be valid (aligned to interval by ta.calendar.interval.intraday/dt-base)"
  [{:keys [calendar close dt dt-next]}]
  (let [time (t/time dt-next)]
    (cond
      (day-closed? calendar dt-next) false
      (intraday? calendar) (t/<= time close)
      (overnight? calendar) (let [day-before (t/<< dt-next (t/new-duration 1 :days))
                                  day-after (t/>> dt-next (t/new-duration 1 :days))]
                              (or
                                ; before day close => a future bar exists (the close bar itself)
                               (and (t/<= time close)
                                    (day-open? calendar day-before))

                                ; because dt-next is valid and aligned, it is a future bar when inside same day
                               (and (same-date? dt dt-next)
                                    (day-open? calendar day-after)))))))
;
(defn inside-overnight-gap?
  "only true if the day has an open and close part and dt is between"
  [calendar dt first-close close]
  (if (overnight? calendar)
    (let [time (t/time dt)
          day-before (t/<< dt (t/new-duration 1 :days))
          day-after (t/>> dt (t/new-duration 1 :days))]
      (and (t/> time close)
           (day-open? calendar day-before)
           (t/< time first-close)
           (day-open? calendar day-after)))
    false))

(defn overnight-weekend? [calendar dt]
  (let [day-after (t/>> dt (t/new-duration 1 :days))]
    (and (overnight? calendar)
         (day-open? calendar dt)
         (day-closed? calendar day-after))))

(defn overnight-week-start? [calendar dt]
  (let [day-before (t/<< dt (t/new-duration 1 :days))]
    (and (overnight? calendar)
         (day-open? calendar dt)
         (day-closed? calendar day-before))))

(defn overnight-gap-or-weekend? [{:keys [close] :as calendar} dt first-close]
  (if (overnight? calendar)
    (let [time (t/time dt)
          day-before (t/<< dt (t/new-duration 1 :days))
          day-after (t/>> dt (t/new-duration 1 :days))
          after-close? (and (t/> time close)
                            (day-open? calendar day-before))
          inside-gap? (and after-close?
                           (t/< time first-close)
                           (day-open? calendar day-after))
          weekend? (and after-close?
                        (day-closed? calendar day-after))]
      (or inside-gap? weekend?))
    false))

(defn midnight-close? [close]
  (t/= close (t/max-of-type (t/new-time 23 59 59))))

(defn next-day-at-midnight [{:keys [timezone] :as calendar} dt]
  (let [next-day (t/>> dt (t/new-duration 1 :days))]
    (at-time (t/date (t/in next-day timezone))
             (t/new-time 0 0 0) timezone)))

(defn same-day-at-midnight [{:keys [timezone] :as calendar} dt]
  (at-time (t/date (t/in dt timezone))
           (t/new-time 0 0 0) timezone))

(defn trading-open-time [{:keys [open timezone] :as calendar} dt]
  (let [date (if (t/date? dt)
               dt
               (t/date (t/in dt timezone)))]
    (at-time date open timezone)))

(defn trading-close-time [{:keys [close timezone] :as calendar} dt]
  (let [date (if (t/date? dt)
               dt
               (t/date (t/in dt timezone)))]
    (at-time date close timezone)))

(defn last-open-of-the-day [{:keys [close] :as calendar} n unit]
  ; handle approx. day close time
  (if (midnight-close? close)
    (t/<< (t/new-time 0 0 0) (t/new-duration n unit))
    (t/<< close (t/new-duration n unit))))