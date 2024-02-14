(ns ta.calendar.iterator
  (:require [tick.core :as t]
            [ta.helper.date :refer [align-field round-down adjust-field]]
            [ta.calendar.day :refer [day-closed? next-open next-close prior-open prior-close]]
            [ta.calendar.intraday :refer [before-trading-hours? after-trading-hours?
                                          trading-open-time trading-close-time]]
            [ta.calendar.calendars :refer [calendars]]))

;
; open
;
(defn current-open-dt [calendar n unit dt]
  (-> (t/in dt (:timezone calendar))
      (align-field unit)
      (round-down unit n)))

(defn next-open-dt
  ([calendar n unit] (next-open-dt calendar n unit (t/now)))
  ([calendar n unit dt]
   (let [dt-next (-> (current-open-dt calendar n unit dt)
                     (t/>> (t/new-duration n unit)))
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
   (let [dt-prev (-> (current-open-dt calendar n unit dt)
                     (t/<< (t/new-duration n unit)))
         day-prev (t/date dt-prev)]
     (if (or (day-closed? calendar day-prev)
             (before-trading-hours? calendar dt-prev))
       (->> (prior-open calendar dt-prev) (prev-open-dt calendars n unit))
       (let [close (trading-close-time calendar day-prev)]
         (if (t/>= dt-prev close)
           (prev-open-dt calendars n unit close)
           dt-prev)
         )))))

;
; close
;
(defn current-close-dt [calendar n unit dt]
  (let [zoned (t/in dt (:timezone calendar))
        alined (align-field zoned unit)
        rounded (round-down alined unit n)]
    (if (t/= rounded alined)
      rounded
      (t/>> rounded (t/new-duration n unit)))))

(defn next-close-dt
  ([calendar n unit] (next-close-dt calendar n unit (t/now)))
  ([calendar n unit dt]
   (let [dt-next (-> (current-close-dt calendar n unit dt)
                     (t/>> (t/new-duration n unit)))
         day-next (t/date dt-next)]
     (if (or (day-closed? calendar day-next)
             (after-trading-hours? calendar dt-next))
       (->> (next-open calendar dt-next) (next-close-dt calendars n unit))
       (let [open (trading-open-time calendar day-next)]
         (if (t/<= dt-next open)
           (next-close-dt calendars n unit open)
           dt-next)
         )))))

(defn prev-close-dt
  ([calendar n unit] (prev-close-dt calendar n unit (t/now)))
  ([calendar n unit dt]
  (let [dt-prev (-> (current-close-dt calendar n unit dt)
                    (t/<< (t/new-duration n unit)))
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

  (prev-close-dt (:us calendars) 15 :minutes (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]"))
  (->> (iterate (partial prev-close-dt (:us calendars) 15 :minutes)
                (prev-close-dt (:us calendars) 15 :minutes
                            (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]")
                            ;(t/zoned-date-time "2024-02-09T12:29:00Z[America/New_York]")
                            ;(t/zoned-date-time "2024-02-09T12:30:00Z[America/New_York]")
                            ))
       (take 5))

  (->> (iterate (partial next-close-dt (:us calendars) 15 :minutes)
                (next-close-dt (:us calendars) 15 :minutes
                                  ;(t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]")
                                  ;(t/zoned-date-time "2024-02-09T12:29:00Z[America/New_York]")
                                  (t/zoned-date-time "2024-02-09T12:30:00Z[America/New_York]")
                                  ;(t/zoned-date-time "2024-02-09T09:00:00Z[America/New_York]")
                                  ))
       (take 5))
  )
