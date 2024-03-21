(ns ta.calendar.intraday
  (:require
   [tick.core :as t]
   [ta.helper.date :refer [align-field round-down adjust-field extract-field date-unit? at-time]]
   [ta.calendar.calendars :refer [calendars]]
   [ta.calendar.day :as day]
   [ta.calendar.helper :refer [before-trading-hours? after-trading-hours?
                               trading-open-time trading-close-time
                               time-open? time-closed?
                               day-open? day-closed?
                               intraday? overnight?
                               day-has-prior-close?
                               day-has-next-close?
                               inside-overnight-gap?
                               overnight-weekend? overnight-week-start?
                               overnight-gap-or-weekend?]]))

;
; base
;

(defn- dt-base [calendar n unit dt conf]
  (let [{:keys [open close]} calendar
        {:keys [on-boundary-fn in-interval-fn]} conf
        zoned (t/in dt (:timezone calendar))
        alined (align-field zoned unit)
        ; round down until 1 hour will stay always in the same day.
        ; after this, shifting must be done before rounding because rounding can flip days
        rounded (if (and (= unit :hours) (> n 1))
                  (round-down alined unit n (extract-field open unit))
                  (round-down alined unit n))]
    (if (t/= rounded dt)
      (if on-boundary-fn
        (on-boundary-fn rounded (t/new-duration n unit))
        rounded)
      (if in-interval-fn
        (in-interval-fn rounded (t/new-duration n unit))
        rounded))))

;
; close
;

(defn next-close-dt
  "next close dt (exclusive current boundary)"
  ([calendar n unit] (next-close-dt calendar n unit (t/now)))
  ([calendar n unit dt] (next-close-dt calendar n unit dt {:on-boundary-fn t/>> :in-interval-fn t/>>}))
  ([calendar n unit dt conf]
   (let [{:keys [open close]} calendar
         dt-next (dt-base calendar n unit dt conf)
         day-next (t/date dt-next)
         first-close (t/>> open (t/new-duration n unit))]
     (if (not (day-has-next-close? calendar dt-next first-close close))
       (->> (day/next-open calendar dt-next) (next-close-dt calendar n unit))
       (let [open (trading-open-time calendar day-next)]
         (if (and (before-trading-hours? calendar dt-next first-close close)
                  (or (intraday? calendar)
                      (inside-overnight-gap? calendar dt-next first-close close)
                      (overnight-week-start? calendar dt-next)))
           (next-close-dt calendar n unit open)
           dt-next))))))

(defn prior-close-dt
  "prior close dt (exclusive current boundary)"
  ([calendar n unit] (prior-close-dt calendar n unit (t/now)))
  ([calendar n unit dt] (prior-close-dt calendar n unit dt {:on-boundary-fn t/<< :in-interval-fn nil}))
  ([calendar n unit dt conf]
   (let [{:keys [open close]} calendar
         dt-prev (dt-base calendar n unit dt conf)
         day-prev (t/date dt-prev)
         first-close (t/>> open (t/new-duration n unit))]
     (if (not (day-has-prior-close? calendar dt-prev first-close))
       (day/prior-close calendar dt-prev)
       (if (and (after-trading-hours? calendar dt-prev)
                (or (intraday? calendar)
                    (inside-overnight-gap? calendar dt-prev first-close close)
                    (overnight-weekend? calendar dt-prev)))
         (trading-close-time calendar day-prev)             ; TODO: bug 1h, currently: 17:00h => 16:30h  => should: 16:00   => possible solution: rounddown close time.... or last-close
         dt-prev)))))

(defn current-close-dt
  "recent close dt (inclusive current boundary).
   same as prior-close-dt but with other boundary handling"
  ([calendar n unit] (current-close-dt calendar n unit (t/now)))
  ([calendar n unit dt]
   (prior-close-dt calendar n unit dt {:on-boundary-fn nil :in-interval-fn nil})))

;
; open
;

(defn next-open-dt
  ([calendar n unit] (next-open-dt calendar n unit (t/now)))
  ([calendar n unit dt] (next-open-dt calendar n unit dt {:on-boundary-fn t/>> :in-interval-fn t/>>}))
  ([calendar n unit dt conf]
   (let [{:keys [open close]} calendar
         dt-next (dt-base calendar n unit dt conf)
         date-next (t/date dt-next)
         last-open (t/<< close (t/new-duration n unit))]    ; take the last possible open of the calendar day interval
     (if (or (day-closed? calendar date-next)
             (after-trading-hours? calendar dt-next open last-open))
       (day/next-open calendar dt-next)
       (if (before-trading-hours? calendar dt-next open last-open)
         (trading-open-time calendar date-next)
         dt-next)))))

(defn prior-open-dt
  ([calendar n unit] (prior-open-dt calendar n unit (t/now)))
  ([calendar n unit dt] (prior-open-dt calendar n unit dt {:on-boundary-fn t/<< :in-interval-fn nil}))
  ([calendar n unit dt conf]
   (let [{:keys [open close timezone]} calendar
         dt-prev (dt-base calendar n unit dt conf)
         date-prev (t/date dt-prev)
         last-open (t/<< close (t/new-duration n unit))]
     (if (or (day-closed? calendar date-prev)
             (before-trading-hours? calendar dt-prev open last-open))
       (->> (day/prior-close calendar dt-prev) (prior-open-dt calendar n unit))
       (if (after-trading-hours? calendar dt-prev open last-open)
         (at-time date-prev last-open timezone)
         dt-prev)))))

(defn current-open-dt
  "recent open dt (inclusive current boundary).
   same as prior-open-dt but with other boundary handling"
  ([calendar n unit] (current-open-dt calendar n unit (t/now)))
  ([calendar n unit dt] (prior-open-dt calendar n unit dt {:on-boundary-fn nil :in-interval-fn nil})))

(comment
  (require '[ta.calendar.calendars :refer [calendars]])
  (def dt (t/at (t/new-date 2023 1 5) (t/new-time 18 30 1)))
  dt
  (t/time dt)
  (time-closed? (:us calendars) dt)
  (day-closed? (:us calendars) dt)
  (next-open (:us calendars) dt)
  (next-intraday (t/new-duration 1 :hours) (:us calendars) dt)

  (def dt2 (t/at (t/new-date 2023 1 5) (t/new-time 11 0 0)))
  (t/day-of-week dt2)
  (time-closed? (:us calendars) dt2)
  (day-closed? (:us calendars) dt2)
  (next-open (:us calendars) dt2)
  (next-close-dt (:us calendars) 1 :hours dt2)
  (next-close-dt (:us calendars) 1 :minutes dt2)

  (prior-close-dt (:us calendars) 1 :hours dt2)
  (prior-close-dt (:us calendars) 1 :minutes dt2)

 ; 
  )

(comment
  (round-down (t/in (t/date-time "2024-02-10T12:00:00") "America/New_York") :hours 4 9)

  (current-close-dt (:us calendars) 15 :minutes
                    ;(t/zoned-date-time "2024-02-20T12:29:00Z[America/New_York]")
                    (t/zoned-date-time "2024-02-20T12:30:00Z[America/New_York]"))

  (t/at (t/on (t/date-time)) (t/new-time 17 0 0))

  ;(round-down (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]") :minutes 15)

  (next-open-dt (:forex calendars) 15 :minutes (t/in (t/date-time "2024-02-10T12:00:00") "America/New_York"))
  (prior-open-dt (:forex calendars) 15 :minutes (t/in (t/date-time "2024-02-11T06:00:00") "America/New_York"))

  (->> (iterate (partial next-open-dt (:us calendars) 15 :minutes)
                (current-open-dt (:us24 calendars) 15 :minutes
                                 (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]")
                                ;(t/zoned-date-time "2024-02-09T12:29:00Z[America/New_York]")
                                ;(t/zoned-date-time "2024-02-09T12:30:00Z[America/New_York]")
                                 ))
       (take 5))

  (->> (iterate (partial prior-open-dt (:us calendars) 15 :minutes)
                (current-open-dt (:us calendars) 15 :minutes
                                 (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]")
                              ;(t/zoned-date-time "2024-02-09T12:29:00Z[America/New_York]")
                              ;(t/zoned-date-time "2024-02-09T12:30:00Z[America/New_York]")
                                 ))
       (take 5))

  ;(prev-close-dt (:us calendars) 15 :minutes (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]"))
  ;(prev-close-dt (:forex calendars) 15 :minutes (t/in (t/date-time "2024-02-08T23:00:00") "America/New_York"))
  (prior-close-dt (:forex calendars) 15 :minutes (t/in (t/date-time "2024-02-08T23:00:00") "America/New_York"))
  (prior-close-dt (:forex calendars) 1 :minutes (t/in (t/date-time "2024-02-08T12:59:30") "America/New_York"))
  (prior-close-dt (:forex calendars) (t/in (t/date-time "2024-02-08T23:00:00") "America/New_York"))
  (->> (iterate (partial prior-close-dt (:us calendars) 15 :minutes)
                (current-close-dt (:us calendars) 15 :minutes
                               ;(t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]")
                               ;(t/zoned-date-time "2024-02-09T12:29:00Z[America/New_York]")
                                  (t/zoned-date-time "2024-02-09T12:30:00Z[America/New_York]")))
       (take 5))
  (->> (iterate (partial prior-close-dt (:us calendars) 1 :days)
                (current-close-dt (:us calendars) 1 :days
                               ;(t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]")
                               ;(t/zoned-date-time "2024-02-09T12:29:00Z[America/New_York]")
                                  (t/zoned-date-time "2024-02-09T12:30:00Z[America/New_York]")))
       (take 5))

  (next-close-dt (:forex calendars) 1 :minutes (t/in (t/date-time "2024-02-08T16:27:00") "America/New_York"))
  (next-close-dt (:us calendars) 15 :minutes (t/in (t/date-time "2024-02-09T06:00:00") "America/New_York"))
  (->> (iterate (partial next-close-dt (:us calendars) 15 :minutes)
                (current-close-dt (:us calendars) 15 :minutes
                                  (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]")
                               ;(t/zoned-date-time "2024-02-09T12:29:00Z[America/New_York]")
                               ;(t/zoned-date-time "2024-02-09T12:30:00Z[America/New_York]")
                               ;(t/zoned-date-time "2024-02-09T09:00:00Z[America/New_York]")
                                  ))
       (take 5))

  (dt-base (:us calendars) 1 :days
           (t/in (t/date-time "2024-02-09T06:00:00") "America/New_York")
           {:on-boundary-fn t/>> :in-interval-fn t/>>}))