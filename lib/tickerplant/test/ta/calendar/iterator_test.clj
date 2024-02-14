(ns ta.calendar.iterator-test
  (:require [clojure.test :refer :all]
            [tick.core :as t]
            [ta.calendar.calendars :as cal]
            [ta.calendar.iterator :refer [prev-close-dt]]))


(defn to-est [dt-str]
  (t/in (t/date-time dt-str) "America/New_York"))

(defn to-est-at [date time-str]
  (-> date
      (t/at time-str)
      (t/in "America/New_York")))

(let [
      dt-monday-09-00 (to-est "2024-02-05T09:00:00")
      ;dt-monday12 (to-est "2024-02-05T12:00:00")
      ;dt-monday18 (to-est "2024-02-05T18:00:00")
      ;dt-thursday06 (to-est "2024-02-08T06:00:00")
      ;dt-thursday12 (to-est "2024-02-08T12:00:00")

      dt-thursday-09-00 (to-est "2024-02-08T09:00:00")
      dt-thursday-17-00 (to-est "2024-02-08T17:00:00")
      dt-thursday-23-00 (to-est "2024-02-08T23:00:00")

      dt-friday-00-00 (to-est "2024-02-09T00:00:00")
      dt-friday-06-00 (to-est "2024-02-09T06:00:00")
      dt-friday-09-00 (to-est "2024-02-09T09:00:00")
      dt-friday-12-34 (to-est "2024-02-09T12:34:56")
      dt-friday-12-15 (to-est "2024-02-09T12:15:00")
      dt-friday-12-30 (to-est "2024-02-09T12:30:00")
      dt-friday-12-45 (to-est "2024-02-09T12:45:00")
      dt-friday-17-00 (to-est "2024-02-09T17:00:00")

      dt-saturday-09-00 (to-est "2024-02-10T09:00:00")
      dt-saturday-12-00 (to-est "2024-02-10T12:00:00")
      dt-saturday-17-00 (to-est "2024-02-10T17:00:00")

      ;dt-sunday06 (to-est "2024-02-11T06:00:00")
      ;dt-sunday12 (to-est "2024-02-11T12:00:00")
      ;dt-sunday18 (to-est "2024-02-11T18:00:00")
      us-cal (:us cal/calendars)]
  (deftest prev-close
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
    (testing "dt not on trading day"
      (is (t/= dt-friday-17-00 (prev-close-dt us-cal 15 :minutes dt-saturday-12-00)))
      (is (not (t/= dt-friday-09-00 (prev-close-dt us-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-monday-09-00 (prev-close-dt us-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-09-00 (prev-close-dt us-cal 15 :minutes dt-saturday-12-00))))
      (is (not (t/= dt-saturday-17-00 (prev-close-dt us-cal 15 :minutes dt-saturday-12-00)))))))