(ns ta.calendar.day-test
  (:require
   [clojure.test :refer :all]
   [tick.core :as t]
   [ta.calendar.core :refer [trailing-window calendar-seq fixed-window
                             close->open-dt open->close-dt]]
   [ta.calendar.data.dates :refer :all]
   [ta.calendar.calendars :as cal]
   [ta.calendar.interval.day :refer [prior-close-dt next-close-dt
                                     prior-open-dt next-open-dt
                                     current-close current-open]]))

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
      dt-wednesday-16-30 (to-est "2024-02-07T16:30:00")
      dt-wednesday-17-00 (to-est "2024-02-07T17:00:00")

      dt-thursday-06-00 (to-est "2024-02-08T06:00:00")
      dt-thursday-09-00 (to-est "2024-02-08T09:00:00")
      dt-thursday-16-30 (to-est "2024-02-08T16:30:00")
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
      dt-sunday-12-00 (to-est "2024-02-11T12:00:00")
      dt-sunday-17-00 (to-est "2024-02-11T17:00:00")
      dt-sunday-17-15 (to-est "2024-02-11T17:15:00")

      dt-monday-next-06-00 (to-est "2024-02-12T06:00:00")
      dt-monday-next-09-00 (to-est "2024-02-12T09:00:00")
      dt-monday-next-09-15 (to-est "2024-02-12T09:15:00")
      dt-monday-next-17-00 (to-est "2024-02-12T17:00:00")

      dt-tuesday-next-09-00 (to-est "2024-02-13T09:00:00")

      us-cal (:us cal/calendars)
      forex-cal (:forex cal/calendars)]

  ; TODO more tests

  ;; CLOSE
  (deftest current-close-daily
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
      (is (t/= dt-friday-17-00 (current-close us-cal dt-sunday-12-00)))
      (is (not (t/= dt-saturday-17-00 (current-close us-cal dt-friday-18-00)))))
    (testing "dt on trading week start"
      (is (t/= dt-friday-17-00 (current-close us-cal dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (current-close us-cal dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-friday-17-00 (current-close us-cal dt-friday-17-00)))
      (is (not (t/= dt-thursday-17-00 (current-close us-cal dt-friday-17-00)))))
    (testing "dt not on trading day"
      (is (t/= dt-friday-17-00 (current-close us-cal dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (current-close us-cal dt-saturday-12-00))))))

  (deftest prior-close-daily
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
      (is (not (t/= dt-saturday-17-00 (prior-close-dt us-cal dt-friday-18-00)))))
    (testing "dt on trading week start"
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-close-dt us-cal dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-thursday-17-00 (prior-close-dt us-cal dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt us-cal dt-friday-17-00)))))
    (testing "dt not on trading day"
      (is (t/= dt-friday-17-00 (prior-close-dt us-cal dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (prior-close-dt us-cal dt-saturday-12-00))))))

  (deftest next-close-daily
    (testing "dt inside interval"
      (is (t/= dt-friday-17-00 (next-close-dt us-cal dt-friday-12-34-56)))
      (is (not (t/= dt-thursday-17-00 (next-close-dt us-cal dt-friday-12-34-56)))))
    (testing "dt on interval boundary"
      (is (t/= dt-friday-17-00 (next-close-dt us-cal dt-thursday-17-00)))
      (is (not (t/= dt-thursday-17-00 (next-close-dt us-cal dt-thursday-17-00)))))
    (testing "dt before trading hours"
      (is (t/= dt-friday-17-00 (next-close-dt us-cal dt-friday-06-00)))
      (is (not (t/= dt-thursday-17-00 (next-close-dt us-cal dt-friday-06-00)))))
    (testing "dt after trading hours"
      (is (t/= dt-friday-17-00 (next-close-dt us-cal dt-thursday-23-00)))
      (is (not (t/= dt-thursday-17-00 (next-close-dt us-cal dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      (is (t/= dt-monday-next-17-00 (next-close-dt us-cal dt-monday-next-06-00)))
      (is (not (t/= dt-friday-17-00 (next-close-dt us-cal dt-monday-next-06-00)))))
    (testing "dt after trading hours (weekend)"
      (is (t/= dt-monday-next-17-00 (next-close-dt us-cal dt-friday-18-00)))
      (is (not (t/= dt-friday-17-00 (next-close-dt us-cal dt-friday-18-00)))))
    (testing "dt on trading week start"
      (is (t/= dt-monday-next-17-00 (next-close-dt us-cal dt-monday-next-09-00)))
      (is (not (t/= dt-friday-17-00 (next-close-dt us-cal dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-monday-next-17-00 (next-close-dt us-cal dt-friday-17-00)))
      (is (not (t/= dt-friday-17-00 (next-close-dt us-cal dt-friday-17-00)))))
    (testing "dt not on trading day"
      (is (t/= dt-monday-next-17-00 (next-close-dt us-cal dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (next-close-dt us-cal dt-saturday-12-00))))))

  ; OPEN
  (deftest current-open-daily
    (testing "dt inside interval"
      (is (t/= dt-friday-09-00 (current-open us-cal dt-friday-12-34-56)))
      (is (not (t/= dt-thursday-09-00 (current-open us-cal dt-friday-12-34-56)))))
    (testing "dt on interval boundary"
      (is (t/= dt-friday-09-00 (current-open us-cal dt-friday-09-00)))
      (is (not (t/= dt-thursday-09-00 (current-open us-cal dt-friday-09-00)))))
    (testing "dt before trading hours"
      (is (t/= dt-thursday-09-00 (current-open us-cal dt-friday-06-00)))
      (is (not (t/= dt-friday-09-00 (current-open us-cal dt-friday-06-00)))))
    (testing "dt after trading hours"
      (is (t/= dt-thursday-09-00 (current-open us-cal dt-thursday-23-00)))
      (is (not (t/= dt-friday-09-00 (current-open us-cal dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      (is (t/= dt-friday-09-00 (current-open us-cal dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (current-open us-cal dt-monday-next-06-00)))))
    (testing "dt after trading hours (weekend)"
      (is (t/= dt-friday-09-00 (current-open us-cal dt-friday-18-00)))
      (is (not (t/= dt-saturday-09-00 (current-open us-cal dt-friday-18-00)))))
    (testing "dt on trading week start"
      (is (t/= dt-monday-next-09-00 (current-open us-cal dt-monday-next-09-00)))
      (is (not (t/= dt-friday-09-00 (current-open us-cal dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-friday-09-00 (current-open us-cal dt-friday-17-00)))
      (is (not (t/= dt-monday-next-09-00 (current-open us-cal dt-friday-17-00)))))
    (testing "dt not on trading day"
      (is (t/= dt-friday-09-00 (current-open us-cal dt-saturday-12-00)))
      (is (not (t/= dt-saturday-09-00 (current-open us-cal dt-saturday-12-00))))))

  (deftest prior-open-daily
    (testing "dt inside interval"
      (is (t/= dt-friday-09-00 (prior-open-dt us-cal dt-friday-12-34-56)))
      (is (not (t/= dt-thursday-09-00 (prior-open-dt us-cal dt-friday-12-34-56)))))
    (testing "dt on interval boundary"
      (is (t/= dt-thursday-09-00 (prior-open-dt us-cal dt-friday-09-00)))
      (is (not (t/= dt-friday-09-00 (prior-open-dt us-cal dt-friday-09-00)))))
    (testing "dt before trading hours"
      (is (t/= dt-thursday-09-00 (prior-open-dt us-cal dt-friday-06-00)))
      (is (not (t/= dt-friday-09-00 (prior-open-dt us-cal dt-friday-06-00)))))
    (testing "dt after trading hours"
      (is (t/= dt-thursday-09-00 (prior-open-dt us-cal dt-thursday-23-00)))
      (is (not (t/= dt-friday-09-00 (prior-open-dt us-cal dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      (is (t/= dt-friday-09-00 (prior-open-dt us-cal dt-monday-next-06-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-open-dt us-cal dt-monday-next-06-00)))))
    (testing "dt after trading hours (weekend)"
      (is (t/= dt-friday-09-00 (prior-open-dt us-cal dt-friday-18-00)))
      (is (not (t/= dt-saturday-09-00 (prior-open-dt us-cal dt-friday-18-00)))))
    (testing "dt on trading week start"
      (is (t/= dt-friday-09-00 (prior-open-dt us-cal dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-open-dt us-cal dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-friday-09-00 (prior-open-dt us-cal dt-friday-17-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-open-dt us-cal dt-friday-17-00)))))
    (testing "dt not on trading day"
      (is (t/= dt-friday-09-00 (prior-open-dt us-cal dt-saturday-12-00)))
      (is (not (t/= dt-saturday-09-00 (prior-open-dt us-cal dt-saturday-12-00))))))

  (deftest next-open-daily
    (testing "dt inside interval"
      (is (t/= dt-friday-09-00 (next-open-dt us-cal dt-thursday-16-45)))
      (is (not (t/= dt-thursday-09-00 (next-open-dt us-cal dt-thursday-16-45)))))
    (testing "dt on interval boundary"
      (is (t/= dt-friday-09-00 (next-open-dt us-cal dt-thursday-09-00)))
      (is (not (t/= dt-thursday-09-00 (next-open-dt us-cal dt-thursday-09-00)))))
    (testing "dt before trading hours"
      (is (t/= dt-friday-09-00 (next-open-dt us-cal dt-friday-06-00)))
      (is (not (t/= dt-thursday-09-00 (next-open-dt us-cal dt-friday-06-00)))))
    (testing "dt after trading hours"
      (is (t/= dt-friday-09-00 (next-open-dt us-cal dt-thursday-23-00)))
      (is (not (t/= dt-thursday-09-00 (next-open-dt us-cal dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal dt-monday-next-06-00)))
      (is (not (t/= dt-friday-09-00 (next-open-dt us-cal dt-monday-next-06-00)))))
    (testing "dt after trading hours (weekend)"
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal dt-friday-18-00)))
      (is (not (t/= dt-saturday-09-00 (next-open-dt us-cal dt-friday-18-00)))))
    (testing "dt on trading week start"
      (is (t/= dt-tuesday-next-09-00 (next-open-dt us-cal dt-monday-next-09-00)))
      (is (not (t/= dt-monday-next-09-00 (next-open-dt us-cal dt-monday-next-09-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal dt-friday-17-00)))
      (is (not (t/= dt-friday-09-00 (next-open-dt us-cal dt-friday-17-00)))))
    (testing "dt not on trading day"
      (is (t/= dt-monday-next-09-00 (next-open-dt us-cal dt-saturday-12-00)))
      (is (not (t/= dt-saturday-09-00 (next-open-dt us-cal dt-saturday-12-00)))))
    )

  ;; FOREX

  (deftest current-close-overnight
    (testing "dt inside interval"
      (is (t/= dt-thursday-16-30 (current-close forex-cal dt-friday-12-34-56)))
      (is (not (t/= dt-friday-16-30 (current-close forex-cal dt-friday-12-34-56)))))
    (testing "dt on interval boundary"
      (is (t/= dt-thursday-16-30 (current-close forex-cal dt-thursday-16-30)))
      (is (not (t/= dt-wednesday-16-30 (current-close forex-cal dt-thursday-16-30)))))
    (testing "dt before trading hours"
      (is (t/= dt-thursday-16-30 (current-close forex-cal dt-friday-06-00)))
      (is (not (t/= dt-friday-17-00 (current-close forex-cal dt-friday-06-00)))))
    (testing "dt after trading hours"
      (is (t/= dt-thursday-16-30 (current-close forex-cal dt-thursday-23-00)))
      (is (not (t/= dt-friday-17-00 (current-close forex-cal dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      (is (t/= dt-friday-16-30 (current-close forex-cal dt-sunday-06-00)))
      (is (not (t/= dt-monday-next-09-00 (current-close forex-cal dt-sunday-06-00)))))
    (testing "dt after trading hours (weekend)"
      (is (t/= dt-friday-16-30 (current-close forex-cal dt-friday-18-00)))
      (is (not (t/= dt-saturday-17-00 (current-close forex-cal dt-friday-18-00)))))
    (testing "dt on trading week start"
      (is (t/= dt-friday-16-30 (current-close forex-cal dt-sunday-17-00)))
      (is (not (t/= dt-monday-next-09-00 (current-close forex-cal dt-sunday-17-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-friday-16-30 (current-close forex-cal dt-friday-16-30)))
      (is (not (t/= dt-thursday-16-30 (current-close forex-cal dt-friday-16-30)))))
    (testing "dt not on trading day"
      (is (t/= dt-friday-16-30 (current-close forex-cal dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (current-close forex-cal dt-saturday-12-00)))))
    (testing "dt inside gap"
      (is (t/= dt-thursday-16-30 (current-close forex-cal dt-thursday-16-45)))
      (is (not (t/= dt-friday-17-00 (current-close forex-cal dt-thursday-16-45))))))

  (deftest prior-close-overnight
    (testing "dt inside interval"
      (is (t/= dt-thursday-16-30 (prior-close-dt forex-cal dt-friday-12-34-56)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt forex-cal dt-friday-12-34-56)))))
    (testing "dt on interval boundary"
      (is (t/= dt-wednesday-16-30 (prior-close-dt forex-cal dt-thursday-16-30)))
      (is (not (t/= dt-thursday-17-00 (prior-close-dt forex-cal dt-thursday-16-30)))))
    (testing "dt before trading hours"
      (is (t/= dt-thursday-16-30 (prior-close-dt forex-cal dt-friday-06-00)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt forex-cal dt-friday-06-00)))))
    (testing "dt after trading hours"
      (is (t/= dt-thursday-16-30 (prior-close-dt forex-cal dt-thursday-23-00)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt forex-cal dt-thursday-23-00)))))
    (testing "dt before trading hours (from next week)"
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal dt-sunday-06-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-close-dt forex-cal dt-sunday-06-00)))))
    (testing "dt after trading hours (weekend)"
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal dt-friday-18-00)))
      (is (not (t/= dt-saturday-17-00 (prior-close-dt forex-cal dt-friday-18-00)))))
    (testing "dt on trading week start"
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal dt-sunday-17-00)))
      (is (not (t/= dt-monday-next-09-00 (prior-close-dt forex-cal dt-sunday-17-00)))))
    (testing "dt on trading week close"
      (is (t/= dt-thursday-16-30 (prior-close-dt forex-cal dt-friday-16-30)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt forex-cal dt-friday-16-30)))))
    (testing "dt not on trading day"
      (is (t/= dt-friday-16-30 (prior-close-dt forex-cal dt-saturday-12-00)))
      (is (not (t/= dt-saturday-17-00 (prior-close-dt forex-cal dt-saturday-12-00)))))
    (testing "dt inside gap"
      (is (t/= dt-thursday-16-30 (prior-close-dt forex-cal dt-thursday-16-45)))
      (is (not (t/= dt-friday-17-00 (prior-close-dt forex-cal dt-thursday-16-45)))))))


;; crypto daily seq test

(deftest close-to-open-time--crypto
  (testing "crypto :d (aligned) - close seq => open seq"
    (let [fixed-window-crypto-d (fixed-window [:crypto :d] {:start utc-monday-23-59-59-999
                                                            :end utc-monday-next-23-59-59-999})
          open-dts (map #(close->open-dt [:crypto :d] %) fixed-window-crypto-d)]
      (is (= (nth open-dts 0) utc-monday-next-00-00))
      (is (= (nth open-dts 1) utc-sunday-00-00))
      (is (= (nth open-dts 2) utc-saturday-00-00))
      (is (= (nth open-dts 3) utc-friday-00-00))
      (is (= (nth open-dts 4) utc-thursday-00-00))
      (is (= (nth open-dts 5) utc-wednesday-00-00))
      (is (= (nth open-dts 6) utc-tuesday-00-00))
      (is (= (nth open-dts 7) utc-monday-00-00))
      (is (not (= (nth open-dts 0) utc-sunday-00-00)))))

  (testing "crypto :d (unaligned) - close seq => open seq"
    (let [fixed-window-crypto-d [utc-tuesday-next-12-00
                                 utc-monday-next-12-00]
          open-dts (map #(close->open-dt [:crypto :d] %) fixed-window-crypto-d)]
      (is (= (nth open-dts 0) utc-monday-next-00-00))
      (is (= (nth open-dts 1) utc-sunday-00-00))
      (is (not (= (nth open-dts 0) utc-tuesday-next-00-00))))))

(deftest open-to-close-time--crypto
  (testing "crypto :d (aligned) - open seq => close seq"
    (let [fixed-window-crypto-d [utc-monday-next-00-00
                                 utc-sunday-00-00
                                 utc-saturday-00-00
                                 utc-friday-00-00
                                 utc-thursday-00-00
                                 utc-wednesday-00-00
                                 utc-tuesday-00-00
                                 utc-monday-00-00]
          close-dts (map #(open->close-dt [:crypto :d] %) fixed-window-crypto-d)]
      (is (= (nth close-dts 0) utc-monday-next-23-59-59-999))
      (is (= (nth close-dts 1) utc-sunday-23-59-59-999))
      (is (= (nth close-dts 2) utc-saturday-23-59-59-999))
      (is (= (nth close-dts 3) utc-friday-23-59-59-999))
      (is (= (nth close-dts 4) utc-thursday-23-59-59-999))
      (is (= (nth close-dts 5) utc-wednesday-23-59-59-999))
      (is (= (nth close-dts 6) utc-tuesday-23-59-59-999))
      (is (= (nth close-dts 7) utc-monday-23-59-59-999))
      (is (not (= (nth close-dts 0) utc-monday-next-00-00)))))

  (testing "crypto :d (unaligned) - open seq => close seq"
    (let [fixed-window-crypto-d [utc-monday-next-12-00
                                 utc-monday-12-00]
          close-dts (map #(open->close-dt [:crypto :d] %) fixed-window-crypto-d)]
      (is (= (nth close-dts 0) utc-monday-next-23-59-59-999))
      (is (= (nth close-dts 1) utc-monday-23-59-59-999))
      (is (not (= (nth close-dts 0) utc-monday-next-00-00))))))