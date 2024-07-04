(ns ta.calendar.intraday-us-test
  (:require
   [clojure.test :refer :all]
   [tick.core :as t]
   [ta.calendar.calendars :as cal]
   [ta.calendar.interval.intraday :refer [prior-close-dt next-close-dt
                                          prior-open-dt next-open-dt
                                          current-close-dt current-open-dt]]))

(defn to-est [dt-str]
  (t/in (t/date-time dt-str) "America/New_York"))

(defn to-est-at [date time-str]
  (-> date
      (t/at time-str)
      (t/in "America/New_York")))

(let [dt-thursday-09-00 (to-est "2024-02-08T09:00:00")
      dt-thursday-13-00 (to-est "2024-02-08T13:00:00")
      dt-thursday-16-00 (to-est "2024-02-08T16:00:00")
      dt-thursday-16-45 (to-est "2024-02-08T16:45:00")
      dt-thursday-16-59 (to-est "2024-02-08T16:59:00")
      dt-thursday-17-00 (to-est "2024-02-08T17:00:00")
      dt-thursday-23-00 (to-est "2024-02-08T23:00:00")

      dt-friday-06-00 (to-est "2024-02-09T06:00:00")
      dt-friday-09-00 (to-est "2024-02-09T09:00:00")
      dt-friday-09-01 (to-est "2024-02-09T09:01:00")
      dt-friday-09-00-30 (to-est "2024-02-09T09:00:30")
      dt-friday-09-10 (to-est "2024-02-09T09:10:00")
      dt-friday-09-15 (to-est "2024-02-09T09:15:00")
      dt-friday-10-00 (to-est "2024-02-09T10:00:00")
      dt-friday-12-00 (to-est "2024-02-09T12:00:00")
      dt-friday-12-15 (to-est "2024-02-09T12:15:00")
      dt-friday-12-29 (to-est "2024-02-09T12:29:00")
      dt-friday-12-30 (to-est "2024-02-09T12:30:00")
      dt-friday-12-31 (to-est "2024-02-09T12:31:00")
      dt-friday-12-33 (to-est "2024-02-09T12:33:00")
      dt-friday-12-34 (to-est "2024-02-09T12:34:00")
      dt-friday-12-34-56 (to-est "2024-02-09T12:34:56")
      dt-friday-12-35 (to-est "2024-02-09T12:35:00")
      dt-friday-12-45 (to-est "2024-02-09T12:45:00")
      dt-friday-13-00 (to-est "2024-02-09T13:00:00")
      dt-friday-14-00 (to-est "2024-02-09T14:00:00")
      dt-friday-16-00 (to-est "2024-02-09T16:00:00")
      dt-friday-16-15 (to-est "2024-02-09T16:15:00")
      dt-friday-16-30 (to-est "2024-02-09T16:30:00")
      dt-friday-16-45 (to-est "2024-02-09T16:45:00")
      dt-friday-16-55 (to-est "2024-02-09T16:55:00")
      dt-friday-16-59 (to-est "2024-02-09T16:59:00")
      dt-friday-16-59-30 (to-est "2024-02-09T16:59:30")
      dt-friday-17-00 (to-est "2024-02-09T17:00:00")
      dt-friday-17-01 (to-est "2024-02-09T17:01:00")
      dt-friday-17-15 (to-est "2024-02-09T17:15:00")
      dt-friday-18-00 (to-est "2024-02-09T18:00:00")

      dt-saturday-12-00 (to-est "2024-02-10T12:00:00")
      dt-saturday-17-00 (to-est "2024-02-10T17:00:00")

      dt-monday-next-06-00 (to-est "2024-02-12T06:00:00")
      dt-monday-next-09-00 (to-est "2024-02-12T09:00:00")
      dt-monday-next-09-01 (to-est "2024-02-12T09:01:00")
      dt-monday-next-09-15 (to-est "2024-02-12T09:15:00")
      dt-monday-next-10-00 (to-est "2024-02-12T10:00:00")
      dt-monday-next-13-00 (to-est "2024-02-12T13:00:00")

      us-cal (:us cal/calendars)]

  ;
  ; CURRENT CLOSE
  ;

  (deftest current-close-intraday
    (testing "dt inside interval"
      ; 1min
      (is (t/= dt-friday-12-34 (current-close-dt us-cal 1 :minutes dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-33 (current-close-dt us-cal 1 :minutes dt-friday-12-34-56))))
      ; 15 min
      (is (t/= dt-friday-12-30 (current-close-dt us-cal 15 :minutes dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-45 (current-close-dt us-cal 15 :minutes dt-friday-12-34-56))))
      ; 1 hour
      (is (t/= dt-friday-12-00 (current-close-dt us-cal 1 :hours dt-friday-12-34-56)))
      (is (not (t/= dt-friday-13-00 (current-close-dt us-cal 1 :hours dt-friday-12-34-56))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (current-close-dt us-cal 4 :hours dt-friday-16-15)))
      (is (not (t/= dt-friday-16-00 (current-close-dt us-cal 4 :hours dt-friday-16-15)))))
    (testing "dt on interval boundary"
      ; 1 min
      (is (t/= dt-friday-12-30 (current-close-dt us-cal 1 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-29 (current-close-dt us-cal 1 :minutes dt-friday-12-30))))
      ; 15 min
      (is (t/= dt-friday-12-30 (current-close-dt us-cal 15 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-45 (current-close-dt us-cal 15 :minutes dt-friday-12-30))))
      ; 1 hour
      (is (t/= dt-friday-13-00 (current-close-dt us-cal 1 :hours dt-friday-13-00)))
      (is (not (t/= dt-friday-12-00 (current-close-dt us-cal 1 :hours dt-friday-13-00))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (current-close-dt us-cal 4 :hours dt-friday-13-00)))
      (is (not (t/= dt-friday-17-00 (current-close-dt us-cal 4 :hours dt-friday-13-00)))))
    (testing "dt before trading hours"
      ; 1 min
      (is (t/= dt-thursday-17-00 (current-close-dt us-cal 1 :minutes dt-friday-06-00)))
      (is (not (t/= dt-friday-17-00 (current-close-dt us-cal 1 :minutes dt-friday-06-00))))
      ; 15 min
      (is (t/= dt-thursday-17-00 (current-close-dt us-cal 15 :minutes dt-friday-06-00)))
      (is (not (t/= dt-friday-17-00 (current-close-dt us-cal 15 :minutes dt-friday-06-00))))
      ; 1 hour
      (is (t/= dt-thursday-17-00 (current-close-dt us-cal 1 :hours dt-friday-06-00)))
      (is (not (t/= dt-friday-17-00 (current-close-dt us-cal 1 :hours dt-friday-06-00))))
      ; 4 hour
      (is (t/= dt-thursday-17-00 (current-close-dt us-cal 4 :hours dt-friday-06-00)))
      (is (not (t/= dt-friday-17-00 (current-close-dt us-cal 4 :hours dt-friday-06-00)))))
    (testing "dt after trading hours"
      ; 1 min
      (is (t/= dt-thursday-17-00 (current-close-dt us-cal 1 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-friday-09-00 (current-close-dt us-cal 1 :minutes dt-thursday-23-00))))
      ; 15 min
      (is (t/= dt-thursday-17-00 (current-close-dt us-cal 15 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-friday-09-00 (current-close-dt us-cal 15 :minutes dt-thursday-23-00))))
      ; 1 hour
      (is (t/= dt-thursday-17-00 (current-close-dt us-cal 1 :hours dt-thursday-23-00)))
      (is (not (t/= dt-friday-09-00 (current-close-dt us-cal 1 :hours dt-thursday-23-00))))
      ; 4 hour
      (is (t/= dt-thursday-17-00 (current-close-dt us-cal 4 :hours dt-thursday-23-00)))
      (is (not (t/= dt-friday-09-00 (current-close-dt us-cal 4 :hours dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      ; 1 min
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 1 :minutes dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (current-close-dt us-cal 1 :minutes dt-monday-next-06-00))))
      ; 15 min
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 15 :minutes dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (current-close-dt us-cal 15 :minutes dt-monday-next-06-00))))
      ; 1 hour
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 1 :hours dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (current-close-dt us-cal 1 :hours dt-monday-next-06-00))))
      ; 4 hour
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 4 :hours dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (current-close-dt us-cal 4 :hours dt-monday-next-06-00)))))
    (testing "dt after trading hours (weekend)"
      ; 1 min
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 1 :minutes dt-friday-18-00)))
      (is (not (t/= dt-friday-16-45 (current-close-dt us-cal 1 :minutes dt-friday-17-15))))
      ; 15 min
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 15 :minutes dt-friday-18-00)))
      (is (not (t/= dt-friday-16-45 (current-close-dt us-cal 15 :minutes dt-friday-17-15))))
      ; 1 hour
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 1 :hours dt-friday-18-00)))
      (is (not (t/= dt-friday-16-45 (current-close-dt us-cal 1 :hours dt-friday-17-15))))
      ; 4 hour
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 4 :hours dt-friday-18-00)))
      (is (not (t/= dt-friday-16-45 (current-close-dt us-cal 4 :hours dt-friday-17-15)))))
    (testing "dt before first interval close"
      ; 1 min
      (is (t/= dt-thursday-17-00 (current-close-dt us-cal 1 :minutes dt-friday-09-00-30)))
      (is (not (t/= dt-friday-09-00 (current-close-dt us-cal 1 :minutes dt-friday-09-00-30))))
      ; 15 min
      (is (t/= dt-thursday-17-00 (current-close-dt us-cal 15 :minutes dt-friday-09-10)))
      (is (not (t/= dt-friday-09-00 (current-close-dt us-cal 15 :minutes dt-friday-09-10))))
      ; 1 hour
      (is (t/= dt-thursday-17-00 (current-close-dt us-cal 1 :hours dt-friday-09-15)))
      (is (not (t/= dt-friday-09-00 (current-close-dt us-cal 1 :hours dt-friday-09-15))))
      ; 4 hour
      (is (t/= dt-thursday-17-00 (current-close-dt us-cal 4 :hours dt-friday-12-34)))
      (is (not (t/= dt-friday-09-00 (current-close-dt us-cal 4 :hours dt-friday-12-34)))))
    (testing "dt on trading week start"
      ; 1 min
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 1 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (current-close-dt us-cal 1 :minutes dt-monday-next-09-00))))
      ; 15 min
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 15 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (current-close-dt us-cal 15 :minutes dt-monday-next-09-00))))
      ; 1 hour
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 1 :hours dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (current-close-dt us-cal 1 :hours dt-monday-next-09-00))))
      ; 4 hour
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 4 :hours dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (current-close-dt us-cal 4 :hours dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      ; 1 min
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 1 :minutes dt-friday-17-00)))
      (is (not (t/= dt-thursday-17-00 (current-close-dt us-cal 1 :minutes dt-friday-17-00))))
      ; 15 min
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 15 :minutes dt-friday-17-00)))
      (is (not (t/= dt-thursday-17-00 (current-close-dt us-cal 15 :minutes dt-friday-17-00))))
      ; 1 hour
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 1 :hours dt-friday-17-00)))
      (is (not (t/= dt-thursday-17-00 (current-close-dt us-cal 1 :hours dt-friday-17-00))))
      ; 4 hour
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 4 :hours dt-friday-17-00)))
      (is (not (t/= dt-thursday-17-00 (current-close-dt us-cal 4 :hours dt-friday-17-00)))))
    (testing "dt not on trading day"
      ; 1 min
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 1 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (current-close-dt us-cal 1 :minutes dt-saturday-12-00))))
      ; 15 min
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (current-close-dt us-cal 15 :minutes dt-saturday-12-00))))
      ; 1 hour
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 1 :hours dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (current-close-dt us-cal 1 :hours dt-saturday-12-00))))
      ; 4 hour
      (is (t/= dt-friday-17-00 (current-close-dt us-cal 4 :hours dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (current-close-dt us-cal 4 :hours dt-saturday-12-00))))))

  ;
  ; CURRENT OPEN
  ;
  (deftest current-open-intraday
    (testing "dt inside interval"
      ; 1min
      (is (t/= dt-friday-12-34 (current-open-dt us-cal 1 :minutes dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-33 (current-open-dt us-cal 1 :minutes dt-friday-12-34-56))))
      ; 15 min
      (is (t/= dt-friday-12-30 (current-open-dt us-cal 15 :minutes dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-45 (current-open-dt us-cal 15 :minutes dt-friday-12-34-56))))
      ; 1 hour
      (is (t/= dt-friday-12-00 (current-open-dt us-cal 1 :hours dt-friday-12-34-56)))
      (is (not (t/= dt-friday-13-00 (current-open-dt us-cal 1 :hours dt-friday-12-34-56))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (current-open-dt us-cal 4 :hours dt-friday-16-15)))
      (is (not (t/= dt-friday-16-00 (current-open-dt us-cal 4 :hours dt-friday-16-15)))))
    (testing "dt on interval boundary"
      ; 1 min
      (is (t/= dt-friday-12-30 (current-open-dt us-cal 1 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-29 (current-open-dt us-cal 1 :minutes dt-friday-12-30))))
      ; 15 min
      (is (t/= dt-friday-12-30 (current-open-dt us-cal 15 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-45 (current-open-dt us-cal 15 :minutes dt-friday-12-30))))
      ; 1 hour
      (is (t/= dt-friday-13-00 (current-open-dt us-cal 1 :hours dt-friday-13-00)))
      (is (not (t/= dt-friday-12-00 (current-open-dt us-cal 1 :hours dt-friday-13-00))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (current-open-dt us-cal 4 :hours dt-friday-13-00)))
      (is (not (t/= dt-friday-17-00 (current-open-dt us-cal 4 :hours dt-friday-13-00)))))
    (testing "dt before trading hours"
      ; 1 min
      (is (t/= dt-thursday-16-59 (current-open-dt us-cal 1 :minutes dt-friday-06-00)))
      (is (not (t/= dt-thursday-17-00 (current-open-dt us-cal 1 :minutes dt-friday-06-00))))
      ; 15 min
      (is (t/= dt-thursday-16-45 (current-open-dt us-cal 15 :minutes dt-friday-06-00)))
      (is (not (t/= dt-thursday-17-00 (current-open-dt us-cal 15 :minutes dt-friday-06-00))))
      ; 1 hour
      (is (t/= dt-thursday-16-00 (current-open-dt us-cal 1 :hours dt-friday-06-00)))
      (is (not (t/= dt-thursday-17-00 (current-open-dt us-cal 1 :hours dt-friday-06-00))))
      ; 4 hour
      (is (t/= dt-thursday-13-00 (current-open-dt us-cal 4 :hours dt-friday-06-00)))
      (is (not (t/= dt-thursday-17-00 (current-open-dt us-cal 4 :hours dt-friday-06-00)))))
    (testing "dt after trading hours"
      ; 1 min
      (is (t/= dt-thursday-16-59 (current-open-dt us-cal 1 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-friday-09-00 (current-open-dt us-cal 1 :minutes dt-thursday-23-00))))
      ; 15 min
      (is (t/= dt-thursday-16-45 (current-open-dt us-cal 15 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-friday-09-00 (current-open-dt us-cal 15 :minutes dt-thursday-23-00))))
      ; 1 hour
      (is (t/= dt-thursday-16-00 (current-open-dt us-cal 1 :hours dt-thursday-23-00)))
      (is (not (t/= dt-friday-09-00 (current-open-dt us-cal 1 :hours dt-thursday-23-00))))
      ; 4 hour
      (is (t/= dt-thursday-13-00 (current-open-dt us-cal 4 :hours dt-thursday-23-00)))
      (is (not (t/= dt-friday-09-00 (current-open-dt us-cal 4 :hours dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      ; 1 min
      (is (t/= dt-friday-16-59 (current-open-dt us-cal 1 :minutes dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (current-open-dt us-cal 1 :minutes dt-monday-next-06-00))))
      ; 15 min
      (is (t/= dt-friday-16-45 (current-open-dt us-cal 15 :minutes dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (current-open-dt us-cal 15 :minutes dt-monday-next-06-00))))
      ; 1 hour
      (is (t/= dt-friday-16-00 (current-open-dt us-cal 1 :hours dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (current-open-dt us-cal 1 :hours dt-monday-next-06-00))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (current-open-dt us-cal 4 :hours dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (current-open-dt us-cal 4 :hours dt-monday-next-06-00)))))
    (testing "dt after trading hours (weekend)"
      ; 1 min
      (is (t/= dt-friday-16-59 (current-open-dt us-cal 1 :minutes dt-friday-17-01)))
      (is (not (t/= dt-friday-17-00 (current-open-dt us-cal 1 :minutes dt-friday-17-01))))
      ; 15 min
      (is (t/= dt-friday-16-45 (current-open-dt us-cal 15 :minutes dt-friday-18-00)))
      (is (not (t/= dt-friday-17-00 (current-open-dt us-cal 15 :minutes dt-friday-18-00))))
      ; 1 hour
      (is (t/= dt-friday-16-00 (current-open-dt us-cal 1 :hours dt-friday-18-00)))
      (is (not (t/= dt-friday-17-00 (current-open-dt us-cal 1 :hours dt-friday-18-00))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (current-open-dt us-cal 4 :hours dt-friday-18-00)))
      (is (not (t/= dt-friday-17-00 (current-open-dt us-cal 4 :hours dt-friday-18-00)))))
    (testing "dt after last interval open"
      ; 1 min
      (is (t/= dt-friday-16-59 (current-open-dt us-cal 1 :minutes dt-friday-16-59-30)))
      (is (not (t/= dt-friday-09-00 (current-open-dt us-cal 1 :minutes dt-friday-16-59-30))))
      ; 15 min
      (is (t/= dt-friday-16-45 (current-open-dt us-cal 15 :minutes dt-friday-16-55)))
      (is (not (t/= dt-friday-09-00 (current-open-dt us-cal 15 :minutes dt-friday-16-55))))
      ; 1 hour
      (is (t/= dt-friday-16-00 (current-open-dt us-cal 1 :hours dt-friday-16-30)))
      (is (not (t/= dt-friday-09-00 (current-open-dt us-cal 1 :hours dt-friday-16-30))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (current-open-dt us-cal 4 :hours dt-friday-16-30)))
      (is (not (t/= dt-friday-09-00 (current-open-dt us-cal 4 :hours dt-friday-16-30)))))
    (testing "dt on trading week start"
      ; 1 min
      (is (t/= dt-monday-next-09-00 (current-open-dt us-cal 1 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-friday-16-59 (current-open-dt us-cal 1 :minutes dt-monday-next-09-00))))
      ; 15 min
      (is (t/= dt-monday-next-09-00 (current-open-dt us-cal 15 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-friday-16-45 (current-open-dt us-cal 15 :minutes dt-monday-next-09-00))))
      ; 1 hour
      (is (t/= dt-monday-next-09-00 (current-open-dt us-cal 1 :hours dt-monday-next-09-00)))
      (is (not (t/= dt-friday-16-00 (current-open-dt us-cal 1 :hours dt-monday-next-09-00))))
      ; 4 hour
      (is (t/= dt-monday-next-09-00 (current-open-dt us-cal 4 :hours dt-monday-next-09-00)))
      (is (not (t/= dt-friday-13-00 (current-open-dt us-cal 4 :hours dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      ; 1 min
      (is (t/= dt-friday-16-59 (current-open-dt us-cal 1 :minutes dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (current-open-dt us-cal 1 :minutes dt-friday-17-00))))
      ; 15 min
      (is (t/= dt-friday-16-45 (current-open-dt us-cal 15 :minutes dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (current-open-dt us-cal 15 :minutes dt-friday-17-00))))
      ; 1 hour
      (is (t/= dt-friday-16-00 (current-open-dt us-cal 1 :hours dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (current-open-dt us-cal 1 :hours dt-friday-17-00))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (current-open-dt us-cal 4 :hours dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (current-open-dt us-cal 4 :hours dt-friday-17-00)))))
    (testing "dt not on trading day"
      ; 1 min
      (is (t/= dt-friday-16-59 (current-open-dt us-cal 1 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (current-open-dt us-cal 1 :minutes dt-saturday-12-00))))
      ; 15 min
      (is (t/= dt-friday-16-45 (current-open-dt us-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (current-open-dt us-cal 15 :minutes dt-saturday-12-00))))
      ; 1 hour
      (is (t/= dt-friday-16-00 (current-open-dt us-cal 1 :hours dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (current-open-dt us-cal 1 :hours dt-saturday-12-00))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (current-open-dt us-cal 4 :hours dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (current-open-dt us-cal 4 :hours dt-saturday-12-00))))))

  ;
  ; PREV CLOSE
  ;
  (deftest prev-close-intraday
    (testing "dt inside interval"
      ; 1 min
      (is (t/= dt-friday-12-34 (prior-close-dt us-cal 1 :minutes dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-33 (prior-close-dt us-cal 1 :minutes dt-friday-12-34-56))))
      ; 15 min
      (is (t/= dt-friday-12-30 (prior-close-dt us-cal 15 :minutes dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-15 (prior-close-dt us-cal 15 :minutes dt-friday-12-34-56))))
      ; 1 hour
      (is (t/= dt-friday-12-00 (prior-close-dt us-cal 1 :hours dt-friday-12-34-56)))
      (is (not (t/= dt-friday-13-00 (prior-close-dt us-cal 1 :hours dt-friday-12-34-56))))
      ; 4 hour
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 4 :hours dt-friday-12-34-56)))
      (is (not (t/= dt-friday-13-00 (prior-close-dt us-cal 4 :hours dt-friday-12-34-56)))))
    (testing "dt on interval boundary"
      ; 1 min
      (is (t/= dt-friday-12-29 (prior-close-dt us-cal 1 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-30 (prior-close-dt us-cal 1 :minutes dt-friday-12-30))))
      ; 15 min
      (is (t/= dt-friday-12-15 (prior-close-dt us-cal 15 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-30 (prior-close-dt us-cal 15 :minutes dt-friday-12-30))))
      ; 1 hour
      (is (t/= dt-friday-12-00 (prior-close-dt us-cal 1 :hours dt-friday-13-00)))
      (is (not (t/= dt-friday-13-00 (prior-close-dt us-cal 1 :hours dt-friday-13-00))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (prior-close-dt us-cal 4 :hours dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt us-cal 4 :hours dt-friday-17-00)))))
    (testing "dt before trading hours"
      ; 1 min
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 1 :minutes dt-friday-06-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt us-cal 1 :minutes dt-friday-06-00))))
      ; 15 min
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 15 :minutes dt-friday-06-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt us-cal 15 :minutes dt-friday-06-00))))
      ; 1 hour
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 1 :hours dt-friday-06-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt us-cal 1 :hours dt-friday-06-00))))
      ; 4 hour
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 4 :hours dt-friday-06-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt us-cal 4 :hours dt-friday-06-00)))))
    (testing "dt after trading hours"
      ; 1 min
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 1 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt us-cal 1 :minutes dt-thursday-23-00))))
      ; 15 min
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 15 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt us-cal 15 :minutes dt-thursday-23-00))))
      ; 1 hour
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 1 :hours dt-thursday-23-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt us-cal 1 :hours dt-thursday-23-00))))
      ; 4 hour
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 4 :hours dt-thursday-23-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt us-cal 4 :hours dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      ; 1 min
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 1 :minutes dt-monday-next-06-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt us-cal 1 :minutes dt-monday-next-06-00))))
      ; 15 min
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 15 :minutes dt-monday-next-06-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt us-cal 15 :minutes dt-monday-next-06-00))))
      ; 1 hour
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 1 :hours dt-monday-next-06-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt us-cal 1 :hours dt-monday-next-06-00))))
      ; 4 hour
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 4 :hours dt-monday-next-06-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt us-cal 4 :hours dt-monday-next-06-00)))))
    (testing "dt after trading hours (weekend)"
      ; 1 min
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 1 :minutes dt-friday-18-00)))
      (is (not (t/= dt-thursday-17-00 (prior-close-dt us-cal 1 :minutes dt-friday-18-00))))
      ; 15 min
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 15 :minutes dt-friday-18-00)))
      (is (not (t/= dt-thursday-17-00 (prior-close-dt us-cal 15 :minutes dt-friday-18-00))))
      ; 1 hour
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 1 :hours dt-friday-18-00)))
      (is (not (t/= dt-thursday-17-00 (prior-close-dt us-cal 1 :hours dt-friday-18-00))))
      ; 4 hour
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 4 :hours dt-friday-18-00)))
      (is (not (t/= dt-thursday-17-00 (prior-close-dt us-cal 4 :hours dt-friday-18-00)))))
    (testing "dt before first interval close"
      ; 1 min
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 1 :minutes dt-friday-09-00-30)))
      (is (not (t/= dt-friday-09-00 (prior-close-dt us-cal 1 :minutes dt-friday-09-00-30))))
      ; 15 min
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 15 :minutes dt-friday-09-10)))
      (is (not (t/= dt-friday-09-00 (prior-close-dt us-cal 15 :minutes dt-friday-09-10))))
      ; 1 hour
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 1 :hours dt-friday-09-15)))
      (is (not (t/= dt-friday-09-00 (prior-close-dt us-cal 1 :hours dt-friday-09-15))))
      ; 4 hour
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 4 :hours dt-friday-12-34)))
      (is (not (t/= dt-friday-09-00 (prior-close-dt us-cal 4 :hours dt-friday-12-34)))))
    (testing "dt on trading week start"
      ; 1 min
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 1 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-close-dt us-cal 1 :minutes dt-monday-next-09-00))))
      ; 15 min
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 15 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-close-dt us-cal 15 :minutes dt-monday-next-09-00))))
      ; 1 hour
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 1 :hours dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-close-dt us-cal 1 :hours dt-monday-next-09-00))))
      ; 4 hour
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 4 :hours dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-close-dt us-cal 4 :hours dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      ; 1 min
      (is (t/= dt-friday-16-59 (prior-close-dt us-cal 1 :minutes dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt us-cal 1 :minutes dt-friday-17-00))))
      ; 15 min
      (is (t/= dt-friday-16-45 (prior-close-dt us-cal 15 :minutes dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt us-cal 15 :minutes dt-friday-17-00))))
      ; 1 hour
      (is (t/= dt-friday-16-00 (prior-close-dt us-cal 1 :hours dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt us-cal 1 :hours dt-friday-17-00))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (prior-close-dt us-cal 4 :hours dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt us-cal 4 :hours dt-friday-17-00)))))
    (testing "dt not on trading day"
      ; 1 min
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 1 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (prior-close-dt us-cal 1 :minutes dt-saturday-12-00))))
      ; 15 min
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (prior-close-dt us-cal 15 :minutes dt-saturday-12-00))))
      ; 1 hour
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 1 :hours dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (prior-close-dt us-cal 1 :hours dt-saturday-12-00))))
      ; 4 hour
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal 4 :hours dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (prior-close-dt us-cal 4 :hours dt-saturday-12-00))))))

  ;
  ; NEXT CLOSE
  ;
  (deftest next-close-intraday
    (testing "dt inside interval"
        ; 1 min
      (is (t/= dt-friday-12-35 (next-close-dt us-cal 1 :minutes dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-34 (next-close-dt us-cal 1 :minutes dt-friday-12-34-56))))
        ; 15 min
      (is (t/= dt-friday-12-45 (next-close-dt us-cal 15 :minutes dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-30 (next-close-dt us-cal 15 :minutes dt-friday-12-34-56))))
        ; 1 hour
      (is (t/= dt-friday-13-00 (next-close-dt us-cal 1 :hours dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-00 (next-close-dt us-cal 1 :hours dt-friday-12-34-56))))
        ; 4 hour
      (is (t/= dt-friday-13-00 (next-close-dt us-cal 4 :hours dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-00 (next-close-dt us-cal 4 :hours dt-friday-12-34-56)))))
    (testing "dt on interval boundary"
        ; 1 min
      (is (t/= dt-friday-12-31 (next-close-dt us-cal 1 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-30 (next-close-dt us-cal 1 :minutes dt-friday-12-30))))
        ; 15 min
      (is (t/= dt-friday-12-45 (next-close-dt us-cal 15 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-30 (next-close-dt us-cal 15 :minutes dt-friday-12-30))))
        ; 1 hour
      (is (t/= dt-friday-14-00 (next-close-dt us-cal 1 :hours dt-friday-13-00)))
      (is (not (t/= dt-friday-13-00 (next-close-dt us-cal 1 :hours dt-friday-13-00))))
        ; 4 hour
      (is (t/= dt-friday-17-00 (next-close-dt us-cal 4 :hours dt-friday-13-00)))
      (is (not (t/= dt-friday-13-00 (next-close-dt us-cal 4 :hours dt-friday-13-00)))))
    (testing "dt before trading hours"
        ; 1 min
      (is (t/= dt-friday-09-01 (next-close-dt us-cal 1 :minutes dt-friday-06-00)))
      (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 1 :minutes dt-friday-06-00))))
        ; 15 min
      (is (t/= dt-friday-09-15 (next-close-dt us-cal 15 :minutes dt-friday-06-00)))
      (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 15 :minutes dt-friday-06-00))))
        ; 1 hour
      (is (t/= dt-friday-10-00 (next-close-dt us-cal 1 :hours dt-friday-06-00)))
      (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 1 :hours dt-friday-06-00))))
        ; 4 hour
      (is (t/= dt-friday-13-00 (next-close-dt us-cal 4 :hours dt-friday-06-00)))
      (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 4 :hours dt-friday-06-00)))))
    (testing "dt after trading hours"
        ; 1 min
      (is (t/= dt-friday-09-01 (next-close-dt us-cal 1 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 1 :minutes dt-thursday-23-00))))
        ; 15 min
      (is (t/= dt-friday-09-15 (next-close-dt us-cal 15 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 15 :minutes dt-thursday-23-00))))
        ; 1 hour
      (is (t/= dt-friday-10-00 (next-close-dt us-cal 1 :hours dt-thursday-23-00)))
      (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 1 :hours dt-thursday-23-00))))
        ; 4 hour
      (is (t/= dt-friday-13-00 (next-close-dt us-cal 4 :hours dt-thursday-23-00)))
      (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 4 :hours dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
        ; 1 min
      (is (t/= dt-monday-next-09-01 (next-close-dt us-cal 1 :minutes dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (next-close-dt us-cal 1 :minutes dt-monday-next-06-00))))
        ; 15 min
      (is (t/= dt-monday-next-09-15 (next-close-dt us-cal 15 :minutes dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (next-close-dt us-cal 15 :minutes dt-monday-next-06-00))))
        ; 1 hour
      (is (t/= dt-monday-next-10-00 (next-close-dt us-cal 1 :hours dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (next-close-dt us-cal 1 :hours dt-monday-next-06-00))))
        ; 4 hour
      (is (t/= dt-monday-next-13-00 (next-close-dt us-cal 4 :hours dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (next-close-dt us-cal 4 :hours dt-monday-next-06-00)))))
    (testing "dt after trading hours (weekend)"
        ; 1 min
      (is (t/= dt-monday-next-09-01 (next-close-dt us-cal 1 :minutes dt-friday-18-00)))
      (is (not (t/= dt-monday-next-09-00 (next-close-dt us-cal 1 :minutes dt-friday-18-00))))
        ; 15 min
      (is (t/= dt-monday-next-09-15 (next-close-dt us-cal 15 :minutes dt-friday-18-00)))
      (is (not (t/= dt-monday-next-09-00 (next-close-dt us-cal 15 :minutes dt-friday-18-00))))
        ; 1 hour
      (is (t/= dt-monday-next-10-00 (next-close-dt us-cal 1 :hours dt-friday-18-00)))
      (is (not (t/= dt-monday-next-09-00 (next-close-dt us-cal 1 :hours dt-friday-18-00))))
        ; 4 hour
      (is (t/= dt-monday-next-13-00 (next-close-dt us-cal 4 :hours dt-friday-18-00)))
      (is (not (t/= dt-monday-next-09-00 (next-close-dt us-cal 4 :hours dt-friday-18-00)))))
    (testing "dt before first interval close"
        ; 1 min
      (is (t/= dt-friday-09-01 (next-close-dt us-cal 1 :minutes dt-friday-09-00-30)))
      (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 1 :minutes dt-friday-09-00-30))))
        ; 15 min
      (is (t/= dt-friday-09-15 (next-close-dt us-cal 15 :minutes dt-friday-09-10)))
      (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 15 :minutes dt-friday-09-10))))
        ; 1 hour
      (is (t/= dt-friday-10-00 (next-close-dt us-cal 1 :hours dt-friday-09-15)))
      (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 1 :hours dt-friday-09-15))))
        ; 4 hour
      (is (t/= dt-friday-13-00 (next-close-dt us-cal 4 :hours dt-friday-12-34)))
      (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 4 :hours dt-friday-12-34)))))
    (testing "dt on trading week start"
        ; 1 min
      (is (t/= dt-monday-next-09-01 (next-close-dt us-cal 1 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (next-close-dt us-cal 1 :minutes dt-monday-next-09-00))))
        ; 15 min
      (is (t/= dt-monday-next-09-15 (next-close-dt us-cal 15 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (next-close-dt us-cal 15 :minutes dt-monday-next-09-00))))
        ; 1 hour
      (is (t/= dt-monday-next-10-00 (next-close-dt us-cal 1 :hours dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (next-close-dt us-cal 1 :hours dt-monday-next-09-00))))
        ; 4 hour
      (is (t/= dt-monday-next-13-00 (next-close-dt us-cal 4 :hours dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (next-close-dt us-cal 4 :hours dt-monday-next-09-00)))))
    (testing "dt on trading week close"
        ; 1 min
      (is (t/= dt-monday-next-09-01 (next-close-dt us-cal 1 :minutes dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (next-close-dt us-cal 1 :minutes dt-friday-17-00))))
        ; 15 min
      (is (t/= dt-monday-next-09-15 (next-close-dt us-cal 15 :minutes dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (next-close-dt us-cal 15 :minutes dt-friday-17-00))))
        ; 1 hour
      (is (t/= dt-monday-next-10-00 (next-close-dt us-cal 1 :hours dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (next-close-dt us-cal 1 :hours dt-friday-17-00))))
        ; 4 hour
      (is (t/= dt-monday-next-13-00 (next-close-dt us-cal 4 :hours dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (next-close-dt us-cal 4 :hours dt-friday-17-00)))))
    (testing "dt not on trading day"
        ; 1 min
      (is (t/= dt-monday-next-09-01 (next-close-dt us-cal 1 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (next-close-dt us-cal 1 :minutes dt-saturday-12-00))))
        ; 15 min
      (is (t/= dt-monday-next-09-15 (next-close-dt us-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (next-close-dt us-cal 15 :minutes dt-saturday-12-00))))
        ; 1 hour
      (is (t/= dt-monday-next-10-00 (next-close-dt us-cal 1 :hours dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (next-close-dt us-cal 1 :hours dt-saturday-12-00))))
        ; 4 hour
      (is (t/= dt-monday-next-13-00 (next-close-dt us-cal 4 :hours dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (next-close-dt us-cal 4 :hours dt-saturday-12-00))))))

  ;
  ; PREV OPEN
  ;
  (deftest prev-open-intraday
    (testing "dt inside interval"
      ; 1 min
      (is (t/= dt-friday-12-34 (prior-open-dt us-cal 1 :minutes dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-33 (prior-open-dt us-cal 1 :minutes dt-friday-12-34-56))))
      ; 15 min
      (is (t/= dt-friday-12-30 (prior-open-dt us-cal 15 :minutes dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-15 (prior-open-dt us-cal 15 :minutes dt-friday-12-34-56))))
      ; 1 hour
      (is (t/= dt-friday-12-00 (prior-open-dt us-cal 1 :hours dt-friday-12-34-56)))
      (is (not (t/= dt-friday-13-00 (prior-open-dt us-cal 1 :hours dt-friday-12-34-56))))
      ; 4 hour
      (is (t/= dt-friday-09-00 (prior-open-dt us-cal 4 :hours dt-friday-12-34-56)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 4 :hours dt-friday-12-34-56)))))
    (testing "dt on interval boundary"
      ; 1 min
      (is (t/= dt-friday-12-29 (prior-open-dt us-cal 1 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-30 (prior-open-dt us-cal 1 :minutes dt-friday-12-30))))
      ; 15 min
      (is (t/= dt-friday-12-15 (prior-open-dt us-cal 15 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-30 (prior-open-dt us-cal 15 :minutes dt-friday-12-30))))
      ; 1 hour
      (is (t/= dt-friday-12-00 (prior-open-dt us-cal 1 :hours dt-friday-13-00)))
      (is (not (t/= dt-friday-13-00 (prior-open-dt us-cal 1 :hours dt-friday-13-00))))
      ; 4 hour
      (is (t/= dt-friday-09-00 (prior-open-dt us-cal 4 :hours dt-friday-13-00)))
      (is (not (t/= dt-friday-13-00 (prior-open-dt us-cal 4 :hours dt-friday-13-00)))))
    (testing "dt before trading hours"
      ; 1 min
      (is (t/= dt-thursday-16-59 (prior-open-dt us-cal 1 :minutes dt-friday-06-00)))
      (is (not (t/= dt-thursday-17-00 (prior-open-dt us-cal 1 :minutes dt-friday-06-00))))
      ; 15 min
      (is (t/= dt-thursday-16-45 (prior-open-dt us-cal 15 :minutes dt-friday-06-00)))
      (is (not (t/= dt-thursday-17-00 (prior-open-dt us-cal 15 :minutes dt-friday-06-00))))
      ; 1 hour
      (is (t/= dt-thursday-16-00 (prior-open-dt us-cal 1 :hours dt-friday-06-00)))
      (is (not (t/= dt-thursday-17-00 (prior-open-dt us-cal 1 :hours dt-friday-06-00))))
      ; 4 hour
      (is (t/= dt-thursday-13-00 (prior-open-dt us-cal 4 :hours dt-friday-06-00)))
      (is (not (t/= dt-thursday-17-00 (prior-open-dt us-cal 4 :hours dt-friday-06-00)))))
    (testing "dt after trading hours"
      ; 1 min
      (is (t/= dt-thursday-16-59 (prior-open-dt us-cal 1 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-thursday-17-00 (prior-open-dt us-cal 1 :minutes dt-thursday-23-00))))
      ; 15 min
      (is (t/= dt-thursday-16-45 (prior-open-dt us-cal 15 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-thursday-17-00 (prior-open-dt us-cal 15 :minutes dt-thursday-23-00))))
      ; 1 hour
      (is (t/= dt-thursday-16-00 (prior-open-dt us-cal 1 :hours dt-thursday-23-00)))
      (is (not (t/= dt-thursday-17-00 (prior-open-dt us-cal 1 :hours dt-thursday-23-00))))
      ; 4 hour
      (is (t/= dt-thursday-13-00 (prior-open-dt us-cal 4 :hours dt-thursday-23-00)))
      (is (not (t/= dt-thursday-17-00 (prior-open-dt us-cal 4 :hours dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      ; 1 min
      (is (t/= dt-friday-16-59 (prior-open-dt us-cal 1 :minutes dt-monday-next-06-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 1 :minutes dt-monday-next-06-00))))
      ; 15 min
      (is (t/= dt-friday-16-45 (prior-open-dt us-cal 15 :minutes dt-monday-next-06-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 15 :minutes dt-monday-next-06-00))))
      ; 1 hour
      (is (t/= dt-friday-16-00 (prior-open-dt us-cal 1 :hours dt-monday-next-06-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 1 :hours dt-monday-next-06-00))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (prior-open-dt us-cal 4 :hours dt-monday-next-06-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 4 :hours dt-monday-next-06-00)))))
    (testing "dt after trading hours (weekend)"
      ; 1 min
      (is (t/= dt-friday-16-59 (prior-open-dt us-cal 1 :minutes dt-friday-18-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 1 :minutes dt-friday-18-00))))
      ; 15 min
      (is (t/= dt-friday-16-45 (prior-open-dt us-cal 15 :minutes dt-friday-18-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 15 :minutes dt-friday-18-00))))
      ; 1 hour
      (is (t/= dt-friday-16-00 (prior-open-dt us-cal 1 :hours dt-friday-18-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 1 :hours dt-friday-18-00))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (prior-open-dt us-cal 4 :hours dt-friday-18-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 4 :hours dt-friday-18-00)))))
    (testing "dt after last interval open"
      ; 1 min
      (is (t/= dt-friday-16-59 (prior-open-dt us-cal 1 :minutes dt-friday-16-59-30)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 1 :minutes dt-friday-16-59-30))))
      ; 15 min
      (is (t/= dt-friday-16-45 (prior-open-dt us-cal 15 :minutes dt-friday-16-59-30)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 15 :minutes dt-friday-16-59-30))))
      ; 1 hour
      (is (t/= dt-friday-16-00 (prior-open-dt us-cal 1 :hours dt-friday-16-59-30)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 1 :hours dt-friday-16-59-30))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (prior-open-dt us-cal 4 :hours dt-friday-16-59-30)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 4 :hours dt-friday-16-59-30)))))
    (testing "dt on trading week start"
      ; 1 min
      (is (t/= dt-friday-16-59 (prior-open-dt us-cal 1 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-open-dt us-cal 1 :minutes dt-monday-next-09-00))))
      ; 15 min
      (is (t/= dt-friday-16-45 (prior-open-dt us-cal 15 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 15 :minutes dt-monday-next-09-00))))
      ; 1 hour
      (is (t/= dt-friday-16-00 (prior-open-dt us-cal 1 :hours dt-monday-next-09-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 1 :hours dt-monday-next-09-00))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (prior-open-dt us-cal 4 :hours dt-monday-next-09-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 4 :hours dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      ; 1 min
      (is (t/= dt-friday-16-59 (prior-open-dt us-cal 1 :minutes dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 1 :minutes dt-friday-17-00))))
      ; 15 min
      (is (t/= dt-friday-16-45 (prior-open-dt us-cal 15 :minutes dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 15 :minutes dt-friday-17-00))))
      ; 1 hour
      (is (t/= dt-friday-16-00 (prior-open-dt us-cal 1 :hours dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 1 :hours dt-friday-17-00))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (prior-open-dt us-cal 4 :hours dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (prior-open-dt us-cal 4 :hours dt-friday-17-00)))))
    (testing "dt not on trading day"
      ; 1 min
      (is (t/= dt-friday-16-59 (prior-open-dt us-cal 1 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (prior-open-dt us-cal 1 :minutes dt-saturday-12-00))))
      ; 15 min
      (is (t/= dt-friday-16-45 (prior-open-dt us-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (prior-open-dt us-cal 15 :minutes dt-saturday-12-00))))
      ; 1 hour
      (is (t/= dt-friday-16-00 (prior-open-dt us-cal 1 :hours dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (prior-open-dt us-cal 1 :hours dt-saturday-12-00))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (prior-open-dt us-cal 4 :hours dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (prior-open-dt us-cal 4 :hours dt-saturday-12-00))))))

  ;
  ; NEXT OPEN
  ;
  (deftest next-open-intraday
    (testing "dt inside interval"
      ; 1 min
      (is (t/= dt-friday-12-35 (next-open-dt us-cal 1 :minutes dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-34 (next-open-dt us-cal 1 :minutes dt-friday-12-34-56))))
      ; 15 min
      (is (t/= dt-friday-12-45 (next-open-dt us-cal 15 :minutes dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-30 (next-open-dt us-cal 15 :minutes dt-friday-12-34-56))))
      ; 1 hour
      (is (t/= dt-friday-13-00 (next-open-dt us-cal 1 :hours dt-friday-12-34-56)))
      (is (not (t/= dt-friday-12-00 (next-open-dt us-cal 1 :hours dt-friday-12-34-56))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (next-open-dt us-cal 4 :hours dt-friday-12-34-56)))
      (is (not (t/= dt-friday-17-00 (next-open-dt us-cal 4 :hours dt-friday-12-34-56)))))
    (testing "dt on interval boundary"
      ; 1 min
      (is (t/= dt-friday-12-31 (next-open-dt us-cal 1 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-30 (next-open-dt us-cal 1 :minutes dt-friday-12-30))))
      ; 15 min
      (is (t/= dt-friday-12-45 (next-open-dt us-cal 15 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-30 (next-open-dt us-cal 15 :minutes dt-friday-12-30))))
      ; 1 hour
      (is (t/= dt-friday-14-00 (next-open-dt us-cal 1 :hours dt-friday-13-00)))
      (is (not (t/= dt-friday-13-00 (next-open-dt us-cal 1 :hours dt-friday-13-00))))
      ; 4 hour
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 4 :hours dt-friday-13-00)))
      (is (not (t/= dt-friday-17-00 (next-open-dt us-cal 4 :hours dt-friday-13-00)))))
    (testing "dt before trading hours"
      ; 1 min
      (is (t/= dt-friday-09-00 (next-open-dt us-cal 1 :minutes dt-friday-06-00)))
      (is (not (t/= dt-thursday-16-59 (next-open-dt us-cal 1 :minutes dt-friday-06-00))))
      ; 15 min
      (is (t/= dt-friday-09-00 (next-open-dt us-cal 15 :minutes dt-friday-06-00)))
      (is (not (t/= dt-thursday-16-45 (next-open-dt us-cal 15 :minutes dt-friday-06-00))))
      ; 1 hour
      (is (t/= dt-friday-09-00 (next-open-dt us-cal 1 :hours dt-friday-06-00)))
      (is (not (t/= dt-thursday-16-00 (next-open-dt us-cal 1 :hours dt-friday-06-00))))
      ; 4 hour
      (is (t/= dt-friday-09-00 (next-open-dt us-cal 4 :hours dt-friday-06-00)))
      (is (not (t/= dt-thursday-13-00 (next-open-dt us-cal 4 :hours dt-friday-06-00)))))
    (testing "dt after trading hours"
      ; 1 min
      (is (t/= dt-friday-09-00 (next-open-dt us-cal 1 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-thursday-16-59 (next-open-dt us-cal 1 :minutes dt-thursday-23-00))))
      ; 15 min
      (is (t/= dt-friday-09-00 (next-open-dt us-cal 15 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-thursday-16-45 (next-open-dt us-cal 15 :minutes dt-thursday-23-00))))
      ; 1 hour
      (is (t/= dt-friday-09-00 (next-open-dt us-cal 1 :hours dt-thursday-23-00)))
      (is (not (t/= dt-thursday-16-00 (next-open-dt us-cal 1 :hours dt-thursday-23-00))))
      ; 4 hour
      (is (t/= dt-friday-09-00 (next-open-dt us-cal 4 :hours dt-thursday-23-00)))
      (is (not (t/= dt-thursday-13-00 (next-open-dt us-cal 4 :hours dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      ; 1 min
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 1 :minutes dt-monday-next-06-00)))
      (is (not (t/= dt-friday-16-59 (next-open-dt us-cal 1 :minutes dt-monday-next-06-00))))
      ; 15 min
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 15 :minutes dt-monday-next-06-00)))
      (is (not (t/= dt-friday-16-45 (next-open-dt us-cal 15 :minutes dt-monday-next-06-00))))
      ; 1 hour
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 1 :hours dt-monday-next-06-00)))
      (is (not (t/= dt-friday-16-00 (next-open-dt us-cal 1 :hours dt-monday-next-06-00))))
      ; 4 hour
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 4 :hours dt-monday-next-06-00)))
      (is (not (t/= dt-friday-13-00 (next-open-dt us-cal 4 :hours dt-monday-next-06-00)))))
    (testing "dt after trading hours (weekend)"
      ; 1 min
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 1 :minutes dt-friday-18-00)))
      (is (not (t/= dt-friday-16-59 (next-open-dt us-cal 1 :minutes dt-friday-18-00))))
      ; 15 min
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 15 :minutes dt-friday-18-00)))
      (is (not (t/= dt-friday-16-45 (next-open-dt us-cal 15 :minutes dt-friday-18-00))))
      ; 1 hour
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 1 :hours dt-friday-18-00)))
      (is (not (t/= dt-friday-16-00 (next-open-dt us-cal 1 :hours dt-friday-18-00))))
      ; 4 hour
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 4 :hours dt-friday-18-00)))
      (is (not (t/= dt-friday-13-00 (next-open-dt us-cal 4 :hours dt-friday-18-00)))))
    (testing "dt after last interval open"
      ; 1 min
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 1 :minutes dt-friday-16-59-30)))
      (is (not (t/= dt-friday-17-00 (next-open-dt us-cal 1 :minutes dt-friday-16-59-30))))
      ; 15 min
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 15 :minutes dt-friday-16-59-30)))
      (is (not (t/= dt-friday-16-45 (next-open-dt us-cal 15 :minutes dt-friday-16-59-30))))
      ; 1 hour
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 1 :hours dt-friday-16-59-30)))
      (is (not (t/= dt-friday-16-00 (next-open-dt us-cal 1 :hours dt-friday-16-59-30))))
      ; 4 hour
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 4 :hours dt-friday-16-59-30)))
      (is (not (t/= dt-friday-13-00 (next-open-dt us-cal 4 :hours dt-friday-16-59-30)))))
    (testing "dt on trading week start"
      ; 1 min
      (is (t/= dt-monday-next-09-01 (next-open-dt us-cal 1 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (next-open-dt us-cal 1 :minutes dt-monday-next-09-00))))
      ; 15 min
      (is (t/= dt-monday-next-09-15 (next-open-dt us-cal 15 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (next-open-dt us-cal 15 :minutes dt-monday-next-09-00))))
      ; 1 hour
      (is (t/= dt-monday-next-10-00 (next-open-dt us-cal 1 :hours dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (next-open-dt us-cal 1 :hours dt-monday-next-09-00))))
      ; 4 hour
      (is (t/= dt-monday-next-13-00 (next-open-dt us-cal 4 :hours dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (next-open-dt us-cal 4 :hours dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      ; 1 min
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 1 :minutes dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (next-open-dt us-cal 1 :minutes dt-friday-17-00))))
      ; 15 min
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 15 :minutes dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (next-open-dt us-cal 15 :minutes dt-friday-17-00))))
      ; 1 hour
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 1 :hours dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (next-open-dt us-cal 1 :hours dt-friday-17-00))))
      ; 4 hour
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 4 :hours dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (next-open-dt us-cal 4 :hours dt-friday-17-00)))))
    (testing "dt not on trading day"
      ; 1 min
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 1 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-friday-16-59 (next-open-dt us-cal 1 :minutes dt-saturday-12-00))))
      ; 15 min
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-friday-16-45 (next-open-dt us-cal 15 :minutes dt-saturday-12-00))))
      ; 1 hour
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 1 :hours dt-saturday-12-00)))
      (is (not (t/= dt-friday-16-00 (next-open-dt us-cal 1 :hours dt-saturday-12-00))))
      ; 4 hour
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 4 :hours dt-saturday-12-00)))
      (is (not (t/= dt-friday-13-00 (next-open-dt us-cal 4 :hours dt-saturday-12-00)))))))