(ns ta.calendar.intraday-forex-test
  (:require [clojure.test :refer :all]
            [tick.core :as t]
            [ta.calendar.calendars :as cal]
            [ta.calendar.intraday :refer [prior-close-dt next-close-dt
                                          prior-open-dt next-open-dt
                                          current-close-dt current-open-dt]]))

(defn to-est [dt-str]
  (t/in (t/date-time dt-str) "America/New_York"))

(defn to-est-at [date time-str]
  (-> date
      (t/at time-str)
      (t/in "America/New_York")))

(let [dt-monday-09-00 (to-est "2024-02-05T09:00:00")

      dt-thursday-09-00 (to-est "2024-02-08T09:00:00")
      dt-thursday-13-00 (to-est "2024-02-08T13:00:00")
      dt-thursday-16-00 (to-est "2024-02-08T16:00:00")
      dt-thursday-16-15 (to-est "2024-02-08T16:15:00")
      dt-thursday-16-29 (to-est "2024-02-08T16:29:00")
      dt-thursday-16-30 (to-est "2024-02-08T16:30:00")
      dt-thursday-16-45 (to-est "2024-02-08T16:45:00")
      dt-thursday-16-59 (to-est "2024-02-08T16:59:00")
      dt-thursday-17-00 (to-est "2024-02-08T17:00:00")
      dt-thursday-21-00 (to-est "2024-02-08T21:00:00")
      dt-thursday-22-00 (to-est "2024-02-08T22:00:00")
      dt-thursday-22-45 (to-est "2024-02-08T22:45:00")
      dt-thursday-22-59 (to-est "2024-02-08T22:59:00")
      dt-thursday-23-00 (to-est "2024-02-08T23:00:00")

      dt-friday-00-00 (to-est "2024-02-09T00:00:00")
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
      dt-friday-16-29 (to-est "2024-02-09T16:29:00")
      dt-friday-16-30 (to-est "2024-02-09T16:30:00")
      dt-friday-16-45 (to-est "2024-02-09T16:45:00")
      dt-friday-16-55 (to-est "2024-02-09T16:55:00")
      dt-friday-16-59 (to-est "2024-02-09T16:59:00")
      dt-friday-16-59-30 (to-est "2024-02-09T16:59:30")
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
      dt-monday-next-09-01 (to-est "2024-02-12T09:01:00")
      dt-monday-next-09-15 (to-est "2024-02-12T09:15:00")
      dt-monday-next-10-00 (to-est "2024-02-12T10:00:00")
      dt-monday-next-13-00 (to-est "2024-02-12T13:00:00")

      forex-cal (:forex cal/calendars)]

  ; TODO: day change: 00:00:00 prior-close-dt. eg. shift 15min into prev day


  ;(deftest current-close-overnight
  ;  (testing "dt inside interval"
  ;    (is (t/= dt-friday-12-30 (current-close-dt us-forex 15 :minutes dt-friday-12-34-56)))
  ;    (is (not (t/= dt-friday-12-15 (current-close-dt us-forex 15 :minutes dt-friday-12-34-56))))
  ;    (is (not (t/= dt-friday-12-34 (current-close-dt us-forex 15 :minutes dt-friday-12-34-56)))))
  ;  (testing "dt on interval boundary"
  ;    (is (t/= dt-friday-12-30 (current-close-dt us-forex 15 :minutes dt-friday-12-30)))
  ;    (is (not (t/= dt-friday-12-15 (current-close-dt us-forex 15 :minutes dt-friday-12-30))))
  ;    (is (not (t/= dt-friday-12-45 (current-close-dt us-forex 15 :minutes dt-friday-12-30)))))
  ;  (testing "dt outside trading hours"
  ;    (is (t/= dt-friday-17-00 (current-close-dt us-forex 15 :minutes dt-friday-18-00)))
  ;    (is (not (t/= dt-monday-next-09-00 (current-close-dt us-forex 15 :minutes dt-friday-18-00))))
  ;    (is (t/= dt-friday-17-00 (current-close-dt us-forex 15 :minutes dt-saturday-09-00)))
  ;    (is (not (t/= dt-monday-next-09-00 (current-close-dt us-forex 15 :minutes dt-saturday-09-00))))
  ;    (is (t/= dt-friday-17-00 (current-close-dt us-forex 15 :minutes dt-monday-next-06-00)))
  ;    (is (not (t/= dt-monday-next-09-00 (current-close-dt us-forex 15 :minutes dt-monday-next-06-00))))))


  (deftest prior-close-overnight
    ; close on 16:30 for forex (custom definition)
    (testing "dt before trading hours - forex/overnight"
      ; 1 min
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 1 :minutes dt-sunday-06-00)))
      (is (not (t/= dt-sunday-17-00 (prior-close-dt forex-cal 1 :minutes dt-sunday-06-00))))
      ; 15 min
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 15 :minutes dt-sunday-06-00)))
      (is (not (t/= dt-sunday-17-00 (prior-close-dt forex-cal 15 :minutes dt-sunday-06-00))))
      ; 1 hour
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 1 :hours dt-sunday-06-00)))
      (is (not (t/= dt-sunday-17-00 (prior-close-dt forex-cal 1 :hours dt-sunday-06-00))))
      ; 4 hour
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 4 :hours dt-sunday-06-00)))
      (is (not (t/= dt-sunday-17-00 (prior-close-dt forex-cal 4 :hours dt-sunday-06-00)))))
    (testing "dt after trading hours - forex/overnight"
      ; 1 min
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 1 :minutes dt-friday-18-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt forex-cal 1 :minutes dt-friday-18-00))))
      ; 15 min
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 15 :minutes dt-friday-18-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt forex-cal 15 :minutes dt-friday-18-00))))
      ; 1 hour
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 1 :hours dt-friday-18-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt forex-cal 1 :hours dt-friday-18-00))))
      ; 4 hour
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 4 :hours dt-friday-18-00)))
      (is (not (t/= dt-thursday-09-00 (prior-close-dt forex-cal 4 :hours dt-friday-18-00)))))
    (testing "dt on trading week start"
      ; 1 min
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 1 :minutes dt-sunday-17-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-close-dt forex-cal 1 :minutes dt-sunday-17-00))))
      ; 15 min
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 15 :minutes dt-sunday-17-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-close-dt forex-cal 15 :minutes dt-sunday-17-00))))
      ; 1 hour
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 1 :hours dt-sunday-17-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-close-dt forex-cal 1 :hours dt-sunday-17-00))))
      ; 4 hour
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 4 :hours dt-sunday-17-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-close-dt forex-cal 4 :hours dt-sunday-17-00)))))
    (testing "dt on trading week close"
      ; 1 min
      (is (t/= dt-friday-16-29 (prior-close-dt forex-cal 1 :minutes dt-friday-16-30)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt forex-cal 1 :minutes dt-friday-16-30))))
      ; 15 min
      (is (t/= dt-friday-16-15 (prior-close-dt forex-cal 15 :minutes dt-friday-16-30)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt forex-cal 15 :minutes dt-friday-16-30))))
      ; 1 hour
      (is (t/= dt-friday-16-00 (prior-close-dt forex-cal 1 :hours dt-friday-16-30)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt forex-cal 1 :hours dt-friday-16-30))))
      ; 4 hour
      (is (t/= dt-friday-13-00 (prior-close-dt forex-cal 4 :hours dt-friday-16-30)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt forex-cal 4 :hours dt-friday-16-30)))))
    (testing "dt not on trading day"
      ; 1 min
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 1 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (prior-close-dt forex-cal 1 :minutes dt-saturday-12-00))))
      ; 15 min
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (prior-close-dt forex-cal 15 :minutes dt-saturday-12-00))))
      ; 1 hour
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 1 :hours dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (prior-close-dt forex-cal 1 :hours dt-saturday-12-00))))
      ; 4 hour
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal 4 :hours dt-saturday-12-00)))
      (is (not (t/= dt-saturday-12-00 (prior-close-dt forex-cal 4 :hours dt-saturday-12-00)))))
    (testing "dt after close: inside next started day interval"
      ; 1 min
      (is (t/= dt-thursday-22-59 (prior-close-dt forex-cal 1 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 1 :minutes dt-thursday-23-00))))
      ; 15 min
      (is (t/= dt-thursday-22-45 (prior-close-dt forex-cal 15 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 15 :minutes dt-thursday-23-00))))
      ; 1 hour
      (is (t/= dt-thursday-22-00 (prior-close-dt forex-cal 1 :hours dt-thursday-23-00)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 1 :hours dt-thursday-23-00))))
      ; 4 hour
      (is (t/= dt-thursday-21-00 (prior-close-dt forex-cal 4 :hours dt-thursday-23-00)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 4 :hours dt-thursday-23-00)))))
    (testing "dt after close: on next started day interval boundary"
      ; 1 min
      (is (t/= dt-thursday-16-30 (prior-close-dt forex-cal 1 :minutes dt-thursday-17-00)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 1 :minutes dt-thursday-17-00))))
      ; 15 min
      (is (t/= dt-thursday-16-30 (prior-close-dt forex-cal 15 :minutes dt-thursday-17-00)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 15 :minutes dt-thursday-17-00))))
      ; 1 hour
      (is (t/= dt-thursday-16-00 (prior-close-dt forex-cal 1 :hours dt-thursday-17-00)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 1 :hours dt-thursday-17-00))))
      ; 4 hour
      (is (t/= dt-thursday-13-00 (prior-close-dt forex-cal 4 :hours dt-thursday-17-00)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 4 :hours dt-thursday-17-00)))))
    (testing "dt after close: on closed day interval boundary (with following trading day)"
      ; 1 min
      (is (t/= dt-thursday-16-29 (prior-close-dt forex-cal 1 :minutes dt-thursday-16-30)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 1 :minutes dt-thursday-16-30))))
      ; 15 min
      (is (t/= dt-thursday-16-15 (prior-close-dt forex-cal 15 :minutes dt-thursday-16-30)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 15 :minutes dt-thursday-16-30))))
      ; 1 hour
      (is (t/= dt-thursday-16-00 (prior-close-dt forex-cal 1 :hours dt-thursday-16-30)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 1 :hours dt-thursday-16-30))))
      ; 4 hour
      (is (t/= dt-thursday-13-00 (prior-close-dt forex-cal 4 :hours dt-thursday-16-30)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 4 :hours dt-thursday-16-30)))))
    (testing "dt after close: inside gap between 2 trading days - (outside trading hours, after close, before open)"
      ; 1 min
      (is (t/= dt-thursday-16-30 (prior-close-dt forex-cal 1 :minutes dt-thursday-16-45)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 1 :minutes dt-thursday-16-45))))
      ; 15 min
      (is (t/= dt-thursday-16-30 (prior-close-dt forex-cal 15 :minutes dt-thursday-16-45)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 15 :minutes dt-thursday-16-45))))
      ; 1 hour
      (is (t/= dt-thursday-16-00 (prior-close-dt forex-cal 1 :hours dt-thursday-16-45)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 1 :hours dt-thursday-16-45))))
      ; 4 hour
      (is (t/= dt-thursday-13-00 (prior-close-dt forex-cal 4 :hours dt-thursday-16-45)))
      (is (not (t/= dt-thursday-23-00 (prior-close-dt forex-cal 4 :hours dt-thursday-16-45)))))
    )

  ;(deftest prev-close-intraday
  ;  (testing "dt before first interval close"
  ;    ; 1 min
  ;    (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 1 :minutes dt-friday-09-00-30)))
  ;    (is (not (t/= dt-friday-09-00 (prior-close-dt us-cal 1 :minutes dt-friday-09-00-30))))
  ;    ; 15 min
  ;    (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 15 :minutes dt-friday-09-10)))
  ;    (is (not (t/= dt-friday-09-00 (prior-close-dt us-cal 15 :minutes dt-friday-09-10))))
  ;    ; 1 hour
  ;    (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 1 :hours dt-friday-09-15)))
  ;    (is (not (t/= dt-friday-09-00 (prior-close-dt us-cal 1 :hours dt-friday-09-15))))
  ;    ; 4 hour
  ;    (is (t/= dt-thursday-17-00 (prior-close-dt us-cal 4 :hours dt-friday-12-34)))
  ;    (is (not (t/= dt-friday-09-00 (prior-close-dt us-cal 4 :hours dt-friday-12-34)))))


  (deftest next-close-overnight
    ; close on 16:30 for forex (custom definition)
    (testing "dt before trading hours - forex/overnight"
      (is (t/= dt-sunday-17-15 (next-close-dt forex-cal 15 :minutes dt-sunday-06-00)))
      (is (not (t/= dt-friday-16-30 (next-close-dt forex-cal 15 :minutes dt-sunday-06-00)))))
    (testing "dt after trading hours - forex/overnight"
      (is (t/= dt-sunday-17-15 (next-close-dt forex-cal 15 :minutes dt-friday-18-00)))
      (is (t/= dt-sunday-17-15 (next-close-dt forex-cal 15 :minutes dt-friday-17-15)))
      (is (not (t/= dt-friday-16-15 (next-open-dt forex-cal 15 :minutes dt-friday-17-15)))))
    (testing "dt on trading week start"
      (is (t/= dt-sunday-17-15 (next-close-dt forex-cal 15 :minutes dt-sunday-17-00)))
      (is (not (t/= dt-monday-next-09-00 (next-close-dt forex-cal 15 :minutes dt-sunday-17-00))))
      (is (not (t/= dt-sunday-17-00 (next-close-dt forex-cal 15 :minutes dt-sunday-17-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-sunday-17-15 (next-close-dt forex-cal 15 :minutes dt-friday-16-30)))
      (is (not (t/= dt-friday-17-00 (next-close-dt forex-cal 15 :minutes dt-friday-16-30)))))
    (testing "dt not on trading day"
      (is (t/= dt-sunday-17-15 (next-close-dt forex-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-friday-17-00 (next-close-dt forex-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-sunday-17-00 (next-close-dt forex-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-09-00 (next-close-dt forex-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-17-00 (next-close-dt forex-cal 15 :minutes dt-saturday-12-00))))))


  (deftest prior-open-overnight
    ; close on 16:30 for forex (custom definition)
    (testing "dt before trading hours - forex/overnight"
      (is (t/= dt-friday-16-15 (prior-open-dt forex-cal 15 :minutes dt-sunday-06-00)))
      (is (not (t/= dt-sunday-17-00 (prior-open-dt forex-cal 15 :minutes dt-sunday-06-00)))))
    (testing "dt after trading hours - forex/overnight"
      (is (t/= dt-friday-16-15 (prior-open-dt forex-cal 15 :minutes dt-friday-18-00)))
      (is (t/= dt-friday-16-15 (prior-open-dt forex-cal 15 :minutes dt-friday-17-15)))
      (is (not (t/= dt-friday-16-15 (next-open-dt forex-cal 15 :minutes dt-friday-17-15)))))
    (testing "dt on trading week start"
      (is (t/= dt-friday-16-15 (prior-open-dt forex-cal 15 :minutes dt-sunday-17-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-open-dt forex-cal 15 :minutes dt-sunday-17-00))))
      (is (not (t/= dt-sunday-17-00 (prior-open-dt forex-cal 15 :minutes dt-sunday-17-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-friday-16-15 (prior-open-dt forex-cal 15 :minutes dt-friday-16-30)))
      (is (not (t/= dt-friday-16-30 (prior-open-dt forex-cal 15 :minutes dt-friday-16-30)))))
    (testing "dt not on trading day"
      (is (t/= dt-friday-16-15 (prior-open-dt forex-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-friday-09-00 (prior-open-dt forex-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-sunday-17-00 (prior-open-dt forex-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-09-00 (prior-open-dt forex-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-17-00 (prior-open-dt forex-cal 15 :minutes dt-saturday-12-00))))))


  (deftest next-open-overnight
    ; close on 16:30 for forex (custom definition)
    (testing "dt before trading hours - forex/overnight"
      (is (t/= dt-sunday-17-00 (next-open-dt forex-cal 15 :minutes dt-sunday-06-00)))
      (is (not (t/= dt-friday-16-30 (next-open-dt forex-cal 15 :minutes dt-sunday-06-00)))))
    (testing "dt after trading hours - forex/overnight"
      (is (t/= dt-sunday-17-00 (next-open-dt forex-cal 15 :minutes dt-friday-18-00)))
      (is (t/= dt-sunday-17-00 (next-open-dt forex-cal 15 :minutes dt-friday-17-15)))
      (is (not (t/= dt-friday-16-15 (next-open-dt forex-cal 15 :minutes dt-friday-17-15)))))
    (testing "dt on trading week start"
      (is (t/= dt-sunday-17-15 (next-open-dt forex-cal 15 :minutes dt-sunday-17-00)))
      (is (not (t/= dt-monday-next-09-00 (next-open-dt forex-cal 15 :minutes dt-sunday-17-00))))
      (is (not (t/= dt-sunday-17-00 (next-open-dt forex-cal 15 :minutes dt-sunday-17-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-sunday-17-00 (next-open-dt forex-cal 15 :minutes dt-friday-16-30)))
      (is (not (t/= dt-sunday-17-15 (next-open-dt forex-cal 15 :minutes dt-friday-16-30)))))
    (testing "dt not on trading day"
      (is (t/= dt-sunday-17-00 (next-open-dt forex-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-friday-17-00 (next-open-dt forex-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-17-00 (next-open-dt forex-cal 15 :minutes dt-saturday-12-00))))))

  )
