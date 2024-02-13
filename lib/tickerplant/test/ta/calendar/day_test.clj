(ns ta.calendar.day-test
  (:require [clojure.test :refer :all]
            [tick.core :as t]
            [ta.calendar.calendars :as cal]
            [ta.calendar.day :refer [next-open next-close
                                     prior-open prior-close]]))

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
  ;(println (next-open (:us1 cal/calendars) dt-monday6))

  (deftest next-open-day
    (testing "next open day - us"
      (is (t/= (t/date "2024-02-06") (t/date (next-open (:us cal/calendars) dt-monday6))))
      (is (not (t/= (t/date dt-saturday06) (t/date (next-open (:us cal/calendars) dt-friday18)))))
      (is (t/= (t/date "2024-02-12") (t/date (next-open (:us cal/calendars) dt-friday18))))
      (is (t/= (t/date "2024-02-12") (t/date (next-open (:us cal/calendars) dt-saturday06))))
      (is (t/= (t/date "2024-02-12") (t/date (next-open (:us cal/calendars) dt-sunday12)))))
    (testing "next open day - forex"
      (is (t/= (t/date "2024-02-06") (t/date (next-open (:forex cal/calendars) dt-monday6))))
      (is (not (t/= (t/date dt-saturday06) (t/date (next-open (:forex cal/calendars) dt-friday18)))))
      (is (t/= (t/date dt-sunday06) (t/date (next-open (:forex cal/calendars) dt-friday18))))
      (is (t/= (t/date dt-sunday06) (t/date (next-open (:forex cal/calendars) dt-saturday06))))
      (is (t/= (t/date "2024-02-12") (t/date (next-open (:forex cal/calendars) dt-sunday12))))))

  ;(deftest next-close-day
  ;  (testing "next close day - us"
  ;    (is (t/= (t/date "2024-02-06") (t/date (next-close (:us cal/calendars) dt-monday6))))
  ;    (is (not (t/= (t/date dt-saturday06) (t/date (next-close (:us cal/calendars) dt-friday18)))))
  ;    (is (t/= (t/date "2024-02-12") (t/date (next-close (:us cal/calendars) dt-friday18))))
  ;    (is (t/= (t/date "2024-02-12") (t/date (next-close (:us cal/calendars) dt-saturday06))))
  ;    (is (t/= (t/date "2024-02-12") (t/date (next-close (:us cal/calendars) dt-sunday12))))))

  (deftest prior-open-day
    (testing "prior open day - us"))

  (deftest prior-close-day
    (testing "prior close day - us"))
  )