(ns ta.calendar.interval-test
  (:require [clojure.test :refer :all]
            [ta.calendar.interval :refer [get-calendar-day-duration]]))


(deftest duration-in-sec
  (testing "us - 9h-17h"
    (is (= 28800 (get-calendar-day-duration :us)))
    (is (not (= 1440 (get-calendar-day-duration :us)))))
  (testing "us24 - 00:00:00h - 23:59:59h"
    (is (= 86399 (get-calendar-day-duration :us24)))
    (is (not (= 86400 (get-calendar-day-duration :us24)))))
  (testing "forex - 17:00h - 16:30h"
    (is (= 84600 (get-calendar-day-duration :forex)))
    (is (not (= 86400 (get-calendar-day-duration :forex)))))
  (testing "equal - 0ß:00h - 00:00h"
    (is (= 86400 (get-calendar-day-duration :test-equal)))
    (is (not (= 86399 (get-calendar-day-duration :test-equal)))))
  )