(ns ta.calendar.day-test
  (:require [clojure.test :refer :all]
            [tick.core :as t]
            [ta.calendar.calendars :as cal]
            [ta.calendar.day :refer [next-open next-close
                                     prior-open prior-close prior-close-dt
                                     current-close current-open]]))

(defn to-est [dt-str]
  (t/in (t/date-time dt-str) "America/New_York"))

(defn same-date? [expected-dt actual-dt]
  (t/= (t/date expected-dt) (t/date actual-dt)))

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
      dt-sunday18 (to-est "2024-02-11T18:00:00")


      ;;

      dt-monday-09-00 (to-est "2024-02-05T09:00:00")
      dt-tuesday-09-00 (to-est "2024-02-06T09:00:00")
      dt-wednesday-17-00 (to-est "2024-02-07T17:00:00")

      dt-thursday-06-00 (to-est "2024-02-08T06:00:00")
      dt-thursday-09-00 (to-est "2024-02-08T09:00:00")
      dt-thursday-16-45 (to-est "2024-02-08T16:45:00")
      dt-thursday-17-00 (to-est "2024-02-08T17:00:00")
      dt-thursday-23-00 (to-est "2024-02-08T23:00:00")

      dt-friday-00-00 (to-est "2024-02-09T00:00:00")
      dt-friday-06-00 (to-est "2024-02-09T06:00:00")
      dt-friday-09-00 (to-est "2024-02-09T09:00:00")
      dt-friday-09-15 (to-est "2024-02-09T09:15:00")
      dt-friday-12-00 (to-est "2024-02-09T12:00:00")
      dt-friday-12-15 (to-est "2024-02-09T12:15:00")
      dt-friday-12-29 (to-est "2024-02-09T12:29:00")
      dt-friday-12-30 (to-est "2024-02-09T12:30:00")
      dt-friday-12-33 (to-est "2024-02-09T12:33:00")
      dt-friday-12-34 (to-est "2024-02-09T12:34:00")
      dt-friday-12-34-56 (to-est "2024-02-09T12:34:56")
      dt-friday-12-45 (to-est "2024-02-09T12:45:00")
      dt-friday-13-00 (to-est "2024-02-09T13:00:00")
      dt-friday-16-15 (to-est "2024-02-09T16:15:00")
      dt-friday-16-30 (to-est "2024-02-09T16:30:00")
      dt-friday-16-45 (to-est "2024-02-09T16:45:00")
      dt-friday-16-55 (to-est "2024-02-09T16:55:00")
      dt-friday-17-00 (to-est "2024-02-09T17:00:00")
      dt-friday-17-01 (to-est "2024-02-09T17:01:00")
      dt-friday-17-15 (to-est "2024-02-09T17:15:00")
      dt-friday-18-00 (to-est "2024-02-09T18:00:00")

      dt-saturday-09-00 (to-est "2024-02-10T09:00:00")
      dt-saturday-12-00 (to-est "2024-02-10T12:00:00")
      dt-saturday-17-00 (to-est "2024-02-10T17:00:00")

      dt-sunday-06-00 (to-est "2024-02-11T06:00:00")
      dt-sunday-17-00 (to-est "2024-02-11T17:00:00")
      dt-sunday-17-15 (to-est "2024-02-11T17:15:00")

      dt-monday-next-06-00 (to-est "2024-02-12T06:00:00")
      dt-monday-next-09-00 (to-est "2024-02-12T09:00:00")
      dt-monday-next-09-15 (to-est "2024-02-12T09:15:00")

      us-cal (:us cal/calendars)
      forex-cal (:forex cal/calendars)]

  ; TODO more tests

  (deftest current-close-intraday
    (testing "dt inside interval"
      (is (t/= dt-thursday-17-00 (current-close us-cal dt-friday-12-34-56)))
      (is (not (t/= dt-friday-17-00 (current-close us-cal dt-friday-12-34-56)))))
    (testing "dt on interval boundary"
      (is (t/= dt-thursday-17-00 (current-close us-cal dt-thursday-17-00)))
      (is (not (t/= dt-friday-17-00 (current-close us-cal dt-thursday-17-00)))))
    (testing "dt before trading hours"
      (is (t/= dt-thursday-17-00 (current-close us-cal dt-friday-06-00)))
      (is (not (t/= dt-friday-17-00 (current-close us-cal dt-friday-06-00)))))
    (testing "dt after trading hours"
      (is (t/= dt-thursday-17-00 (current-close us-cal dt-thursday-23-00)))
      (is (not (t/= dt-friday-17-00 (current-close us-cal dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      (is (t/= dt-friday-17-00 (current-close us-cal dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (current-close us-cal dt-monday-next-06-00)))))
    (testing "dt after trading hours (weekend)"
      (is (t/= dt-friday-17-00 (current-close us-cal dt-friday-18-00)))
      (is (not (t/= dt-saturday-17-00 (current-close us-cal dt-friday-17-15)))))
    (testing "dt on trading week start"
      (is (t/= dt-friday-17-00 (current-close us-cal dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (current-close us-cal dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-friday-17-00 (current-close us-cal dt-friday-17-00)))
      (is (not (t/= dt-thursday-17-00 (current-close us-cal dt-friday-17-00)))))
    (testing "dt not on trading day"
      (is (t/= dt-friday-17-00 (current-close us-cal dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (current-close us-cal dt-saturday-12-00)))))
    )

  (deftest current-open-intraday)

  (deftest prior-open-intraday)

  (deftest prior-close-intraday
    (testing "dt inside interval"
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal dt-friday-12-34-56)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt us-cal dt-friday-12-34-56)))))
    (testing "dt on interval boundary"
      (is (t/= dt-wednesday-17-00 (prior-close-dt us-cal dt-thursday-17-00)))
      (is (not (t/= dt-thursday-17-00 (prior-close-dt us-cal dt-thursday-17-00)))))
    (testing "dt before trading hours"
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal dt-friday-06-00)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt us-cal dt-friday-06-00)))))
    (testing "dt after trading hours"
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal dt-thursday-23-00)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt us-cal dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-close-dt us-cal dt-monday-next-06-00)))))
    (testing "dt after trading hours (weekend)"
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal dt-friday-18-00)))
      (is (not (t/= dt-saturday-17-00 (prior-close-dt us-cal dt-friday-17-15)))))
    (testing "dt on trading week start"
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-close-dt us-cal dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt us-cal dt-friday-17-00)))))
    (testing "dt not on trading day"
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (prior-close-dt us-cal dt-saturday-12-00))))))

  ; TODO: next-open vs next-day-open
  (deftest next-open-day
    (testing "dt inside interval"
      (is (t/= dt-friday-09-00 (next-open us-cal dt-thursday-16-45)))
      (is (not (t/= dt-thursday-09-00 (next-open us-cal dt-thursday-16-45)))))
    (testing "dt on interval boundary"
      (is (t/= dt-friday-09-00 (next-open us-cal dt-thursday-09-00)))
      (is (not (t/= dt-thursday-09-00 (next-open us-cal dt-thursday-09-00)))))
    ;(testing "dt before trading hours"
    ;  (is (t/= dt-thursday-09-00 (next-open us-cal dt-thursday-06-00)))
    ;  (is (not (t/= dt-friday-09-00 (next-open us-cal dt-thursday-06-00)))))
    )

  (deftest overnight
    (testing "dt inside interval"
      (is (t/= (t/date "2024-02-06") (t/date (next-open (:forex cal/calendars) dt-monday6))))
      (is (not (t/= (t/date dt-saturday06) (t/date (next-open (:forex cal/calendars) dt-friday18)))))
      (is (t/= (t/date dt-sunday06) (t/date (next-open (:forex cal/calendars) dt-friday18))))
      (is (t/= (t/date dt-sunday06) (t/date (next-open (:forex cal/calendars) dt-saturday06))))
      (is (t/= (t/date "2024-02-12") (t/date (next-open (:forex cal/calendars) dt-sunday12)))))))