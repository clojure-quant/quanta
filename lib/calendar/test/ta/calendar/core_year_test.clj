(ns ta.calendar.core-year-test
  (:require [clojure.test :refer :all]
            [tick.core :as t]
            [ta.calendar.calendars :as cal]
            [ta.calendar.core :refer [trailing-window calendar-seq fixed-window]]))

(defn to-est [dt-str]
  (t/in (t/date-time dt-str) "America/New_York"))

(let [; year seq
      dt-friday-2023-12-29--17-00 (to-est "2023-12-29T17:00:00")
      dt-friday-2022-12-30--17-00 (to-est "2022-12-30T17:00:00")
      dt-friday-2021-12-31--17-00 (to-est "2021-12-31T17:00:00")
      dt-thursday-2020-12-31--17-00 (to-est "2020-12-31T17:00:00")
      dt-tuesday-2019-12-31--17-00 (to-est "2019-12-31T17:00:00")
      dt-monday-2018-12-31--17-00 (to-est "2018-12-31T17:00:00")

      dt-monday-2023-12-29--12-34-56 (to-est "2023-12-29T12:34:56")
      dt-friday-2023-12-29--06-00 (to-est "2023-12-29T06:00:00")
      dt-friday-2023-12-29-18-00 (to-est "2023-12-29T18:00:00")
      ]

  (deftest trailing-window-us-Y
    (testing "dt inside interval"
      (let [window-5-us-Y (trailing-window [:us :Y] 5 dt-monday-2023-12-29--12-34-56)]
        (is (= (nth window-5-us-Y 0) dt-friday-2022-12-30--17-00))
        (is (= (nth window-5-us-Y 1) dt-friday-2021-12-31--17-00))
        (is (= (nth window-5-us-Y 2) dt-thursday-2020-12-31--17-00))
        (is (= (nth window-5-us-Y 3) dt-tuesday-2019-12-31--17-00))
        (is (= (nth window-5-us-Y 4) dt-monday-2018-12-31--17-00))
        (is (not (= (nth window-5-us-Y 0) dt-friday-2023-12-29--17-00)))))
    (testing "dt on interval boundary"
      (let [window-5-us-Y (trailing-window [:us :Y] 5 dt-friday-2023-12-29--17-00)]
        (is (= (nth window-5-us-Y 0) dt-friday-2023-12-29--17-00))
        (is (= (nth window-5-us-Y 1) dt-friday-2022-12-30--17-00))
        (is (= (nth window-5-us-Y 2) dt-friday-2021-12-31--17-00))
        (is (= (nth window-5-us-Y 3) dt-thursday-2020-12-31--17-00))
        (is (= (nth window-5-us-Y 4) dt-tuesday-2019-12-31--17-00))
        (is (not (= (nth window-5-us-Y 0) dt-monday-2018-12-31--17-00)))))
    (testing "dt before trading hours"
      (let [window-5-us-Y (trailing-window [:us :Y] 5 dt-friday-2023-12-29--06-00)]
        (is (= (nth window-5-us-Y 0) dt-friday-2022-12-30--17-00))
        (is (= (nth window-5-us-Y 1) dt-friday-2021-12-31--17-00))
        (is (= (nth window-5-us-Y 2) dt-thursday-2020-12-31--17-00))
        (is (= (nth window-5-us-Y 3) dt-tuesday-2019-12-31--17-00))
        (is (= (nth window-5-us-Y 4) dt-monday-2018-12-31--17-00))
        (is (not (= (nth window-5-us-Y 0) dt-friday-2023-12-29--17-00)))))
    (testing "dt after trading hours"
      (let [window-5-us-Y (trailing-window [:us :Y] 5 dt-friday-2023-12-29-18-00)]
        (is (= (nth window-5-us-Y 0) dt-friday-2023-12-29--17-00))
        (is (= (nth window-5-us-Y 1) dt-friday-2022-12-30--17-00))
        (is (= (nth window-5-us-Y 2) dt-friday-2021-12-31--17-00))
        (is (= (nth window-5-us-Y 3) dt-thursday-2020-12-31--17-00))
        (is (= (nth window-5-us-Y 4) dt-tuesday-2019-12-31--17-00))
        (is (not (= (nth window-5-us-Y 0) dt-monday-2018-12-31--17-00)))))
    ;(testing "dt before trading hours (from next week)"
    ;  (let [window-5-us-Y (trailing-window [:us :Y] 5 dt-monday-next-06-00)]
    ;    (is (= (nth window-5-us-Y 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-Y 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-Y 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-Y 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-Y 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-Y 0) dt-monday-next-17-00)))))
    ;(testing "dt after trading hours (weekend)"
    ;  (let [window-5-us-Y (trailing-window [:us :Y] 5 dt-friday-18-00)]
    ;    (is (= (nth window-5-us-Y 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-Y 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-Y 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-Y 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-Y 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-Y 0) dt-monday-next-17-00)))))
    ;(testing "dt on trading week start"
    ;  (let [window-5-us-Y (trailing-window [:us :Y] 5 dt-monday-next-09-00)]
    ;    (is (= (nth window-5-us-Y 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-Y 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-Y 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-Y 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-Y 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-Y 0) dt-monday-next-17-00)))))
    ;(testing "dt on trading week close"
    ;  (let [window-5-us-Y (trailing-window [:us :Y] 5 dt-friday-17-00)]
    ;    (is (= (nth window-5-us-Y 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-Y 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-Y 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-Y 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-Y 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-Y 0) dt-monday-next-17-00)))))
    ;(testing "dt not on trading day"
    ;  (let [window-5-us-Y (trailing-window [:us :Y] 5 dt-saturday-18-00)]
    ;    (is (= (nth window-5-us-Y 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-Y 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-Y 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-Y 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-Y 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-Y 0) dt-monday-next-17-00)))))
    ))

