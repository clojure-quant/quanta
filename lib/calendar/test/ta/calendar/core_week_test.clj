(ns ta.calendar.core-week-test
  (:require [clojure.test :refer :all]
            [tick.core :as t]
            [ta.calendar.calendars :as cal]
            [ta.calendar.core :refer [trailing-window calendar-seq fixed-window]]))


(defn to-est [dt-str]
  (t/in (t/date-time dt-str) "America/New_York"))

(let [; week seq
      dt-friday-03-01--17-00 (to-est "2024-03-01T17:00:00")
      dt-friday-02-23--17-00 (to-est "2024-02-23T17:00:00")
      dt-friday-02-16--17-00 (to-est "2024-02-16T17:00:00")
      dt-friday-02-09--17-00 (to-est "2024-02-09T17:00:00")
      dt-friday-02-02--17-00 (to-est "2024-02-02T17:00:00")
      dt-friday-01-26--17-00 (to-est "2024-01-26T17:00:00")


      dt-friday-03-01--06-00 (to-est "2024-03-01T06:00:00")
      dt-friday-03-01--12-34-56 (to-est "2024-03-01T12:34:56")
      dt-friday-03-01--18-00 (to-est "2024-03-01T18:00:00")
      ]

  (deftest trailing-window-us-d
    (testing "dt inside interval"
      (let [window-5-us-W (trailing-window [:us :W] 5 dt-friday-03-01--12-34-56)]
        (is (= (nth window-5-us-W 0) dt-friday-02-23--17-00))
        (is (= (nth window-5-us-W 1) dt-friday-02-16--17-00))
        (is (= (nth window-5-us-W 2) dt-friday-02-09--17-00))
        (is (= (nth window-5-us-W 3) dt-friday-02-02--17-00))
        (is (= (nth window-5-us-W 4) dt-friday-01-26--17-00))
        (is (not (= (nth window-5-us-W 0) dt-friday-03-01--17-00)))))
    (testing "dt on interval boundary"
      (let [window-5-us-W (trailing-window [:us :W] 5 dt-friday-03-01--17-00)]
        (is (= (nth window-5-us-W 0) dt-friday-03-01--17-00))
        (is (= (nth window-5-us-W 1) dt-friday-02-23--17-00))
        (is (= (nth window-5-us-W 2) dt-friday-02-16--17-00))
        (is (= (nth window-5-us-W 3) dt-friday-02-09--17-00))
        (is (= (nth window-5-us-W 4) dt-friday-02-02--17-00))
        (is (not (= (nth window-5-us-W 0) dt-friday-02-23--17-00)))))
    (testing "dt before trading hours"
      (let [window-5-us-W (trailing-window [:us :W] 5 dt-friday-03-01--06-00)]
        (is (= (nth window-5-us-W 0) dt-friday-02-23--17-00))
        (is (= (nth window-5-us-W 1) dt-friday-02-16--17-00))
        (is (= (nth window-5-us-W 2) dt-friday-02-09--17-00))
        (is (= (nth window-5-us-W 3) dt-friday-02-02--17-00))
        (is (= (nth window-5-us-W 4) dt-friday-01-26--17-00))
        (is (not (= (nth window-5-us-W 0) dt-friday-03-01--17-00)))))
    (testing "dt after trading hours"
      (let [window-5-us-W (trailing-window [:us :W] 5 dt-friday-03-01--18-00)]
        (is (= (nth window-5-us-W 0) dt-friday-03-01--17-00))
        (is (= (nth window-5-us-W 1) dt-friday-02-23--17-00))
        (is (= (nth window-5-us-W 2) dt-friday-02-16--17-00))
        (is (= (nth window-5-us-W 3) dt-friday-02-09--17-00))
        (is (= (nth window-5-us-W 4) dt-friday-02-02--17-00))
        (is (not (= (nth window-5-us-W 0) dt-friday-02-23--17-00)))))
    ;(testing "dt before trading hours (from next week)"
    ;  (let [window-5-us-M (trailing-window [:us :W] 5 dt-monday-next-06-00)]
    ;    (is (= (nth window-5-us-M 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-M 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-M 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-M 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-M 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-M 0) dt-monday-next-17-00)))))
    ;(testing "dt after trading hours (weekend)"
    ;  (let [window-5-us-M (trailing-window [:us :W] 5 dt-friday-18-00)]
    ;    (is (= (nth window-5-us-M 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-M 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-M 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-M 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-M 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-M 0) dt-monday-next-17-00)))))
    ;(testing "dt on trading week start"
    ;  (let [window-5-us-M (trailing-window [:us :W] 5 dt-monday-next-09-00)]
    ;    (is (= (nth window-5-us-M 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-M 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-M 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-M 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-M 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-M 0) dt-monday-next-17-00)))))
    ;(testing "dt on trading week close"
    ;  (let [window-5-us-M (trailing-window [:us :W] 5 dt-friday-17-00)]
    ;    (is (= (nth window-5-us-M 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-M 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-M 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-M 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-M 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-M 0) dt-monday-next-17-00)))))
    ;(testing "dt not on trading day"
    ;  (let [window-5-us-M (trailing-window [:us :W] 5 dt-saturday-18-00)]
    ;    (is (= (nth window-5-us-M 0) dt-friday-17-00))
    ;    (is (= (nth window-5-us-M 1) dt-thursday-17-00))
    ;    (is (= (nth window-5-us-M 2) dt-wednesday-17-00))
    ;    (is (= (nth window-5-us-M 3) dt-tuesday-17-00))
    ;    (is (= (nth window-5-us-M 4) dt-monday-17-00))
    ;    (is (not (= (nth window-5-us-M 0) dt-monday-next-17-00)))))
    ))
