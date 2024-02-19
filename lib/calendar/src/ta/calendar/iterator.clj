(ns ta.calendar.iterator
  (:require [tick.core :as t]
            [ta.helper.date :refer [align-field round-down adjust-field]]
            [ta.calendar.day :refer [day-closed? next-open next-close prior-open prior-close]]
            [ta.calendar.intraday :refer [before-trading-hours? after-trading-hours?
                                          trading-open-time trading-close-time]]
            [ta.calendar.calendars :refer [calendars]]))

(defn- dt-base [calendar n unit dt conf]
  (let [{:keys [on-boundary-fn in-interval-fn]} conf
        zoned (t/in dt (:timezone calendar))
        alined (align-field zoned unit)
        rounded (round-down alined unit n)]
    (if (t/= rounded alined)
      (if on-boundary-fn
        (on-boundary-fn rounded (t/new-duration n unit))
        rounded)
      (if in-interval-fn
        (in-interval-fn rounded (t/new-duration n unit))
        rounded))))

;
; open
;
(defn current-open-dt [calendar n unit dt]
  (dt-base calendar n unit dt {:on-boundary-fn nil :in-interval-fn nil}))

(defn next-open-dt
  ([calendar n unit] (next-open-dt calendar n unit (t/now)))
  ([calendar n unit dt]
   (let [dt-next (dt-base calendar n unit dt {:on-boundary-fn t/>> :in-interval-fn t/>>})
         day-next (t/date dt-next)]
     (if (or (day-closed? calendar day-next)
             (after-trading-hours? calendar dt-next))
       (next-open calendar dt-next)
       (if (before-trading-hours? calendar dt-next)
         (trading-open-time calendar day-next)
         dt-next)))))

(defn prev-open-dt
  ([calendar n unit] (prev-open-dt calendar n unit (t/now)))
  ([calendar n unit dt]
   (let [dt-prev (dt-base calendar n unit dt {:on-boundary-fn t/<< :in-interval-fn nil})
         day-prev (t/date dt-prev)]
     (if (or (day-closed? calendar day-prev)
             (before-trading-hours? calendar dt-prev))
       (->> (prior-close calendar dt-prev) (prev-open-dt calendar n unit))
       (let [close (trading-close-time calendar day-prev)]
         (if (t/>= dt-prev close)
           (prev-open-dt calendar n unit close)
           dt-prev)
         )))))

;
; close
;
(defn current-close-dt [calendar n unit dt]
  (dt-base calendar n unit dt {:on-boundary-fn nil :in-interval-fn t/>>}))

(defn next-close-dt
  ([calendar n unit] (next-close-dt calendar n unit (t/now)))
  ([calendar n unit dt]
   (let [dt-next (dt-base calendar n unit dt {:on-boundary-fn t/>> :in-interval-fn t/>>})
         day-next (t/date dt-next)]
     (if (or (day-closed? calendar day-next)
             (after-trading-hours? calendar dt-next))
       (->> (next-open calendar dt-next) (next-close-dt calendar n unit))
       (let [open (trading-open-time calendar day-next)]
         (if (t/<= dt-next open)
           (next-close-dt calendar n unit open)
           dt-next)
         )))))

(defn prev-close-dt
  ([calendar n unit] (prev-close-dt calendar n unit (t/now)))
  ([calendar n unit dt]
  (let [dt-prev (dt-base calendar n unit dt {:on-boundary-fn t/<< :in-interval-fn nil})
        day-prev (t/date dt-prev)]
    (if (or (day-closed? calendar day-prev)
            (before-trading-hours? calendar dt-prev))
      (prior-close calendar dt-prev)
      (if (after-trading-hours? calendar dt-prev)
        (trading-close-time calendar day-prev)
        dt-prev)))))

(comment
  (t/at (t/on (t/date-time)) (t/new-time 17 0 0))

  ;(round-down (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]") :minutes 15)

  (->> (iterate (partial next-open-dt (:us calendars) 15 :minutes)
           (next-open-dt (:us24 calendars) 15 :minutes
                    ;(t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]")
                    ;(t/zoned-date-time "2024-02-09T12:29:00Z[America/New_York]")
                    (t/zoned-date-time "2024-02-09T12:30:00Z[America/New_York]")
                            ))
      (take 5))

  (->> (iterate (partial prev-open-dt (:us calendars) 15 :minutes)
                (prev-open-dt (:us24 calendars) 15 :minutes
                                 ;(t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]")
                                 (t/zoned-date-time "2024-02-09T12:29:00Z[America/New_York]")
                                 ;(t/zoned-date-time "2024-02-09T12:30:00Z[America/New_York]")
                                 ))
       (take 5))

  ;(prev-close-dt (:us calendars) 15 :minutes (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]"))
  ;(prev-close-dt (:forex calendars) 15 :minutes (t/in (t/date-time "2024-02-08T23:00:00") "America/New_York"))
  (prev-close-dt (:forex calendars) 15 :minutes (t/in (t/date-time "2024-02-08T23:00:00") "America/New_York"))
  (->> (iterate (partial prev-close-dt (:us calendars) 15 :minutes)
                (prev-close-dt (:us calendars) 15 :minutes
                            (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]")
                            ;(t/zoned-date-time "2024-02-09T12:29:00Z[America/New_York]")
                            ;(t/zoned-date-time "2024-02-09T12:30:00Z[America/New_York]")
                            ))
       (take 5))

  (next-close-dt (:us calendars) 15 :minutes (t/in (t/date-time "2024-02-09T06:00:00") "America/New_York"))
  (->> (iterate (partial next-close-dt (:us calendars) 15 :minutes)
                (next-close-dt (:us calendars) 15 :minutes
                                  ;(t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]")
                                  ;(t/zoned-date-time "2024-02-09T12:29:00Z[America/New_York]")
                                  (t/zoned-date-time "2024-02-09T12:30:00Z[America/New_York]")
                                  ;(t/zoned-date-time "2024-02-09T09:00:00Z[America/New_York]")
                                  ))
       (take 5))
  )
