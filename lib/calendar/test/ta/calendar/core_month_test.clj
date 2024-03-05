(ns ta.calendar.core-month-test
  (:require [clojure.test :refer :all]
            [tick.core :as t]
            [ta.calendar.calendars :as cal]
            [ta.calendar.core :refer [trailing-window calendar-seq fixed-window]]))


(defn to-est [dt-str]
  (t/in (t/date-time dt-str) "America/New_York"))

(let [; month seq
      dt-thursday-02-29--17-00 (to-est "2024-02-29T17:00:00")
      dt-wednesday-01-31--17-00 (to-est "2024-01-31T17:00:00")
      dt-friday-12-29--17-00 (to-est "2023-12-29T17:00:00")
      dt-thursday-11-30--17-00 (to-est "2023-11-30T17:00:00")
      dt-tuesday-10-31--17-00 (to-est "2023-10-31T17:00:00")
      dt-friday-09-29--17-00 (to-est "2023-09-29T17:00:00")


      dt-friday-03-01--12-34-56 (to-est "2024-03-01T12:34:56")
      dt-thursday-02-29--06-00 (to-est "2024-02-29T06:00:00")
      dt-thursday-02-29--18-00 (to-est "2024-02-29T18:00:00")
      ]

  (deftest trailing-window-us-M
    (testing "dt inside interval"
      (let [window-5-us-M (trailing-window [:us :M] 5 dt-friday-03-01--12-34-56)]
        (is (= (nth window-5-us-M 0) dt-thursday-02-29--17-00))
        (is (= (nth window-5-us-M 1) dt-wednesday-01-31--17-00))
        (is (= (nth window-5-us-M 2) dt-friday-12-29--17-00))
        (is (= (nth window-5-us-M 3) dt-thursday-11-30--17-00))
        (is (= (nth window-5-us-M 4) dt-tuesday-10-31--17-00))
        (is (not (= (nth window-5-us-M 0) dt-friday-09-29--17-00)))))
    (testing "dt on interval boundary"
      (let [window-5-us-M (trailing-window [:us :M] 5 dt-thursday-02-29--17-00)]
        (is (= (nth window-5-us-M 0) dt-thursday-02-29--17-00))
        (is (= (nth window-5-us-M 1) dt-wednesday-01-31--17-00))
        (is (= (nth window-5-us-M 2) dt-friday-12-29--17-00))
        (is (= (nth window-5-us-M 3) dt-thursday-11-30--17-00))
        (is (= (nth window-5-us-M 4) dt-tuesday-10-31--17-00))
        (is (not (= (nth window-5-us-M 0) dt-friday-09-29--17-00)))))
    (testing "dt before trading hours"
      (let [window-5-us-M (trailing-window [:us :M] 5 dt-thursday-02-29--06-00)]
        (is (= (nth window-5-us-M 0) dt-wednesday-01-31--17-00))
        (is (= (nth window-5-us-M 1) dt-friday-12-29--17-00))
        (is (= (nth window-5-us-M 2) dt-thursday-11-30--17-00))
        (is (= (nth window-5-us-M 3) dt-tuesday-10-31--17-00))
        (is (= (nth window-5-us-M 4) dt-friday-09-29--17-00))
        (is (not (= (nth window-5-us-M 0) dt-friday-03-01--12-34-56)))))
    (testing "dt after trading hours"
      (let [window-5-us-M (trailing-window [:us :M] 5 dt-thursday-02-29--18-00)]
        (is (= (nth window-5-us-M 0) dt-thursday-02-29--17-00))
        (is (= (nth window-5-us-M 1) dt-wednesday-01-31--17-00))
        (is (= (nth window-5-us-M 2) dt-friday-12-29--17-00))
        (is (= (nth window-5-us-M 3) dt-thursday-11-30--17-00))
        (is (= (nth window-5-us-M 4) dt-tuesday-10-31--17-00))
        (is (not (= (nth window-5-us-M 0) dt-friday-09-29--17-00)))))
    ;(testing "dt before trading hours (from next week)"
    ;  (let [window-5-us-M (trailing-window [:us :M] 5 dt-monday-next-06-00)]
    ;    (is (= (nth window-5-us-M 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-M 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-M 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-M 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-M 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-M 0) dt-monday-next-17-00)))))
    ;(testing "dt after trading hours (weekend)"
    ;  (let [window-5-us-M (trailing-window [:us :M] 5 dt-friday-18-00)]
    ;    (is (= (nth window-5-us-M 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-M 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-M 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-M 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-M 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-M 0) dt-monday-next-17-00)))))
    ;(testing "dt on trading week start"
    ;  (let [window-5-us-M (trailing-window [:us :M] 5 dt-monday-next-09-00)]
    ;    (is (= (nth window-5-us-M 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-M 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-M 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-M 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-M 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-M 0) dt-monday-next-17-00)))))
    ;(testing "dt on trading week close"
    ;  (let [window-5-us-M (trailing-window [:us :M] 5 dt-friday-17-00)]
    ;    (is (= (nth window-5-us-M 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-M 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-M 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-M 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-M 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-M 0) dt-monday-next-17-00)))))
    ;(testing "dt not on trading day"
    ;  (let [window-5-us-M (trailing-window [:us :M] 5 dt-saturday-18-00)]
    ;    (is (= (nth window-5-us-M 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-M 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-M 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-M 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-M 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-M 0) dt-monday-next-17-00)))))
    ))
