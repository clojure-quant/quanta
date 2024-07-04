(ns ta.calendar.calendars-test
  (:require
   [clojure.test :refer :all]
   [tick.core :as t]
   [ta.calendar.helper :refer [day-open? day-closed?
                               time-closed? time-open?
                               before-trading-hours? after-trading-hours?]]
   [ta.calendar.interval.intraday :refer []]
   [ta.calendar.calendars :as cal]))

(deftest trading-days
  (testing "five-day week"
    (is (contains? cal/week-5 t/MONDAY))
    (is (not (contains? cal/week-5 t/SATURDAY)))
    (is (not (contains? cal/week-5 t/SUNDAY))))
  (testing "six-day week"
    (is (contains? cal/week-6 t/SATURDAY))
    (is (not (contains? cal/week-6 t/SUNDAY))))
  (testing "six-day week (incl. sunday)"
    (is (contains? cal/week-6-sunday t/SUNDAY))
    (is (not (contains? cal/week-6-sunday t/SATURDAY))))
  (testing "seven-day week"
    (is (contains? cal/week-7 t/SUNDAY))))

(defn to-est [dt-str]
  (t/in (t/date-time dt-str) "America/New_York"))

(let [dt-monday6 (to-est "2024-02-05T06:00:00")
      dt-monday12 (to-est "2024-02-05T12:00:00")
      dt-monday18 (to-est "2024-02-05T18:00:00")
      dt-thursday06 (to-est "2024-02-08T06:00:00")
      dt-thursday12 (to-est "2024-02-08T12:00:00")
      dt-thursday18 (to-est "2024-02-08T18:00:00")
      dt-friday06 (to-est "2024-02-09T06:00:00")
      dt-friday12 (to-est "2024-02-09T12:00:00")
      dt-friday18 (to-est "2024-02-09T18:00:00")
      dt-saturday06 (to-est "2024-02-10T06:00:00")
      dt-saturday12 (to-est "2024-02-10T12:00:00")
      dt-saturday18 (to-est "2024-02-10T18:00:00")
      dt-sunday06 (to-est "2024-02-11T06:00:00")
      dt-sunday12 (to-est "2024-02-11T12:00:00")
      dt-sunday18 (to-est "2024-02-11T18:00:00")]

  (deftest us-calendar
    (testing "trading days"
      ; monday
      (is (day-open? (:us24 cal/calendars) dt-monday6))
      (is (day-open? (:us24 cal/calendars) dt-monday12))
      (is (day-open? (:us24 cal/calendars) dt-monday18))
      ; thursday
      (is (day-open? (:us24 cal/calendars) dt-thursday06))
      (is (day-open? (:us24 cal/calendars) dt-thursday12))
      (is (day-open? (:us24 cal/calendars) dt-thursday18))
      ; friday
      (is (day-open? (:us24 cal/calendars) dt-friday06))
      (is (day-open? (:us24 cal/calendars) dt-friday12))
      (is (day-open? (:us24 cal/calendars) dt-friday18))
      ; saturday
      (is (day-closed? (:us24 cal/calendars) dt-saturday06))
      (is (day-closed? (:us24 cal/calendars) dt-saturday12))
      (is (day-closed? (:us24 cal/calendars) dt-saturday18))
      ; sunday
      (is (day-closed? (:us24 cal/calendars) dt-sunday06))
      (is (day-closed? (:us24 cal/calendars) dt-sunday12))
      (is (day-closed? (:us24 cal/calendars) dt-sunday18)))
    (testing "trading hours"
      (is (time-open? (:us24 cal/calendars) dt-friday06))
      (is (not (time-closed? (:us24 cal/calendars) dt-friday06)))
      (is (not (time-closed? (:us24 cal/calendars) dt-friday12)))
      (is (not (time-closed? (:us24 cal/calendars) dt-friday18)))))

  (deftest us1-calendar
    (testing "trading days"
      ; monday
      (is (day-open? (:us cal/calendars) dt-monday6))
      (is (day-open? (:us cal/calendars) dt-monday12))
      (is (day-open? (:us cal/calendars) dt-monday18))
      ; thursday
      (is (day-open? (:us cal/calendars) dt-thursday06))
      (is (day-open? (:us cal/calendars) dt-thursday12))
      (is (day-open? (:us cal/calendars) dt-thursday18))
      ; friday
      (is (day-open? (:us cal/calendars) dt-friday06))
      (is (day-open? (:us cal/calendars) dt-friday12))
      (is (day-open? (:us cal/calendars) dt-friday18))
      ; saturday
      (is (day-closed? (:us cal/calendars) dt-saturday06))
      (is (day-closed? (:us cal/calendars) dt-saturday12))
      (is (day-closed? (:us cal/calendars) dt-saturday18))
      ; sunday
      (is (day-closed? (:us cal/calendars) dt-sunday06))
      (is (day-closed? (:us cal/calendars) dt-sunday12))
      (is (day-closed? (:us cal/calendars) dt-sunday18)))
    (testing "trading hours"
      (is (time-closed? (:us cal/calendars) dt-friday06))
      (is (not (time-closed? (:us cal/calendars) dt-friday12)))
      (is (time-closed? (:us cal/calendars) dt-friday18))
      (is (before-trading-hours? (:us cal/calendars) dt-friday06))
      (is (after-trading-hours? (:us cal/calendars) dt-friday18))))

  (deftest forex-calendar
    (testing "trading days"
      ; monday
      (is (day-open? (:forex cal/calendars) dt-monday6))
      (is (day-open? (:forex cal/calendars) dt-monday12))
      (is (day-open? (:forex cal/calendars) dt-monday18))
      ; thursday
      (is (day-open? (:forex cal/calendars) dt-thursday06))
      (is (day-open? (:forex cal/calendars) dt-thursday12))
      (is (day-open? (:forex cal/calendars) dt-thursday18))
      ; friday
      (is (day-open? (:forex cal/calendars) dt-friday06))
      (is (day-open? (:forex cal/calendars) dt-friday12))
      (is (day-open? (:forex cal/calendars) dt-friday18))
      ; saturday
      (is (day-closed? (:forex cal/calendars) dt-saturday06))
      (is (day-closed? (:forex cal/calendars) dt-saturday12))
      (is (day-closed? (:forex cal/calendars) dt-saturday18))
      ; sunday
      (is (day-open? (:forex cal/calendars) dt-sunday06))
      (is (day-open? (:forex cal/calendars) dt-sunday12))
      (is (day-open? (:forex cal/calendars) dt-sunday18)))
    (testing "trading hours"
      (is (time-open? (:forex cal/calendars) dt-friday06))
      (is (not (time-closed? (:forex cal/calendars) dt-friday12)))
      (is (time-closed? (:forex cal/calendars) dt-friday18))
      (is (before-trading-hours? (:forex cal/calendars) dt-sunday12))
      (is (not (before-trading-hours? (:forex cal/calendars) dt-sunday18)))
      (is (after-trading-hours? (:forex cal/calendars) dt-friday18))
      (is (not (after-trading-hours? (:forex cal/calendars) dt-friday12)))))

  ; TODO: time zones
  ; TODO: DST
  ; TODO: leap seconds
  ; 31 December 2005, 23:59:60 UTC was 31 December 2005, 18:59:60 (6:59:60 p.m.) in U.S. Eastern Standard Time
  ; and 1 January 2006, 08:59:60 (a.m.) in Japan Standard Time.

  ;(t/- (t/date-time "2005-12-31T23:59:59") (t/date-time "2006-01-01T00:00:00"))

  ;(deftest leap-second
  ;  (testing "2005 => 2006"
  ;    (is ())))

  ;(let [aest-winter (t/zoned-date-time "2024-01-01T07:00:00Z[Australia/Sydney]")
  ;      aest-summer (t/zoned-date-time "2024-07-01T07:00:00Z[Australia/Sydney]")
  ;      jst-winter (t/zoned-date-time "2024-01-01T09:00:00Z[Asia/Tokyo]")
  ;      jst-summer (t/zoned-date-time "2024-07-01T09:00:00Z[Asia/Tokyo]")
  ;      est-winter (t/zoned-date-time "2024-01-01T17:00:00Z[America/New_York]")
  ;      est-summer (t/zoned-date-time "2024-07-01T17:00:00Z[America/New_York]")]
  ;  (println "before DST opening: " (str (t/zoned-date-time "2024-04-05T07:00:00Z[Australia/Sydney]")))
  ;  (println "after DST opening: " (str (t/zoned-date-time "2024-04-08T07:00:00Z[Australia/Sydney]")))
  ;
  ;  (println "winter opening: " (str (t/in aest-winter "Europe/Athens")))
  ;  (println "summer opening: " (str (t/in aest-summer "Europe/Athens")))
  ;  (println "winter closing: " (str (t/in est-winter "Europe/Athens")))
  ;  (println "summer closing: " (str (t/in est-summer "Europe/Athens"))))

  ;; DST 2024
  ; Australia - 2024-04-07 (end)    2024-10-06 (start)
  ; US        - 2024-03-10 (start)  2024-11-03 (end)

  (deftest daylight-saving-time
    (let [dt-winter (t/date-time "2024-01-01T00:00:00")
          dt-summer (t/date-time "2024-07-01T00:00:00")]
      (testing "EST - JST"))))

