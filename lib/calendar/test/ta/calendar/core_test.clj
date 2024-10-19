(ns ta.calendar.core-test
  (:require [clojure.test :refer :all]
            [tick.core :as t]
            [ta.calendar.data.dates :refer :all]
            [ta.calendar.calendars :as cal]
            [quanta.calendar.core :refer [trailing-window calendar-seq
                                          fixed-window fixed-window-open
                                          close->open-dt open->close-dt]]))
(defn print-seq [s]
  (for [i (range 0 (count s))]
    (println i ": " (nth s i))))

(comment
  ;(def window-10d (trailing-window [:us :d] 10 (to-est "2024-02-11T08:00:00")))
  ;(def window-10d (trailing-window [:us :d] 10 (to-est "2024-02-10T18:00:00")))
  (def window-10d (trailing-window [:forex :d] 10 (to-est "2024-02-10T18:00:00")))
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
  (doall (print-seq (trailing-window [:forex :d] 10 (to-est "2024-02-09T12:34:56"))))

  ;(doall (print-seq (fixed-window [:us :d ] {:start (t/date-time "2023-01-01T00:00:00")
  ;                                           :end (t/date-time "2023-02-01T00:00:00")})))
)

(let [
      ; min seq
      dt-thursday-13-00 (to-est "2024-02-08T13:00:00")
      dt-thursday-12-59-30 (to-est "2024-02-08T12:59:30")
      dt-thursday-12-59 (to-est "2024-02-08T12:59:00")
      dt-thursday-12-58 (to-est "2024-02-08T12:58:00")
      dt-thursday-12-57 (to-est "2024-02-08T12:57:00")
      dt-thursday-12-56 (to-est "2024-02-08T12:56:00")
      dt-thursday-12-55 (to-est "2024-02-08T12:55:00")

      dt-thursday-16-29 (to-est "2024-02-08T16:29:00")
      dt-thursday-16-28 (to-est "2024-02-08T16:28:00")
      dt-thursday-16-27 (to-est "2024-02-08T16:27:00")
      dt-thursday-16-26 (to-est "2024-02-08T16:26:00")
      dt-thursday-16-25 (to-est "2024-02-08T16:25:00")

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

      dt-friday-16-29 (to-est "2024-02-09T16:29:00")
      dt-friday-16-28 (to-est "2024-02-09T16:28:00")
      dt-friday-16-27 (to-est "2024-02-09T16:27:00")
      dt-friday-16-26 (to-est "2024-02-09T16:26:00")
      dt-friday-16-25 (to-est "2024-02-09T16:25:00")

      dt-thursday-22-59 (to-est "2024-02-08T22:59:00")
      dt-thursday-22-58 (to-est "2024-02-08T22:58:00")
      dt-thursday-22-57 (to-est "2024-02-08T22:57:00")
      dt-thursday-22-56 (to-est "2024-02-08T22:56:00")
      dt-thursday-22-55 (to-est "2024-02-08T22:55:00")

      ; misc
      dt-thursday-12-45 (to-est "2024-02-08T12:45:00")
      dt-thursday-12-00 (to-est "2024-02-08T12:00:00")
      dt-thursday-09-00 (to-est "2024-02-08T09:00:00")
      dt-thursday-15-30 (to-est "2024-02-08T15:30:00")
      dt-thursday-16-00 (to-est "2024-02-08T16:00:00")
      dt-thursday-16-45 (to-est "2024-02-08T16:45:00")

      dt-friday-09-01 (to-est "2024-02-09T09:01:00")


      ; day seq
      dt-prev-friday-17-00 (to-est "2024-02-02T17:00:00")
      dt-monday-17-00 (to-est "2024-02-05T17:00:00")
      dt-tuesday-17-00 (to-est "2024-02-06T17:00:00")
      dt-wednesday-17-00 (to-est "2024-02-07T17:00:00")
      dt-thursday-17-00 (to-est "2024-02-08T17:00:00")
      dt-friday-17-00 (to-est "2024-02-09T17:00:00")

      dt-prev-friday-16-30 (to-est "2024-02-02T16:30:00")
      dt-monday-16-30 (to-est "2024-02-05T16:30:00")
      dt-tuesday-16-30 (to-est "2024-02-06T16:30:00")
      dt-wednesday-16-30 (to-est "2024-02-07T16:30:00")
      dt-thursday-16-30 (to-est "2024-02-08T16:30:00")
      dt-friday-16-30 (to-est "2024-02-09T16:30:00")

      ;
      dt-prev-friday-17-00-30 (to-est "2024-02-02T17:00:30")
      dt-thursday-17-01 (to-est "2024-02-08T17:01:00")
      dt-thursday-23-00 (to-est "2024-02-08T23:00:00")

      dt-friday-06-00 (to-est "2024-02-09T06:00:00")
      dt-friday-12-00 (to-est "2024-02-09T12:00:00")
      dt-friday-12-34-56 (to-est "2024-02-09T12:34:56")
      dt-friday-18-00 (to-est "2024-02-09T18:00:00")

      dt-saturday-18-00 (to-est "2024-02-10T18:00:00")

      dt-sunday-16-59 (to-est "2024-02-11T16:59:00")
      dt-sunday-17-00 (to-est "2024-02-11T17:00:00")
      dt-sunday-17-01 (to-est "2024-02-11T17:01:00")
      dt-sunday-17-01-30 (to-est "2024-02-11T17:01:30")

      dt-monday-next-06-00 (to-est "2024-02-12T06:00:00")
      dt-monday-next-09-00 (to-est "2024-02-12T09:00:00")
      dt-monday-next-09-01 (to-est "2024-02-12T09:01:00")
      dt-monday-next-09-01-30 (to-est "2024-02-12T09:01:30")

      dt-monday-next-16-50 (to-est "2024-02-12T16:50:00")
      dt-monday-next-16-30 (to-est "2024-02-12T16:30:00")
      dt-monday-next-17-00 (to-est "2024-02-12T17:00:00")
      ]

  (deftest calendar-seq-forwards-d
    (testing "1 day seq - us"
      (let [seq-5-us-d (take 5 (calendar-seq [:us :d] dt-monday-17-00))]
        (is (= (nth seq-5-us-d 0) dt-monday-17-00))
        (is (= (nth seq-5-us-d 1) dt-tuesday-17-00))
        (is (= (nth seq-5-us-d 2) dt-wednesday-17-00))
        (is (= (nth seq-5-us-d 3) dt-thursday-17-00))
        (is (= (nth seq-5-us-d 4) dt-friday-17-00))
        (is (not (= (nth seq-5-us-d 0) dt-prev-friday-17-00)))))
    (testing "1 day seq - us over a week"
      (let [seq-5-us-d (take 5 (calendar-seq [:us :d]  dt-prev-friday-17-00))]
        (is (= (nth seq-5-us-d 0) dt-prev-friday-17-00))
        (is (= (nth seq-5-us-d 1) dt-monday-17-00))
        (is (= (nth seq-5-us-d 2) dt-tuesday-17-00))
        (is (= (nth seq-5-us-d 3) dt-wednesday-17-00))
        (is (= (nth seq-5-us-d 4) dt-thursday-17-00))
        (is (not (= (nth seq-5-us-d 0) dt-friday-17-00)))))
    (testing "1 day seq - us over a week - inside interval"
      (let [seq-5-us-d (take 7 (calendar-seq [:us :d] dt-prev-friday-17-00-30))]
        (is (= (nth seq-5-us-d 0) dt-prev-friday-17-00))
        (is (= (nth seq-5-us-d 1) dt-monday-17-00))
        (is (= (nth seq-5-us-d 2) dt-tuesday-17-00))
        (is (= (nth seq-5-us-d 3) dt-wednesday-17-00))
        (is (= (nth seq-5-us-d 4) dt-thursday-17-00))
        (is (= (nth seq-5-us-d 5) dt-friday-17-00))
        (is (= (nth seq-5-us-d 6) dt-monday-next-17-00))
        (is (not (= (nth seq-5-us-d 0) dt-friday-17-00)))))
    (testing "1 day seq - forex over a week - inside interval"
      (let [seq-5-us-d (take 7 (calendar-seq [:forex :d] dt-prev-friday-16-30))]
        (is (= (nth seq-5-us-d 0) dt-prev-friday-16-30))
        (is (= (nth seq-5-us-d 1) dt-monday-16-30))
        (is (= (nth seq-5-us-d 2) dt-tuesday-16-30))
        (is (= (nth seq-5-us-d 3) dt-wednesday-16-30))
        (is (= (nth seq-5-us-d 4) dt-thursday-16-30))
        (is (= (nth seq-5-us-d 5) dt-friday-16-30))
        (is (= (nth seq-5-us-d 6) dt-monday-next-16-30))
        (is (not (= (nth seq-5-us-d 0) dt-friday-17-00))))))

  (deftest calendar-seq-forwards-m
   (testing "1 min seq - us"
      (let [seq-5-us-d (take 5 (calendar-seq [:us :m] dt-thursday-12-55))]
        (is (= (nth seq-5-us-d 0) dt-thursday-12-55))
        (is (= (nth seq-5-us-d 1) dt-thursday-12-56))
        (is (= (nth seq-5-us-d 2) dt-thursday-12-57))
        (is (= (nth seq-5-us-d 3) dt-thursday-12-58))
        (is (= (nth seq-5-us-d 4) dt-thursday-12-59))
        (is (not (= (nth seq-5-us-d 0) dt-friday-17-00)))))
    ; TODO: jumps from thursday to sunday / next forex week !!
    ;(testing "1 min seq - forex - jump into next trading day"
    ;  (let [seq-5-us-d (take 5 (calendar-seq :forex :m dt-thursday-16-27))]
    ;    (is (= (nth seq-5-us-d 0) dt-thursday-16-27))
    ;    (is (= (nth seq-5-us-d 1) dt-thursday-16-28))
    ;    (is (= (nth seq-5-us-d 2) dt-thursday-16-29))
    ;    (is (= (nth seq-5-us-d 3) dt-thursday-16-30))
    ;    (is (= (nth seq-5-us-d 4) dt-thursday-17-00))
    ;    (is (not (= (nth seq-5-us-d 0) dt-friday-17-00)))))
   (testing "1 min seq - forex - jump into next trading day"
     (let [seq-5-us-d (take 5 (calendar-seq [:forex :m] dt-thursday-16-27))]
       (is (= (nth seq-5-us-d 0) dt-thursday-16-27))
       (is (= (nth seq-5-us-d 1) dt-thursday-16-28))
       (is (= (nth seq-5-us-d 2) dt-thursday-16-29))
       (is (= (nth seq-5-us-d 3) dt-thursday-16-30))
       (is (= (nth seq-5-us-d 4) dt-thursday-17-01))
       (is (not (= (nth seq-5-us-d 0) dt-friday-17-00)))))
    )
      
  ; us
  
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

  ; forex
  
  (deftest trailing-window-forex-d
    (testing "dt inside interval"
      (let [window-5-forex-d (trailing-window [:forex :d] 5 dt-friday-12-34-56)]
        (is (= (nth window-5-forex-d 0) dt-thursday-16-30))
        (is (= (nth window-5-forex-d 1) dt-wednesday-16-30))
        (is (= (nth window-5-forex-d 2) dt-tuesday-16-30))
        (is (= (nth window-5-forex-d 3) dt-monday-16-30))
        (is (= (nth window-5-forex-d 4) dt-prev-friday-16-30))
        (is (not (= (nth window-5-forex-d 0) dt-thursday-17-00)))))
    (testing "dt on interval boundary"
      (let [window-5-forex-d (trailing-window [:forex :d] 5 dt-thursday-16-30)]
        (is (= (nth window-5-forex-d 0) dt-thursday-16-30))
        (is (= (nth window-5-forex-d 1) dt-wednesday-16-30))
        (is (= (nth window-5-forex-d 2) dt-tuesday-16-30))
        (is (= (nth window-5-forex-d 3) dt-monday-16-30))
        (is (= (nth window-5-forex-d 4) dt-prev-friday-16-30))
        (is (not (= (nth window-5-forex-d 0) dt-friday-17-00)))))
    (testing "dt before trading hours"
      (let [window-5-forex-d (trailing-window [:forex :d] 5 dt-friday-06-00)]
        (is (= (nth window-5-forex-d 0) dt-thursday-16-30))
        (is (= (nth window-5-forex-d 1) dt-wednesday-16-30))
        (is (= (nth window-5-forex-d 2) dt-tuesday-16-30))
        (is (= (nth window-5-forex-d 3) dt-monday-16-30))
        (is (= (nth window-5-forex-d 4) dt-prev-friday-16-30))
        (is (not (= (nth window-5-forex-d 0) dt-friday-17-00)))))
    (testing "dt after trading hours"
      (let [window-5-forex-d (trailing-window [:forex :d] 5 dt-thursday-23-00)]
        (is (= (nth window-5-forex-d 0) dt-thursday-16-30))
        (is (= (nth window-5-forex-d 1) dt-wednesday-16-30))
        (is (= (nth window-5-forex-d 2) dt-tuesday-16-30))
        (is (= (nth window-5-forex-d 3) dt-monday-16-30))
        (is (= (nth window-5-forex-d 4) dt-prev-friday-16-30))
        (is (not (= (nth window-5-forex-d 0) dt-friday-17-00)))))
    (testing "dt before trading hours (from next week)"
      (let [window-5-forex-d (trailing-window [:forex :d] 5 dt-monday-next-06-00)]
        (is (= (nth window-5-forex-d 0) dt-friday-16-30))
        (is (= (nth window-5-forex-d 1) dt-thursday-16-30))
        (is (= (nth window-5-forex-d 2) dt-wednesday-16-30))
        (is (= (nth window-5-forex-d 3) dt-tuesday-16-30))
        (is (= (nth window-5-forex-d 4) dt-monday-16-30))
        (is (not (= (nth window-5-forex-d 0) dt-monday-next-17-00)))))
    (testing "dt after trading hours (weekend)"
      (let [window-5-forex-d (trailing-window [:forex :d] 5 dt-friday-18-00)]
        (is (= (nth window-5-forex-d 0) dt-friday-16-30))
        (is (= (nth window-5-forex-d 1) dt-thursday-16-30))
        (is (= (nth window-5-forex-d 2) dt-wednesday-16-30))
        (is (= (nth window-5-forex-d 3) dt-tuesday-16-30))
        (is (= (nth window-5-forex-d 4) dt-monday-16-30))
        (is (not (= (nth window-5-forex-d 0) dt-monday-next-17-00)))))
    (testing "dt on trading week start"
      (let [window-5-forex-d (trailing-window [:forex :d] 5 dt-sunday-17-00)]
        (is (= (nth window-5-forex-d 0) dt-friday-16-30))
        (is (= (nth window-5-forex-d 1) dt-thursday-16-30))
        (is (= (nth window-5-forex-d 2) dt-wednesday-16-30))
        (is (= (nth window-5-forex-d 3) dt-tuesday-16-30))
        (is (= (nth window-5-forex-d 4) dt-monday-16-30))
        (is (not (= (nth window-5-forex-d 0) dt-monday-next-17-00)))))
    (testing "dt on trading week close"
      (let [window-5-forex-d (trailing-window [:forex :d] 5 dt-friday-16-30)]
        (is (= (nth window-5-forex-d 0) dt-friday-16-30))
        (is (= (nth window-5-forex-d 1) dt-thursday-16-30))
        (is (= (nth window-5-forex-d 2) dt-wednesday-16-30))
        (is (= (nth window-5-forex-d 3) dt-tuesday-16-30))
        (is (= (nth window-5-forex-d 4) dt-monday-16-30))
        (is (not (= (nth window-5-forex-d 0) dt-monday-next-17-00)))))
    (testing "dt not on trading day"
      (let [window-5-forex-d (trailing-window [:forex :d] 5 dt-saturday-18-00)]
        (is (= (nth window-5-forex-d 0) dt-friday-16-30))
        (is (= (nth window-5-forex-d 1) dt-thursday-16-30))
        (is (= (nth window-5-forex-d 2) dt-wednesday-16-30))
        (is (= (nth window-5-forex-d 3) dt-tuesday-16-30))
        (is (= (nth window-5-forex-d 4) dt-monday-16-30))
        (is (not (= (nth window-5-forex-d 0) dt-monday-next-17-00))))))

  (deftest trailingwindow-forex-m
    (testing "dt inside interval"
      (let [window-5-forex-m (trailing-window [:forex :m] 5 dt-thursday-12-59-30)]
        (is (= (nth window-5-forex-m 0) dt-thursday-12-59))
        (is (= (nth window-5-forex-m 1) dt-thursday-12-58))
        (is (= (nth window-5-forex-m 2) dt-thursday-12-57))
        (is (= (nth window-5-forex-m 3) dt-thursday-12-56))
        (is (= (nth window-5-forex-m 4) dt-thursday-12-55))
        (is (not (= (nth window-5-forex-m 0) dt-thursday-16-30)))))
    (testing "dt on interval boundary"
      (let [window-5-forex-m (trailing-window [:forex :m] 5 dt-thursday-12-59)]
        (is (= (nth window-5-forex-m 0) dt-thursday-12-59))
        (is (= (nth window-5-forex-m 1) dt-thursday-12-58))
        (is (= (nth window-5-forex-m 2) dt-thursday-12-57))
        (is (= (nth window-5-forex-m 3) dt-thursday-12-56))
        (is (= (nth window-5-forex-m 4) dt-thursday-12-55))
        (is (not (= (nth window-5-forex-m 0) dt-thursday-16-30)))))
    (testing "dt before trading hours"
      (let [window-5-forex-m (trailing-window [:forex :m] 5 dt-thursday-16-59)]
        (is (= (nth window-5-forex-m 0) dt-thursday-16-30))
        (is (= (nth window-5-forex-m 1) dt-thursday-16-29))
        (is (= (nth window-5-forex-m 2) dt-thursday-16-28))
        (is (= (nth window-5-forex-m 3) dt-thursday-16-27))
        (is (= (nth window-5-forex-m 4) dt-thursday-16-26))
        (is (not (= (nth window-5-forex-m 0) dt-thursday-17-00)))))
    (testing "dt after trading hours"
      (let [window-5-forex-m (trailing-window [:forex :m] 5 dt-thursday-23-00)]
        (is (= (nth window-5-forex-m 0) dt-thursday-23-00))
        (is (= (nth window-5-forex-m 1) dt-thursday-22-59))
        (is (= (nth window-5-forex-m 2) dt-thursday-22-58))
        (is (= (nth window-5-forex-m 3) dt-thursday-22-57))
        (is (= (nth window-5-forex-m 4) dt-thursday-22-56))
        (is (not (= (nth window-5-forex-m 0) dt-thursday-16-30)))))
    (testing "dt before trading hours (from next week)"
      (let [window-5-forex-m (trailing-window [:forex :m] 5 dt-sunday-16-59)]
        (is (= (nth window-5-forex-m 0) dt-friday-16-30))
        (is (= (nth window-5-forex-m 1) dt-friday-16-29))
        (is (= (nth window-5-forex-m 2) dt-friday-16-28))
        (is (= (nth window-5-forex-m 3) dt-friday-16-27))
        (is (= (nth window-5-forex-m 4) dt-friday-16-26))
        (is (not (= (nth window-5-forex-m 0) dt-monday-next-17-00)))))
    (testing "dt after trading hours (weekend)"
      (let [window-5-forex-m (trailing-window [:forex :m] 5 dt-friday-18-00)]
        (is (= (nth window-5-forex-m 0) dt-friday-16-30))
        (is (= (nth window-5-forex-m 1) dt-friday-16-29))
        (is (= (nth window-5-forex-m 2) dt-friday-16-28))
        (is (= (nth window-5-forex-m 3) dt-friday-16-27))
        (is (= (nth window-5-forex-m 4) dt-friday-16-26))
        (is (not (= (nth window-5-forex-m 0) dt-monday-next-17-00)))))
    (testing "dt before first interval close and seq over a weekend"
      (let [window-5-forex-m (trailing-window [:forex :m] 5 dt-sunday-17-01-30)]
        (is (= (nth window-5-forex-m 0) dt-sunday-17-01))
        (is (= (nth window-5-forex-m 1) dt-friday-16-30))
        (is (= (nth window-5-forex-m 2) dt-friday-16-29))
        (is (= (nth window-5-forex-m 3) dt-friday-16-28))
        (is (= (nth window-5-forex-m 4) dt-friday-16-27))
        (is (not (= (nth window-5-forex-m 0) dt-monday-next-17-00)))))
    (testing "dt on trading week start"
      (let [window-5-forex-m (trailing-window [:forex :m] 5 dt-sunday-17-00)]
        (is (= (nth window-5-forex-m 0) dt-friday-16-30))
        (is (= (nth window-5-forex-m 1) dt-friday-16-29))
        (is (= (nth window-5-forex-m 2) dt-friday-16-28))
        (is (= (nth window-5-forex-m 3) dt-friday-16-27))
        (is (= (nth window-5-forex-m 4) dt-friday-16-26))
        (is (not (= (nth window-5-forex-m 0) dt-monday-next-17-00)))))
    (testing "dt on trading week close"
      (let [window-5-forex-m (trailing-window [:forex :m] 5 dt-friday-17-00)]
        (is (= (nth window-5-forex-m 0) dt-friday-16-30))
        (is (= (nth window-5-forex-m 1) dt-friday-16-29))
        (is (= (nth window-5-forex-m 2) dt-friday-16-28))
        (is (= (nth window-5-forex-m 3) dt-friday-16-27))
        (is (= (nth window-5-forex-m 4) dt-friday-16-26))
        (is (not (= (nth window-5-forex-m 0) dt-monday-next-17-00)))))
    (testing "dt not on trading day"
      (let [window-5-forex-m (trailing-window [:forex :m] 5 dt-saturday-18-00)]
        (is (= (nth window-5-forex-m 0) dt-friday-16-30))
        (is (= (nth window-5-forex-m 1) dt-friday-16-29))
        (is (= (nth window-5-forex-m 2) dt-friday-16-28))
        (is (= (nth window-5-forex-m 3) dt-friday-16-27))
        (is (= (nth window-5-forex-m 4) dt-friday-16-26))
        (is (not (= (nth window-5-forex-m 0) dt-monday-next-17-00))))))

  ; fixed window

  (deftest fixed-window-test-us-d
    (testing "end dt inside interval"
      (let [fixed-window-us-d (fixed-window [:us :d] {:start dt-prev-friday-17-00
                                                      :end dt-friday-12-34-56})]
        (is (= (nth fixed-window-us-d 0) dt-thursday-17-00))
        (is (= (nth fixed-window-us-d 1) dt-wednesday-17-00))
        (is (= (nth fixed-window-us-d 2) dt-tuesday-17-00))
        (is (= (nth fixed-window-us-d 3) dt-monday-17-00))
        (is (= (nth fixed-window-us-d 4) dt-prev-friday-17-00))
        (is (not (= (nth fixed-window-us-d 0) dt-friday-17-00)))))
    (testing "end + start dt inside interval"
      (let [fixed-window-us-d (fixed-window [:us :d] {:start dt-prev-friday-17-00-30
                                                      :end dt-friday-12-34-56})]
        (is (= (nth fixed-window-us-d 0) dt-thursday-17-00))
        (is (= (nth fixed-window-us-d 1) dt-wednesday-17-00))
        (is (= (nth fixed-window-us-d 2) dt-tuesday-17-00))
        (is (= (nth fixed-window-us-d 3) dt-monday-17-00))
        (is (= (count fixed-window-us-d) 4))
        (is (not (= (nth fixed-window-us-d 0) dt-friday-17-00)))))
    (testing "end + start dt on interval boundary"
      (let [fixed-window-us-d (fixed-window [:us :d] {:start dt-prev-friday-17-00
                                                      :end dt-friday-12-00})]
        (is (= (nth fixed-window-us-d 0) dt-thursday-17-00))
        (is (= (nth fixed-window-us-d 1) dt-wednesday-17-00))
        (is (= (nth fixed-window-us-d 2) dt-tuesday-17-00))
        (is (= (nth fixed-window-us-d 3) dt-monday-17-00))
        (is (= (nth fixed-window-us-d 4) dt-prev-friday-17-00))
        (is (not (= (nth fixed-window-us-d 0) dt-friday-17-00))))))

  ; TODO
  (deftest seq-range
    (testing "trailing-range"

      )
    (testing "calendar-seq->range"

      )
    (testing "get-bar-window"

      ))

  ;; close->open time
  (deftest close-to-open-time--intraday-us
    (testing "aligned close - inside day"
      ; m
      (is (= dt-thursday-16-28 (close->open-dt [:us :m] dt-thursday-16-29)))
      (is (not (= dt-thursday-16-29 (close->open-dt [:us :m] dt-thursday-16-29))))

      ; m15
      (is (= dt-thursday-12-45 (close->open-dt [:us :m15] dt-thursday-13-00)))
      (is (not (= dt-thursday-13-00 (close->open-dt [:us :m15] dt-thursday-13-00))))

      ; h
      (is (= dt-thursday-12-00 (close->open-dt [:us :h] dt-thursday-13-00)))
      (is (not (= dt-thursday-13-00 (close->open-dt [:us :h] dt-thursday-13-00))))

      ; h4
      (is (= dt-thursday-09-00 (close->open-dt [:us :h4] dt-thursday-13-00)))
      (is (not (= dt-thursday-13-00 (close->open-dt [:us :h4] dt-thursday-13-00))))
      )

    (testing "aligned close - on day close"
      ; m
      (is (= dt-thursday-16-59 (close->open-dt [:us :m] dt-thursday-17-00)))
      (is (not (= dt-thursday-17-00 (close->open-dt [:us :m] dt-thursday-17-00))))

      ; m15
      (is (= dt-thursday-16-45 (close->open-dt [:us :m15] dt-thursday-17-00)))
      (is (not (= dt-thursday-17-00 (close->open-dt [:us :m15] dt-thursday-17-00))))

      ; h
      (is (= dt-thursday-16-00 (close->open-dt [:us :h] dt-thursday-17-00)))
      (is (not (= dt-thursday-17-00 (close->open-dt [:us :h] dt-thursday-17-00))))

      ; h4
      (is (= dt-thursday-13-00 (close->open-dt [:us :h4] dt-thursday-17-00)))
      (is (not (= dt-thursday-17-00 (close->open-dt [:us :h4] dt-thursday-17-00))))

      ;; TODO day iterator does not have next / prior open...
      ;; d
      ;(is (= dt-thursday-09-00 (close-dt->open-dt [:us :d] dt-thursday-17-00)))
      ;(is (not (= dt-thursday-17-00 (close-dt->open-dt [:us :d] dt-thursday-17-00))))
      )

    (testing "not aligned close"
      ; m
      (is (= dt-thursday-16-59 (close->open-dt [:us :m] dt-thursday-16-59-30)))
      (is (not (= dt-thursday-17-00 (close->open-dt [:us :m] dt-thursday-16-59-30))))

      ; m15
      (is (= dt-thursday-16-45 (close->open-dt [:us :m15] dt-thursday-16-59-30)))
      (is (not (= dt-thursday-17-00 (close->open-dt [:us :m15] dt-thursday-16-59-30))))

      ; h
      (is (= dt-thursday-16-00 (close->open-dt [:us :h] dt-thursday-16-59-30)))
      (is (not (= dt-thursday-17-00 (close->open-dt [:us :h] dt-thursday-16-59-30))))

      ; h4
      (is (= dt-thursday-13-00 (close->open-dt [:us :h4] dt-thursday-16-59-30)))
      (is (not (= dt-thursday-17-00 (close->open-dt [:us :h4] dt-thursday-16-59-30))))
      )

    )

  ;; TODO overnight forex
  (deftest close-to-open-time--overnight-forex
    (testing "aligned close"
      ; m
      ;(is (= dt-thursday-16-28 (close->open-dt [:us :m] dt-thursday-16-29)))
      ;(is (not (= dt-thursday-16-29 (close->open-dt [:us :m] dt-thursday-16-29))))

      ; m15
      ;(is (= dt-thursday-12-45 (close->open-dt [:us :m15] dt-thursday-13-00)))
      ;(is (not (= dt-thursday-13-00 (close->open-dt [:us :m15] dt-thursday-13-00))))

      ; h
      ;(is (= dt-thursday-16-00 (close->open-dt [:forex :h] dt-thursday-16-30)))
      ;(is (not (= dt-thursday-15-30 (close->open-dt [:forex :h] dt-thursday-16-30))))

      ; h4
      ;(is (= dt-thursday-09-00 (close->open-dt [:us :h4] dt-thursday-13-00)))
      ;(is (not (= dt-thursday-13-00 (close->open-dt [:us :h4] dt-thursday-13-00))))
      ))

  (deftest open-to-close-time--intraday-us
    (testing "aligned open - inside day"
      ; m
      (is (= dt-thursday-16-29 (open->close-dt [:us :m] dt-thursday-16-28)))
      (is (not (= dt-thursday-16-28 (open->close-dt [:us :m] dt-thursday-16-28))))

      ; m15
      (is (= dt-thursday-13-00 (open->close-dt [:us :m15] dt-thursday-12-45)))
      (is (not (= dt-thursday-12-45 (open->close-dt [:us :m15] dt-thursday-12-45))))

      ; h
      (is (= dt-thursday-13-00 (open->close-dt [:us :h] dt-thursday-12-00)))
      (is (not (= dt-thursday-12-00 (open->close-dt [:us :h] dt-thursday-12-00))))

      ; h4
      (is (= dt-thursday-13-00 (open->close-dt [:us :h4] dt-thursday-09-00)))
      (is (not (= dt-thursday-09-00 (open->close-dt [:us :h4] dt-thursday-09-00)))))

    ; TODO
    ;(testing "aligned open - on day close"
    ;  ; m
    ;  (is (= dt-thursday-16-59 (open->close-dt [:us :m] dt-thursday-17-00)))
    ;  (is (not (= dt-thursday-17-00 (open->close-dt [:us :m] dt-thursday-17-00))))
    ;
    ;  ; m15
    ;  (is (= dt-thursday-16-45 (open->close-dt [:us :m15] dt-thursday-17-00)))
    ;  (is (not (= dt-thursday-17-00 (open->close-dt [:us :m15] dt-thursday-17-00))))
    ;
    ;  ; h
    ;  (is (= dt-thursday-16-00 (open->close-dt [:us :h] dt-thursday-17-00)))
    ;  (is (not (= dt-thursday-17-00 (open->close-dt [:us :h] dt-thursday-17-00))))
    ;
    ;  ; h4
    ;  (is (= dt-thursday-13-00 (open->close-dt [:us :h4] dt-thursday-17-00)))
    ;  (is (not (= dt-thursday-17-00 (open->close-dt [:us :h4] dt-thursday-17-00))))
    ;
    ;  ;; d
    ;  ;(is (= dt-thursday-09-00 (open->close-dt [:us :d] dt-thursday-17-00)))
    ;  ;(is (not (= dt-thursday-17-00 (open->close-dt [:us :d] dt-thursday-17-00))))
    ;  )

    (testing "not aligned open - close on day close"
      ; m
      (is (= dt-thursday-17-00 (open->close-dt [:us :m] dt-thursday-16-59-30)))
      (is (not (= dt-thursday-16-59 (open->close-dt [:us :m] dt-thursday-16-59-30))))
      ;(is (= dt-friday-09-01 (open->close-dt [:us :m] dt-thursday-17-00)))
      ;(is (not (= dt-thursday-17-00 (open->close-dt [:us :m] dt-thursday-17-00))))

      ; m15
      (is (= dt-thursday-17-00 (open->close-dt [:us :m15] dt-thursday-16-59-30)))
      (is (not (= dt-thursday-16-45 (open->close-dt [:us :m15] dt-thursday-16-59-30))))

      ; h
      (is (= dt-thursday-17-00 (open->close-dt [:us :h] dt-thursday-16-59-30)))
      (is (not (= dt-thursday-16-00 (open->close-dt [:us :h] dt-thursday-16-59-30))))

      ; h4
      (is (= dt-thursday-17-00 (open->close-dt [:us :h4] dt-thursday-16-59-30)))
      (is (not (= dt-thursday-13-00 (open->close-dt [:us :h4] dt-thursday-16-59-30))))
      )
    )

  (deftest close-to-open-time--crypto
    (testing "crypto :m | inside trading day - close seq => open seq"
      (let [fixed-window-crypto-m (fixed-window [:crypto :m] {:start utc-friday-16-26
                                                              :end utc-friday-16-30})
            open-dts (map #(close->open-dt [:crypto :m] %) fixed-window-crypto-m)]
        (is (= (nth open-dts 0) utc-friday-16-29))
        (is (= (nth open-dts 1) utc-friday-16-28))
        (is (= (nth open-dts 2) utc-friday-16-27))
        (is (= (nth open-dts 3) utc-friday-16-26))
        (is (= (nth open-dts 4) utc-friday-16-25))
        (is (not (= (nth open-dts 0) utc-friday-16-30)))))

    (testing "crypto :m | on trading day start - open seq => close seq"
      ; manual list here because 00-00 is not a close and cannot be generated with fixed-window func
      (let [fixed-window-crypto-m (fixed-window [:crypto :m] {:start utc-friday-00-01
                                                              :end utc-friday-00-05})
            open-dts (map #(close->open-dt [:crypto :m] %) fixed-window-crypto-m)]
        (is (= (nth open-dts 0) utc-friday-00-04))
        (is (= (nth open-dts 1) utc-friday-00-03))
        (is (= (nth open-dts 2) utc-friday-00-02))
        (is (= (nth open-dts 3) utc-friday-00-01))
        (is (= (nth open-dts 4) utc-friday-00-00))
        (is (not (= (nth open-dts 0) utc-friday-00-05)))))

    (testing "crypto :m | on trading day end - open seq => close seq"
      (let [fixed-window-crypto-m (fixed-window [:crypto :m] {:start utc-thursday-23-56
                                                              :end utc-thursday-23-59-59-999})
            open-dts (map #(close->open-dt [:crypto :m] %) fixed-window-crypto-m)]
        (is (= (nth open-dts 0) utc-thursday-23-59))
        (is (= (nth open-dts 1) utc-thursday-23-58))
        (is (= (nth open-dts 2) utc-thursday-23-57))
        (is (= (nth open-dts 3) utc-thursday-23-56))
        (is (= (nth open-dts 4) utc-thursday-23-55))
        (is (not (= (nth open-dts 0) utc-thursday-23-59-59-999)))))

    (testing "crypto :m | over 2 trading days - open seq => close seq"
      (let [fixed-window-crypto-m (fixed-window [:crypto :m] {:start utc-thursday-23-56
                                                              :end utc-friday-00-05})
            open-dts (map #(close->open-dt [:crypto :m] %) fixed-window-crypto-m)]
        (is (= (nth open-dts 0) utc-friday-00-04))
        (is (= (nth open-dts 1) utc-friday-00-03))
        (is (= (nth open-dts 2) utc-friday-00-02))
        (is (= (nth open-dts 3) utc-friday-00-01))
        (is (= (nth open-dts 4) utc-friday-00-00))
        (is (= (nth open-dts 5) utc-thursday-23-59))
        (is (= (nth open-dts 6) utc-thursday-23-58))
        (is (= (nth open-dts 7) utc-thursday-23-57))
        (is (= (nth open-dts 8) utc-thursday-23-56))
        (is (= (nth open-dts 9) utc-thursday-23-55))
        (is (not (= (nth open-dts 5) utc-thursday-23-59-59-999)))))
    )

  (deftest open-to-close-time--crypto
    (testing "crypto :m | inside trading day - open seq => close seq"
      (let [fixed-window-crypto-m (fixed-window [:crypto :m] {:start utc-friday-16-25
                                                              :end utc-friday-16-29})
            close-dts (map #(open->close-dt [:crypto :m] %) fixed-window-crypto-m)]
        (is (= (nth close-dts 0) utc-friday-16-30))
        (is (= (nth close-dts 1) utc-friday-16-29))
        (is (= (nth close-dts 2) utc-friday-16-28))
        (is (= (nth close-dts 3) utc-friday-16-27))
        (is (= (nth close-dts 4) utc-friday-16-26))
        (is (not (= (nth close-dts 0) utc-friday-16-29)))))

    (testing "crypto :m | on trading day start - open seq => close seq"
      ; manual list here because 00-00 is not a close and cannot be generated with fixed-window func
      (let [fixed-window-crypto-m [utc-friday-00-04
                                   utc-friday-00-03
                                   utc-friday-00-02
                                   utc-friday-00-01
                                   utc-friday-00-00]
            close-dts (map #(open->close-dt [:crypto :m] %) fixed-window-crypto-m)]
        (is (= (nth close-dts 0) utc-friday-00-05))
        (is (= (nth close-dts 1) utc-friday-00-04))
        (is (= (nth close-dts 2) utc-friday-00-03))
        (is (= (nth close-dts 3) utc-friday-00-02))
        (is (= (nth close-dts 4) utc-friday-00-01))
        (is (not (= (nth close-dts 0) utc-friday-00-04)))))

    (testing "crypto :m | on trading day end - open seq => close seq"
      (let [fixed-window-crypto-m (fixed-window [:crypto :m] {:start utc-thursday-23-55
                                                              :end utc-thursday-23-59})
            close-dts (map #(open->close-dt [:crypto :m] %) fixed-window-crypto-m)]
        (is (= (nth close-dts 0) utc-thursday-23-59-59-999))
        (is (= (nth close-dts 1) utc-thursday-23-59))
        (is (= (nth close-dts 2) utc-thursday-23-58))
        (is (= (nth close-dts 3) utc-thursday-23-57))
        (is (= (nth close-dts 4) utc-thursday-23-56))
        (is (not (= (nth close-dts 0) utc-thursday-23-59)))))

    (testing "crypto :m | over 2 trading days - open seq => close seq"
      (let [fixed-window-crypto-m (concat [utc-friday-00-04
                                           utc-friday-00-03
                                           utc-friday-00-02
                                           utc-friday-00-01
                                           utc-friday-00-00]
                                          (fixed-window [:crypto :m] {:start utc-thursday-23-55
                                                                      :end utc-thursday-23-59}))
            close-dts (map #(open->close-dt [:crypto :m] %) fixed-window-crypto-m)]
        (is (= (nth close-dts 0) utc-friday-00-05))
        (is (= (nth close-dts 1) utc-friday-00-04))
        (is (= (nth close-dts 2) utc-friday-00-03))
        (is (= (nth close-dts 3) utc-friday-00-02))
        (is (= (nth close-dts 4) utc-friday-00-01))
        (is (= (nth close-dts 5) utc-thursday-23-59-59-999))
        (is (= (nth close-dts 6) utc-thursday-23-59))
        (is (= (nth close-dts 7) utc-thursday-23-58))
        (is (= (nth close-dts 8) utc-thursday-23-57))
        (is (= (nth close-dts 9) utc-thursday-23-56))
        (is (not (= (nth close-dts 5) utc-friday-00-00)))))
    )

  (deftest fixed-window-open--crypto
    (testing "crypto :m | inside trading day - fixed-window-open"
      (let [fixed-window-crypto-m (fixed-window-open [:crypto :m] {:start utc-friday-16-25
                                                                   :end utc-friday-16-29})]
        (is (= (nth fixed-window-crypto-m 0) utc-friday-16-29))
        (is (= (nth fixed-window-crypto-m 1) utc-friday-16-28))
        (is (= (nth fixed-window-crypto-m 2) utc-friday-16-27))
        (is (= (nth fixed-window-crypto-m 3) utc-friday-16-26))
        (is (= (nth fixed-window-crypto-m 4) utc-friday-16-25))
        (is (not (= (nth fixed-window-crypto-m 0) utc-friday-16-30)))))

    (testing "crypto :m | on trading day start - fixed-window-open"
      (let [fixed-window-crypto-m (fixed-window-open [:crypto :m] {:start utc-friday-00-00
                                                                   :end utc-friday-00-04})]
        (is (= (nth fixed-window-crypto-m 0) utc-friday-00-04))
        (is (= (nth fixed-window-crypto-m 1) utc-friday-00-03))
        (is (= (nth fixed-window-crypto-m 2) utc-friday-00-02))
        (is (= (nth fixed-window-crypto-m 3) utc-friday-00-01))
        (is (= (nth fixed-window-crypto-m 4) utc-friday-00-00))
        (is (not (= (nth fixed-window-crypto-m 0) utc-friday-00-05)))))

    (testing "crypto :m | on trading day end - fixed-window-open"
      (let [fixed-window-crypto-m (fixed-window-open [:crypto :m] {:start utc-thursday-23-56
                                                                   :end utc-friday-00-00})]
        (is (= (nth fixed-window-crypto-m 0) utc-friday-00-00))
        (is (= (nth fixed-window-crypto-m 1) utc-thursday-23-59))
        (is (= (nth fixed-window-crypto-m 2) utc-thursday-23-58))
        (is (= (nth fixed-window-crypto-m 3) utc-thursday-23-57))
        (is (= (nth fixed-window-crypto-m 4) utc-thursday-23-56))
        (is (not (= (nth fixed-window-crypto-m 0) utc-thursday-23-59-59-999)))))

    (testing "crypto :m | over 2 trading days - fixed-window-open"
      (let [fixed-window-crypto-m (fixed-window-open [:crypto :m] {:start utc-thursday-23-56
                                                                   :end utc-friday-00-05})]
        (is (= (nth fixed-window-crypto-m 0) utc-friday-00-05))
        (is (= (nth fixed-window-crypto-m 1) utc-friday-00-04))
        (is (= (nth fixed-window-crypto-m 2) utc-friday-00-03))
        (is (= (nth fixed-window-crypto-m 3) utc-friday-00-02))
        (is (= (nth fixed-window-crypto-m 4) utc-friday-00-01))
        (is (= (nth fixed-window-crypto-m 5) utc-friday-00-00))
        (is (= (nth fixed-window-crypto-m 6) utc-thursday-23-59))
        (is (= (nth fixed-window-crypto-m 7) utc-thursday-23-58))
        (is (= (nth fixed-window-crypto-m 8) utc-thursday-23-57))
        (is (= (nth fixed-window-crypto-m 9) utc-thursday-23-56))
        (is (not (= (nth fixed-window-crypto-m 5) utc-thursday-23-59-59-999)))))
    )
  )
