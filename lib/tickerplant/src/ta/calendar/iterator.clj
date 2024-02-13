(ns ta.calendar.iterator
  (:require [tick.core :as t]
            [ta.helper.date :refer [align-field round-down adjust-field]]
            [ta.calendar.day :refer [day-closed? next-open next-close prior-close]]
            [ta.calendar.intraday :refer [before-trading-hours? after-trading-hours?
                                          trading-open-time trading-close-time]]
            [ta.calendar.calendars :refer [calendars]]))



(defn current-close-dt [calendar n unit dt]
  (-> (t/in dt (:timezone calendar))
      (align-field unit)
      ; TODO: round up correctly. this only works if dt != close dt
      (round-down unit n)
      (t/>> (t/new-duration n unit))
      ))

(defn current-open-dt [calendar n unit dt]
  (-> (t/in dt (:timezone calendar))
      (align-field unit)
      (round-down unit n)))

(defn next-dt [calendar n unit dt]
  (-> (t/in dt (:timezone calendar))
      (t/>> (t/new-duration n unit))
      (align-field unit)
      (round-down unit n)))

(defn prev-dt [calendar n unit dt]
  (-> (t/in dt (:timezone calendar))
      (t/<< (t/new-duration n unit))
      (align-field unit)
      (round-down unit n)
      ))

;
; open
;
(defn next-open-dt
  ([calendar n unit] (next-open-dt calendar n unit (t/now)))
  ([calendar n unit dt]
   (let [dt-next (next-dt calendar n unit dt)
         day-next (t/date dt-next)]
     (if (or (day-closed? calendar day-next)
             (after-trading-hours? calendar dt-next))
       (next-open calendar dt)
       (if (before-trading-hours? calendar dt-next)
         (trading-open-time calendar dt)
         dt-next)))))

;
; close
;
;(defn next-close-dt
;  ([calendar n unit] (next-close-dt calendar n unit (t/now)))
;  ([calendar n unit dt]
;   (let [dt-next (next-dt calendar n unit dt)
;         day-next (t/date dt-next)]
;     (if (or (day-closed? calendar day-next)
;             (after-trading-hours? calendar dt-next))
;       (->> (next-open calendar dt) (next-close-dt calendars n unit))
;       (if (before-trading-hours? calendar dt-next)
;         (trading-open-time calendar dt)
;         dt-next)))))

(defn prev-close-dt
  ([calendar n unit] (prev-close-dt calendar n unit (t/now)))
  ([calendar n unit dt]
  (let [dt-prev (prev-dt calendar n unit dt)
        day-prev (t/date dt-prev)]
    (if (or (day-closed? calendar day-prev)
            (before-trading-hours? calendar dt-prev))
      (prior-close calendar dt)
      (if (after-trading-hours? calendar dt-prev)
        (trading-close-time calendar dt)
        dt-prev)))))

(comment
  (t/at (t/on (t/date-time)) (t/new-time 17 0 0))
  (next-close-dt (:us1 calendars) 15 :minutes (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]"))
  (round-down (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]") :minutes 15)

  (->> (iterate (partial next-close-dt (:us1 calendars) 15 :minutes)
           (current-open-dt (:us calendars) 15 :minutes
                    (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]")))
      (take 5))

  (prev-close-dt (:us1 calendars) 15 :minutes (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]"))
  (->> (iterate (partial prev-close-dt (:us1 calendars) 15 :minutes)
                (current-close-dt (:us1 calendars) 15 :minutes
                            (t/zoned-date-time "2024-02-09T12:34:56Z[America/New_York]")
                            ;(t/zoned-date-time "2024-02-09T12:29:00Z[America/New_York]")
                            ))
       (take 5))
  )
