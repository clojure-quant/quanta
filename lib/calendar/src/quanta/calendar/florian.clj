(ns quanta.calendar.florian
  (:require
   [tick.core :as t]
   [ta.calendar.calendars :as caldb]
   [ta.calendar.helper :as calhelp]))

(-> (caldb/get-calendar :us)
    :timezone)
;; => "America/New_York"

(-> (caldb/get-calendar :crypto)
    :timezone)

(-> (caldb/get-calendar :crypto)
    :timezone)

(defn market-info [market-kw]
  (let [cal (caldb/get-calendar market-kw)
        dt (t/instant)
        dt-cal (-> dt
                   (t/zoned-date-time)
                   (t/in (:timezone cal)))
        open? (calhelp/time-open? cal dt-cal)
        business? (calhelp/day-open? cal dt-cal)]
    {:market market-kw
     :open? open?
     :business business?
     :as-of-dt dt-cal}))

(t/instant)

(market-info :crypto)
  ;; => {:market :crypto, :open? true, :business true, :as-of-dt #time/zoned-date-time "2024-10-11T21:10:02.760020057Z[UTC]"}

(market-info :eu)
  ;; => {:market :eu,
  ;;     :open? false,
  ;;     :business true,
  ;;     :as-of-dt #time/zoned-date-time "2024-10-11T23:09:51.853464621+02:00[Europe/Berlin]"}

(market-info :jp)
 ;; => {:market :jp,
 ;;     :open? false,
 ;;     :business false,
 ;;     :as-of-dt #time/zoned-date-time "2024-10-12T06:09:57.855540184+09:00[Asia/Tokyo]"}

