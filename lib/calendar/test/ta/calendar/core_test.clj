(ns ta.calendar.core-test
  (:require [clojure.test :refer :all]
    [tick.core :as t]
    [ta.calendar.calendars :as cal]
    [ta.calendar.core :refer [trailing-window calendar-seq fixed-window]]))

(defn to-est [dt-str]
  (t/in (t/date-time dt-str) "America/New_York"))

(defn print-seq [s]
  (for [i (range 0 (count s))]
    (println i ": " (nth s i))))

(comment
  ;(def window-10d (trailing-window [:us :d] 10 (to-est "2024-02-11T08:00:00")))
  (def window-10d (trailing-window [:us :d] 10 (to-est "2024-02-10T18:00:00")))
  ;(def window-10d (trailing-window [:us :d] 10))

  ;(def window-10 (trailing-window [:us :d] 10 (to-est "2024-02-10T18:00:00")))
  ;(def window-10 (trailing-window [:us :d] 10 (to-est "2024-02-09T16:59:59")))
  (def window-10 (trailing-window [:us :d] 10 (to-est "2024-02-09T17:00:00")))
  ;(def window-10 (trailing-window [:us :d] 10 (to-est "2024-02-09T17:01:00")))
  ;(def window-10 (trailing-window [:us :m15] 10 (to-est "2024-02-09T12:34:56")))
  ;(def window-10 (trailing-window [:us :m15] 10 (to-est "2024-02-09T12:30:00")))
  ;(def window-10 (trailing-window [:us :m] 10 (to-est "2024-02-09T12:34:56")))
  ;(def window-10 (trailing-window [:us :m] 10 (to-est "2024-02-09T12:34:00")))
  (def next-10 (take 10 (calendar-seq :us :m)))

  ;(doall (print-seq window-10d))
  ;(doall (print-seq window-10))
  ;(doall (print-seq next-10))

  ;(doall (print-seq (fixed-window [:us :d ] {:start (t/date-time "2023-01-01T00:00:00")
  ;                                           :end (t/date-time "2023-02-01T00:00:00")})))
)

(let [
      ; min seq
      dt-thursday-17-00 (to-est "2024-02-08T17:00:00")
      dt-thursday-16-59-30 (to-est "2024-02-08T16:59:30")
      dt-thursday-16-59 (to-est "2024-02-08T16:59:00")
      dt-thursday-16-58 (to-est "2024-02-08T16:58:00")
      dt-thursday-16-57 (to-est "2024-02-08T16:57:00")
      dt-thursday-16-56 (to-est "2024-02-08T16:56:00")
      dt-thursday-16-55 (to-est "2024-02-08T16:55:00")
      
      dt-friday-17-00 (to-est "2024-02-09T17:00:00")
      dt-friday-16-59-30 (to-est "2024-02-09T16:59:30")
      dt-friday-16-59 (to-est "2024-02-09T16:59:00")
      dt-friday-16-58 (to-est "2024-02-09T16:58:00")
      dt-friday-16-57 (to-est "2024-02-09T16:57:00")
      dt-friday-16-56 (to-est "2024-02-09T16:56:00")
      dt-friday-16-55 (to-est "2024-02-09T16:55:00")

      ; day seq
      dt-prev-friday-17-00 (to-est "2024-02-02T17:00:00")
      dt-monday-17-00 (to-est "2024-02-05T17:00:00")
      dt-tuesday-17-00 (to-est "2024-02-06T17:00:00")
      dt-wednesday-17-00 (to-est "2024-02-07T17:00:00")
      dt-thursday-17-00 (to-est "2024-02-08T17:00:00")
      dt-friday-17-00 (to-est "2024-02-09T17:00:00")

      ;
      dt-thursday-23-00 (to-est "2024-02-08T23:00:00")

      dt-friday-06-00 (to-est "2024-02-09T06:00:00")
      dt-friday-12-00 (to-est "2024-02-09T12:00:00")
      dt-friday-12-34-56 (to-est "2024-02-09T12:34:56")
      dt-friday-18-00 (to-est "2024-02-09T18:00:00")

      dt-saturday-18-00 (to-est "2024-02-10T18:00:00")

      dt-monday-next-06-00 (to-est "2024-02-12T06:00:00")
      dt-monday-next-09-00 (to-est "2024-02-12T09:00:00")
      dt-monday-next-09-01 (to-est "2024-02-12T09:01:00")
      dt-monday-next-09-01-30 (to-est "2024-02-12T09:01:30")
      dt-monday-next-17-00 (to-est "2024-02-12T17:00:00")
      ]
  (deftest calendar-seq-forwards
    (testing "1 day seq"
      ;(let [dt-friday-12-34-56 (to-est "2024-02-09T12:34:56")
      ;      seq-5-us-d (take 5 (calendar-seq-next :us :d (to-est "2024-02-09T12:34:56")))]
      ;  ())
      )
    (testing "1 min seq"
      ))

  (deftest calendar-seq-backwards
    (testing ".."

      ))

  (deftest trailing-window-us-d
    (testing "dt inside interval"
      (let [window-5-us-d (trailing-window [:us :d] 5 dt-friday-12-34-56)]
        (is (= (nth window-5-us-d 0) dt-thursday-17-00))
        (is (= (nth window-5-us-d 1) dt-wednesday-17-00))
        (is (= (nth window-5-us-d 2) dt-tuesday-17-00))
        (is (= (nth window-5-us-d 3) dt-monday-17-00))
        (is (= (nth window-5-us-d 4) dt-prev-friday-17-00))
        (is (not (= (nth window-5-us-d 0) dt-friday-17-00)))))
    (testing "dt on interval boundary"
      (let [window-5-us-d (trailing-window [:us :d] 5 dt-thursday-17-00)]
        (is (= (nth window-5-us-d 0) dt-thursday-17-00))
        (is (= (nth window-5-us-d 1) dt-wednesday-17-00))
        (is (= (nth window-5-us-d 2) dt-tuesday-17-00))
        (is (= (nth window-5-us-d 3) dt-monday-17-00))
        (is (= (nth window-5-us-d 4) dt-prev-friday-17-00))
        (is (not (= (nth window-5-us-d 0) dt-friday-17-00)))))
    (testing "dt before trading hours"
      (let [window-5-us-d (trailing-window [:us :d] 5 dt-friday-06-00)]
        (is (= (nth window-5-us-d 0) dt-thursday-17-00))
        (is (= (nth window-5-us-d 1) dt-wednesday-17-00))
        (is (= (nth window-5-us-d 2) dt-tuesday-17-00))
        (is (= (nth window-5-us-d 3) dt-monday-17-00))
        (is (= (nth window-5-us-d 4) dt-prev-friday-17-00))
        (is (not (= (nth window-5-us-d 0) dt-friday-17-00)))))
    (testing "dt after trading hours"
      (let [window-5-us-d (trailing-window [:us :d] 5 dt-thursday-23-00)]
        (is (= (nth window-5-us-d 0) dt-thursday-17-00))
        (is (= (nth window-5-us-d 1) dt-wednesday-17-00))
        (is (= (nth window-5-us-d 2) dt-tuesday-17-00))
        (is (= (nth window-5-us-d 3) dt-monday-17-00))
        (is (= (nth window-5-us-d 4) dt-prev-friday-17-00))
        (is (not (= (nth window-5-us-d 0) dt-friday-17-00)))))
    (testing "dt before trading hours (from next week)"
      (let [window-5-us-d (trailing-window [:us :d] 5 dt-monday-next-06-00)]
        (is (= (nth window-5-us-d 0) dt-friday-17-00))
        (is (= (nth window-5-us-d 1) dt-thursday-17-00))
        (is (= (nth window-5-us-d 2) dt-wednesday-17-00))
        (is (= (nth window-5-us-d 3) dt-tuesday-17-00))
        (is (= (nth window-5-us-d 4) dt-monday-17-00))
        (is (not (= (nth window-5-us-d 0) dt-monday-next-17-00)))))
    (testing "dt after trading hours (weekend)"
      (let [window-5-us-d (trailing-window [:us :d] 5 dt-friday-18-00)]
        (is (= (nth window-5-us-d 0) dt-friday-17-00))
        (is (= (nth window-5-us-d 1) dt-thursday-17-00))
        (is (= (nth window-5-us-d 2) dt-wednesday-17-00))
        (is (= (nth window-5-us-d 3) dt-tuesday-17-00))
        (is (= (nth window-5-us-d 4) dt-monday-17-00))
        (is (not (= (nth window-5-us-d 0) dt-monday-next-17-00)))))
    (testing "dt on trading week start"
      (let [window-5-us-d (trailing-window [:us :d] 5 dt-monday-next-09-00)]
        (is (= (nth window-5-us-d 0) dt-friday-17-00))
        (is (= (nth window-5-us-d 1) dt-thursday-17-00))
        (is (= (nth window-5-us-d 2) dt-wednesday-17-00))
        (is (= (nth window-5-us-d 3) dt-tuesday-17-00))
        (is (= (nth window-5-us-d 4) dt-monday-17-00))
        (is (not (= (nth window-5-us-d 0) dt-monday-next-17-00)))))
    (testing "dt on trading week close"
      (let [window-5-us-d (trailing-window [:us :d] 5 dt-friday-17-00)]
        (is (= (nth window-5-us-d 0) dt-friday-17-00))
        (is (= (nth window-5-us-d 1) dt-thursday-17-00))
        (is (= (nth window-5-us-d 2) dt-wednesday-17-00))
        (is (= (nth window-5-us-d 3) dt-tuesday-17-00))
        (is (= (nth window-5-us-d 4) dt-monday-17-00))
        (is (not (= (nth window-5-us-d 0) dt-monday-next-17-00)))))
    (testing "dt not on trading day"
      (let [window-5-us-d (trailing-window [:us :d] 5 dt-saturday-18-00)]
        (is (= (nth window-5-us-d 0) dt-friday-17-00))
        (is (= (nth window-5-us-d 1) dt-thursday-17-00))
        (is (= (nth window-5-us-d 2) dt-wednesday-17-00))
        (is (= (nth window-5-us-d 3) dt-tuesday-17-00))
        (is (= (nth window-5-us-d 4) dt-monday-17-00))
        (is (not (= (nth window-5-us-d 0) dt-monday-next-17-00))))))

  (deftest trailingwindow-us-m
    (testing "dt inside interval"
      (let [window-5-us-m (trailing-window [:us :m] 5 dt-friday-16-59-30)]
        (is (= (nth window-5-us-m 0) dt-friday-16-59))
        (is (= (nth window-5-us-m 1) dt-friday-16-58))
        (is (= (nth window-5-us-m 2) dt-friday-16-57))
        (is (= (nth window-5-us-m 3) dt-friday-16-56))
        (is (= (nth window-5-us-m 4) dt-friday-16-55))
        (is (not (= (nth window-5-us-m 0) dt-friday-17-00)))))
    (testing "dt on interval boundary"
      (let [window-5-us-m (trailing-window [:us :m] 5 dt-friday-16-59)]
        (is (= (nth window-5-us-m 0) dt-friday-16-59))
        (is (= (nth window-5-us-m 1) dt-friday-16-58))
        (is (= (nth window-5-us-m 2) dt-friday-16-57))
        (is (= (nth window-5-us-m 3) dt-friday-16-56))
        (is (= (nth window-5-us-m 4) dt-friday-16-55))
        (is (not (= (nth window-5-us-m 0) dt-friday-17-00)))))
    (testing "dt before trading hours"
      (let [window-5-us-m (trailing-window [:us :m] 5 dt-friday-06-00)]
        (is (= (nth window-5-us-m 0) dt-thursday-17-00))
        (is (= (nth window-5-us-m 1) dt-thursday-16-59))
        (is (= (nth window-5-us-m 2) dt-thursday-16-58))
        (is (= (nth window-5-us-m 3) dt-thursday-16-57))
        (is (= (nth window-5-us-m 4) dt-thursday-16-56))
        (is (not (= (nth window-5-us-m 0) dt-friday-17-00)))))
    (testing "dt after trading hours"
      (let [window-5-us-m (trailing-window [:us :m] 5 dt-thursday-23-00)]
        (is (= (nth window-5-us-m 0) dt-thursday-17-00))
        (is (= (nth window-5-us-m 1) dt-thursday-16-59))
        (is (= (nth window-5-us-m 2) dt-thursday-16-58))
        (is (= (nth window-5-us-m 3) dt-thursday-16-57))
        (is (= (nth window-5-us-m 4) dt-thursday-16-56))
        (is (not (= (nth window-5-us-m 0) dt-friday-17-00)))))
    (testing "dt before trading hours (from next week)"
      (let [window-5-us-m (trailing-window [:us :m] 5 dt-monday-next-06-00)]
        (is (= (nth window-5-us-m 0) dt-friday-17-00))
        (is (= (nth window-5-us-m 1) dt-friday-16-59))
        (is (= (nth window-5-us-m 2) dt-friday-16-58))
        (is (= (nth window-5-us-m 3) dt-friday-16-57))
        (is (= (nth window-5-us-m 4) dt-friday-16-56))
        (is (not (= (nth window-5-us-m 0) dt-monday-next-17-00)))))
    (testing "dt after trading hours (weekend)"
      (let [window-5-us-m (trailing-window [:us :m] 5 dt-friday-18-00)]
        (is (= (nth window-5-us-m 0) dt-friday-17-00))
        (is (= (nth window-5-us-m 1) dt-friday-16-59))
        (is (= (nth window-5-us-m 2) dt-friday-16-58))
        (is (= (nth window-5-us-m 3) dt-friday-16-57))
        (is (= (nth window-5-us-m 4) dt-friday-16-56))
        (is (not (= (nth window-5-us-m 0) dt-monday-next-17-00)))))
    (testing "dt before first interval close and seq over a weekend"
      (let [window-5-us-m (trailing-window [:us :m] 5 dt-monday-next-09-01-30)]
        (is (= (nth window-5-us-m 0) dt-monday-next-09-01))
        (is (= (nth window-5-us-m 1) dt-friday-17-00))
        (is (= (nth window-5-us-m 2) dt-friday-16-59))
        (is (= (nth window-5-us-m 3) dt-friday-16-58))
        (is (= (nth window-5-us-m 4) dt-friday-16-57))
        (is (not (= (nth window-5-us-m 0) dt-monday-next-17-00)))))
    (testing "dt on trading week start"
      (let [window-5-us-m (trailing-window [:us :m] 5 dt-monday-next-09-00)]
        (is (= (nth window-5-us-m 0) dt-friday-17-00))
        (is (= (nth window-5-us-m 1) dt-friday-16-59))
        (is (= (nth window-5-us-m 2) dt-friday-16-58))
        (is (= (nth window-5-us-m 3) dt-friday-16-57))
        (is (= (nth window-5-us-m 4) dt-friday-16-56))
        (is (not (= (nth window-5-us-m 0) dt-monday-next-17-00)))))
    (testing "dt on trading week close"
      (let [window-5-us-m (trailing-window [:us :m] 5 dt-friday-17-00)]
        (is (= (nth window-5-us-m 0) dt-friday-17-00))
        (is (= (nth window-5-us-m 1) dt-friday-16-59))
        (is (= (nth window-5-us-m 2) dt-friday-16-58))
        (is (= (nth window-5-us-m 3) dt-friday-16-57))
        (is (= (nth window-5-us-m 4) dt-friday-16-56))
        (is (not (= (nth window-5-us-m 0) dt-monday-next-17-00)))))
    (testing "dt not on trading day"
      (let [window-5-us-m (trailing-window [:us :m] 5 dt-saturday-18-00)]
        (is (= (nth window-5-us-m 0) dt-friday-17-00))
        (is (= (nth window-5-us-m 1) dt-friday-16-59))
        (is (= (nth window-5-us-m 2) dt-friday-16-58))
        (is (= (nth window-5-us-m 3) dt-friday-16-57))
        (is (= (nth window-5-us-m 4) dt-friday-16-56))
        (is (not (= (nth window-5-us-m 0) dt-monday-next-17-00))))))

  (deftest fixed-window-test-us-d
    (testing "dt inside interval"
      (let [fixed-window-us-d (fixed-window [:us :d] {:start dt-prev-friday-17-00
                                                      :end dt-friday-12-34-56})]
        (is (= (nth fixed-window-us-d 0) dt-thursday-17-00))
        (is (= (nth fixed-window-us-d 1) dt-wednesday-17-00))
        (is (= (nth fixed-window-us-d 2) dt-tuesday-17-00))
        (is (= (nth fixed-window-us-d 3) dt-monday-17-00))
        (is (= (nth fixed-window-us-d 4) dt-prev-friday-17-00))
        (is (not (= (nth fixed-window-us-d 0) dt-friday-17-00))))))

  (deftest seq-range
    (testing "trailing-range"

      )
    (testing "calendar-seq->range"

      )
    (testing "get-bar-window"

      )))



; current-, prior-, next-close
