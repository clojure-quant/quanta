(ns ta.calendar.iterator-test
  (:require [clojure.test :refer :all]
            [tick.core :as t]
            [ta.calendar.calendars :as cal]
            [ta.calendar.iterator :refer [prev-close-dt next-close-dt
                                          prev-open-dt next-open-dt
                                          current-close-dt current-open-dt]]))


(defn to-est [dt-str]
  (t/in (t/date-time dt-str) "America/New_York"))

(defn to-est-at [date time-str]
  (-> date
      (t/at time-str)
      (t/in "America/New_York")))

(let [
      dt-monday-09-00 (to-est "2024-02-05T09:00:00")

      dt-thursday-09-00 (to-est "2024-02-08T09:00:00")
      dt-thursday-16-45 (to-est "2024-02-08T16:45:00")
      dt-thursday-17-00 (to-est "2024-02-08T17:00:00")
      dt-thursday-23-00 (to-est "2024-02-08T23:00:00")

      dt-friday-00-00 (to-est "2024-02-09T00:00:00")
      dt-friday-06-00 (to-est "2024-02-09T06:00:00")
      dt-friday-09-00 (to-est "2024-02-09T09:00:00")
      dt-friday-09-15 (to-est "2024-02-09T09:15:00")
      dt-friday-12-34 (to-est "2024-02-09T12:34:56")
      dt-friday-12-15 (to-est "2024-02-09T12:15:00")
      dt-friday-12-30 (to-est "2024-02-09T12:30:00")
      dt-friday-12-45 (to-est "2024-02-09T12:45:00")
      dt-friday-13-00 (to-est "2024-02-09T13:00:00")
      dt-friday-16-15 (to-est "2024-02-09T16:15:00")
      dt-friday-16-30 (to-est "2024-02-09T16:30:00")
      dt-friday-16-45 (to-est "2024-02-09T16:45:00")
      dt-friday-17-00 (to-est "2024-02-09T17:00:00")
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

  ;
  ; CURRENT
  ;
  (deftest current-close
    (testing "dt inside interval"
      (is (t/= dt-friday-12-45 (current-close-dt us-cal 15 :minutes dt-friday-12-34)))
      (is (not (t/= dt-friday-12-15 (current-close-dt us-cal 15 :minutes dt-friday-12-34))))
      (is (not (t/= dt-friday-12-30 (current-close-dt us-cal 15 :minutes dt-friday-12-34)))))
    (testing "dt on interval boundary"
      (is (t/= dt-friday-12-30 (current-close-dt us-cal 15 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-15 (current-close-dt us-cal 15 :minutes dt-friday-12-30))))
      (is (not (t/= dt-friday-12-45 (current-close-dt us-cal 15 :minutes dt-friday-12-30))))))

  (deftest current-open
    (testing "dt inside interval"
      (is (t/= dt-friday-12-30 (current-open-dt us-cal 15 :minutes dt-friday-12-34)))
      (is (not (t/= dt-friday-12-15 (current-open-dt us-cal 15 :minutes dt-friday-12-34))))
      (is (not (t/= dt-friday-12-45 (current-open-dt us-cal 15 :minutes dt-friday-12-34)))))
    (testing "dt on interval boundary"
      (is (t/= dt-friday-12-30 (current-open-dt us-cal 15 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-15 (current-open-dt us-cal 15 :minutes dt-friday-12-30))))
      (is (not (t/= dt-friday-12-45 (current-open-dt us-cal 15 :minutes dt-friday-12-30))))))

  ;
  ; PREV CLOSE
  ;
  (deftest prev-close-intraday
    (testing "dt inside interval"
      (is (t/= dt-friday-12-30 (prev-close-dt us-cal 15 :minutes dt-friday-12-34)))
      (is (not (t/= dt-friday-12-15 (prev-close-dt us-cal 15 :minutes dt-friday-12-34))))
      (is (not (t/= dt-friday-12-45 (prev-close-dt us-cal 15 :minutes dt-friday-12-34)))))
    (testing "dt on interval boundary"
      (is (t/= dt-friday-12-15 (prev-close-dt us-cal 15 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-30 (prev-close-dt us-cal 15 :minutes dt-friday-12-30))))
      (is (not (t/= dt-friday-12-45 (prev-close-dt us-cal 15 :minutes dt-friday-12-30)))))
    (testing "dt before trading hours"
      (is (t/= dt-thursday-17-00 (prev-close-dt us-cal 15 :minutes dt-friday-06-00)))
      (is (not (t/= dt-thursday-09-00 (prev-close-dt us-cal 15 :minutes dt-friday-06-00))))
      (is (not (t/= dt-friday-00-00 (prev-close-dt us-cal 15 :minutes dt-friday-06-00))))
      (is (not (t/= dt-friday-09-00 (prev-close-dt us-cal 15 :minutes dt-friday-06-00)))))
    (testing "dt after trading hours"
      (is (t/= dt-thursday-17-00 (prev-close-dt us-cal 15 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-thursday-09-00 (prev-close-dt us-cal 15 :minutes dt-thursday-23-00))))
      (is (not (t/= dt-friday-00-00 (prev-close-dt us-cal 15 :minutes dt-thursday-23-00))))
      (is (not (t/= dt-friday-09-00 (prev-close-dt us-cal 15 :minutes dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      (is (t/= dt-friday-17-00 (prev-close-dt us-cal 15 :minutes dt-monday-next-06-00)))
      (is (t/= dt-friday-17-00 (prev-close-dt us-cal 15 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (prev-close-dt us-cal 15 :minutes dt-monday-next-06-00)))))
    (testing "dt after trading hours (weekend)"
      (is (t/= dt-friday-17-00 (prev-close-dt us-cal 15 :minutes dt-friday-18-00)))
      (is (t/= dt-friday-17-00 (prev-close-dt us-cal 15 :minutes dt-friday-17-15)))
      (is (not (t/= dt-friday-16-45 (next-open-dt forex-cal 15 :minutes dt-friday-17-15)))))
    (testing "dt on trading week start"
      (is (t/= dt-friday-17-00 (prev-close-dt us-cal 15 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (prev-close-dt us-cal 15 :minutes dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-friday-16-45 (prev-close-dt us-cal 15 :minutes dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (prev-close-dt us-cal 15 :minutes dt-friday-17-00)))))
    (testing "dt not on trading day"
      (is (t/= dt-friday-17-00 (prev-close-dt us-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-friday-09-00 (prev-close-dt us-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-monday-09-00 (prev-close-dt us-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-09-00 (prev-close-dt us-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-17-00 (prev-close-dt us-cal 15 :minutes dt-saturday-12-00))))))

  (deftest prev-close-overnight
    ; close on 16:30 for forex (custom definition)
    (testing "dt before trading hours - forex/overnight"
      (is (t/= dt-friday-16-30 (prev-close-dt forex-cal 15 :minutes dt-sunday-06-00)))
      (is (not (t/= dt-sunday-17-00 (prev-close-dt forex-cal 15 :minutes dt-sunday-06-00)))))
    (testing "dt after trading hours - forex/overnight"
      (is (t/= dt-friday-16-30 (prev-close-dt forex-cal 15 :minutes dt-friday-18-00)))
      (is (t/= dt-friday-16-30 (prev-close-dt forex-cal 15 :minutes dt-friday-17-15)))
      (is (not (t/= dt-friday-16-15 (next-open-dt forex-cal 15 :minutes dt-friday-17-15)))))
    (testing "dt on trading week start"
      (is (t/= dt-friday-16-30 (prev-close-dt forex-cal 15 :minutes dt-sunday-17-00)))
      (is (not (t/= dt-monday-next-09-00 (prev-close-dt forex-cal 15 :minutes dt-sunday-17-00))))
      (is (not (t/= dt-sunday-17-00 (prev-close-dt forex-cal 15 :minutes dt-sunday-17-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-friday-16-15 (prev-close-dt forex-cal 15 :minutes dt-friday-16-30)))
      (is (not (t/= dt-friday-16-30 (prev-close-dt forex-cal 15 :minutes dt-friday-16-30)))))
    (testing "dt not on trading day"
      (is (t/= dt-friday-16-30 (prev-close-dt forex-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-friday-09-00 (prev-close-dt forex-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-sunday-17-00 (prev-close-dt forex-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-09-00 (prev-close-dt forex-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-17-00 (prev-close-dt forex-cal 15 :minutes dt-saturday-12-00))))))

  ;
  ; NEXT CLOSE
  ;
    (deftest next-close-intraday
      (testing "dt inside interval"
        (is (t/= dt-friday-12-45 (next-close-dt us-cal 15 :minutes dt-friday-12-34)))
        (is (not (t/= dt-friday-12-15 (next-close-dt us-cal 15 :minutes dt-friday-12-34))))
        (is (not (t/= dt-friday-12-30 (next-close-dt us-cal 15 :minutes dt-friday-12-34)))))
      (testing "dt on interval boundary"
        (is (t/= dt-friday-12-45 (next-close-dt us-cal 15 :minutes dt-friday-12-30)))
        (is (not (t/= dt-friday-12-30 (next-close-dt us-cal 15 :minutes dt-friday-12-30))))
        (is (not (t/= dt-friday-13-00 (next-close-dt us-cal 15 :minutes dt-friday-12-30)))))
      (testing "dt before trading hours"
        (is (t/= dt-friday-09-15 (next-close-dt us-cal 15 :minutes dt-friday-06-00)))
        (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 15 :minutes dt-friday-06-00))))
        (is (not (t/= dt-friday-17-00 (next-close-dt us-cal 15 :minutes dt-friday-06-00)))))
      (testing "dt after trading hours"
        (is (t/= dt-friday-09-15 (next-close-dt us-cal 15 :minutes dt-thursday-23-00)))
        (is (not (t/= dt-thursday-09-00 (next-close-dt us-cal 15 :minutes dt-thursday-23-00))))
        (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 15 :minutes dt-thursday-23-00)))))
      (testing "dt before trading hours (from next week)"
        (is (t/= dt-monday-next-09-15 (next-close-dt us-cal 15 :minutes dt-monday-next-06-00)))
        (is (t/= dt-monday-next-09-15 (next-close-dt us-cal 15 :minutes dt-monday-next-09-00)))
        (is (not (t/= dt-friday-17-00 (next-close-dt us-cal 15 :minutes dt-monday-next-06-00)))))
      (testing "dt after trading hours (weekend)"
        (is (t/= dt-monday-next-09-15 (next-close-dt us-cal 15 :minutes dt-friday-18-00)))
        (is (t/= dt-monday-next-09-15 (next-close-dt us-cal 15 :minutes dt-friday-17-15)))
        (is (not (t/= dt-friday-16-45 (next-open-dt forex-cal 15 :minutes dt-friday-17-15)))))
      (testing "dt on trading week start"
        (is (t/= dt-monday-next-09-15 (next-close-dt us-cal 15 :minutes dt-monday-next-09-00)))
        (is (not (t/= dt-monday-next-09-00 (next-close-dt us-cal 15 :minutes dt-monday-next-09-00)))))
      (testing "dt on trading week close"
        (is (t/= dt-monday-next-09-15 (next-close-dt us-cal 15 :minutes dt-friday-17-00)))
        (is (not (t/= dt-friday-17-00 (next-close-dt us-cal 15 :minutes dt-friday-17-00)))))
      (testing "dt not on trading day"
        (is (t/= dt-monday-next-09-15 (next-close-dt us-cal 15 :minutes dt-saturday-12-00)))
        (is (not (t/= dt-friday-09-00 (next-close-dt us-cal 15 :minutes dt-saturday-12-00))))
        (is (not (t/= dt-monday-09-00 (next-close-dt us-cal 15 :minutes dt-saturday-12-00))))
        (is (not (t/= dt-saturday-09-00 (next-close-dt us-cal 15 :minutes dt-saturday-12-00))))
        (is (not (t/= dt-saturday-17-00 (next-close-dt us-cal 15 :minutes dt-saturday-12-00))))))

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
  
  ;
  ; PREV OPEN
  ;
  (deftest prev-open-intraday
    (testing "dt inside interval"
      (is (t/= dt-friday-12-30 (prev-open-dt us-cal 15 :minutes dt-friday-12-34)))
      (is (not (t/= dt-friday-12-15 (prev-open-dt us-cal 15 :minutes dt-friday-12-34))))
      (is (not (t/= dt-friday-12-45 (prev-open-dt us-cal 15 :minutes dt-friday-12-34)))))
    (testing "dt on interval boundary"
      (is (t/= dt-friday-12-15 (prev-open-dt us-cal 15 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-30 (prev-open-dt us-cal 15 :minutes dt-friday-12-30))))
      (is (not (t/= dt-friday-12-45 (prev-open-dt us-cal 15 :minutes dt-friday-12-30)))))
    (testing "dt before trading hours"
      (is (t/= dt-thursday-16-45 (prev-open-dt us-cal 15 :minutes dt-friday-06-00)))
      (is (not (t/= dt-thursday-17-00 (prev-open-dt us-cal 15 :minutes dt-friday-06-00))))
      (is (not (t/= dt-friday-00-00 (prev-open-dt us-cal 15 :minutes dt-friday-06-00))))
      (is (not (t/= dt-friday-09-00 (prev-open-dt us-cal 15 :minutes dt-friday-06-00)))))
    (testing "dt after trading hours"
      (is (t/= dt-thursday-16-45 (prev-open-dt us-cal 15 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-thursday-17-00 (prev-open-dt us-cal 15 :minutes dt-thursday-23-00))))
      (is (not (t/= dt-friday-00-00 (prev-open-dt us-cal 15 :minutes dt-thursday-23-00))))
      (is (not (t/= dt-friday-09-00 (prev-open-dt us-cal 15 :minutes dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      (is (t/= dt-friday-16-45 (prev-open-dt us-cal 15 :minutes dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (prev-open-dt us-cal 15 :minutes dt-monday-next-06-00))))
      (is (not (t/= dt-friday-16-45 (next-open-dt forex-cal 15 :minutes dt-friday-17-15)))))
    (testing "dt after trading hours (weekend)"
      (is (t/= dt-friday-16-45 (prev-open-dt us-cal 15 :minutes dt-friday-18-00)))
      (is (not (t/= dt-friday-17-00 (prev-open-dt us-cal 15 :minutes dt-friday-17-15)))))
    (testing "dt on trading week start"
      (is (t/= dt-friday-16-45 (prev-open-dt us-cal 15 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (prev-open-dt us-cal 15 :minutes dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-friday-16-45 (prev-open-dt us-cal 15 :minutes dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (prev-open-dt us-cal 15 :minutes dt-friday-17-00)))))
    (testing "dt not on trading day"
      (is (t/= dt-friday-16-45 (prev-open-dt us-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-monday-09-00 (prev-open-dt us-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-09-00 (prev-open-dt us-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-17-00 (prev-open-dt us-cal 15 :minutes dt-saturday-12-00))))))

  (deftest prev-open-overnight
    ; close on 16:30 for forex (custom definition)
    (testing "dt before trading hours - forex/overnight"
      (is (t/= dt-friday-16-15 (prev-open-dt forex-cal 15 :minutes dt-sunday-06-00)))
      (is (not (t/= dt-sunday-17-00 (prev-open-dt forex-cal 15 :minutes dt-sunday-06-00)))))
    (testing "dt after trading hours - forex/overnight"
      (is (t/= dt-friday-16-15 (prev-open-dt forex-cal 15 :minutes dt-friday-18-00)))
      (is (t/= dt-friday-16-15 (prev-open-dt forex-cal 15 :minutes dt-friday-17-15)))
      (is (not (t/= dt-friday-16-15 (next-open-dt forex-cal 15 :minutes dt-friday-17-15)))))
    (testing "dt on trading week start"
      (is (t/= dt-friday-16-15 (prev-open-dt forex-cal 15 :minutes dt-sunday-17-00)))
      (is (not (t/= dt-monday-next-09-00 (prev-open-dt forex-cal 15 :minutes dt-sunday-17-00))))
      (is (not (t/= dt-sunday-17-00 (prev-open-dt forex-cal 15 :minutes dt-sunday-17-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-friday-16-15 (prev-open-dt forex-cal 15 :minutes dt-friday-16-30)))
      (is (not (t/= dt-friday-16-30 (prev-open-dt forex-cal 15 :minutes dt-friday-16-30)))))
    (testing "dt not on trading day"
      (is (t/= dt-friday-16-15 (prev-open-dt forex-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-friday-09-00 (prev-open-dt forex-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-sunday-17-00 (prev-open-dt forex-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-09-00 (prev-open-dt forex-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-17-00 (prev-open-dt forex-cal 15 :minutes dt-saturday-12-00))))))

  ;
  ; NEXT OPEN
  ;
  (deftest next-open-intraday
    (testing "dt inside interval"
      (is (t/= dt-friday-12-45 (next-open-dt us-cal 15 :minutes dt-friday-12-34)))
      (is (not (t/= dt-friday-12-15 (next-open-dt us-cal 15 :minutes dt-friday-12-34))))
      (is (not (t/= dt-friday-12-30 (next-open-dt us-cal 15 :minutes dt-friday-12-34)))))
    (testing "dt on interval boundary"
      (is (t/= dt-friday-12-45 (next-open-dt us-cal 15 :minutes dt-friday-12-30)))
      (is (not (t/= dt-friday-12-30 (next-open-dt us-cal 15 :minutes dt-friday-12-30))))
      (is (not (t/= dt-friday-13-00 (next-open-dt us-cal 15 :minutes dt-friday-12-30)))))
    (testing "dt before trading hours"
      (is (t/= dt-friday-09-00 (next-open-dt us-cal 15 :minutes dt-friday-06-00)))
      (is (not (t/= dt-friday-09-15 (next-open-dt us-cal 15 :minutes dt-friday-06-00))))
      (is (not (t/= dt-friday-17-00 (next-open-dt us-cal 15 :minutes dt-friday-06-00)))))
    (testing "dt after trading hours"
      (is (t/= dt-friday-09-00 (next-open-dt us-cal 15 :minutes dt-thursday-23-00)))
      (is (not (t/= dt-thursday-09-00 (next-open-dt us-cal 15 :minutes dt-thursday-23-00))))
      (is (not (t/= dt-friday-09-15 (next-open-dt us-cal 15 :minutes dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 15 :minutes dt-monday-next-06-00)))
      (is (not (t/= dt-friday-17-00 (next-open-dt us-cal 15 :minutes dt-monday-next-06-00)))))
    (testing "dt after trading hours (weekend)"
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 15 :minutes dt-friday-18-00)))
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 15 :minutes dt-friday-17-15)))
      (is (not (t/= dt-friday-16-45 (next-open-dt forex-cal 15 :minutes dt-friday-17-15)))))
    (testing "dt on trading week start"
      (is (t/= dt-monday-next-09-15 (next-open-dt us-cal 15 :minutes dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (next-open-dt us-cal 15 :minutes dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 15 :minutes dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (next-open-dt us-cal 15 :minutes dt-friday-17-00)))))
    (testing "dt not on trading day"
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-friday-09-00 (next-open-dt us-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-monday-09-00 (next-open-dt us-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-09-00 (next-open-dt us-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-17-00 (next-open-dt us-cal 15 :minutes dt-saturday-12-00))))))

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