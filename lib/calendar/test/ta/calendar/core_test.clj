(ns ta.calendar.core-test
  (:require [clojure.test :refer :all]
    [tick.core :as t]
    [ta.calendar.calendars :as cal]
    [ta.calendar.core :refer [trailing-window calendar-seq fixed-window]]
    [ta.calendar.day :refer [next-open next-close
                             prior-open prior-close]]))

; trailing-window / calendar-seq-prior
(defn to-est [dt-str]
  (t/in (t/date-time dt-str) "America/New_York"))

;(def window-10d (trailing-window [:us :d] 10 (to-est "2024-02-11T08:00:00")))
;(def window-10d (trailing-window [:us :d] 10 (to-est "2024-02-10T18:00:00"))) ; TODO: BUG - takes the current date (even its not a trading day)
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


(defn print-seq [s]
  (for [i (range 0 (count s))]
    (println i ": " (nth s i))))


;(doall (print-seq window-10d))
;(doall (print-seq window-10))
;(doall (print-seq next-10))

;(doall (print-seq (fixed-window [:us :d ] {:start (t/date-time "2023-01-01T00:00:00")
;                                           :end (t/date-time "2023-02-01T00:00:00")})))



(let [dt-prev-friday-17-00 (to-est "2024-02-02T17:00:00")
      dt-monday-17-00 (to-est "2024-02-05T17:00:00")
      dt-tuesday-17-00 (to-est "2024-02-06T17:00:00")
      dt-wednesday-17-00 (to-est "2024-02-07T17:00:00")
      dt-thursday-17-00 (to-est "2024-02-08T17:00:00")
      dt-friday-17-00 (to-est "2024-02-09T17:00:00")


      dt-thursday-23-00 (to-est "2024-02-08T23:00:00")

      dt-friday-06-00 (to-est "2024-02-09T06:00:00")
      dt-friday-12-34-56 (to-est "2024-02-09T12:34:56")]
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

  (deftest trailing-window-test
    (testing "trailing window 20 h"

      ))

  (deftest fixed-window-test
    (testing "dt inside interval"
      (let [window-5-us-d (trailing-window [:us :d] 5 dt-friday-12-34-56)]
        (is (= (nth window-5-us-d 0) dt-thursday-17-00))
        (is (= (nth window-5-us-d 1) dt-wednesday-17-00))
        (is (= (nth window-5-us-d 2) dt-tuesday-17-00))
        (is (= (nth window-5-us-d 3) dt-monday-17-00))
        (is (= (nth window-5-us-d 4) dt-prev-friday-17-00))))
    (testing "dt on interval boundary"
      (let [window-5-us-d (trailing-window [:us :d] 5 dt-thursday-17-00)]
        (is (= (nth window-5-us-d 0) dt-thursday-17-00))
        (is (= (nth window-5-us-d 1) dt-wednesday-17-00))
        (is (= (nth window-5-us-d 2) dt-tuesday-17-00))
        (is (= (nth window-5-us-d 3) dt-monday-17-00))
        (is (= (nth window-5-us-d 4) dt-prev-friday-17-00))))
    (testing "dt before trading hours"
      (let [window-5-us-d (trailing-window [:us :d] 5 dt-friday-06-00)]
        (is (= (nth window-5-us-d 0) dt-thursday-17-00))
        (is (= (nth window-5-us-d 1) dt-wednesday-17-00))
        (is (= (nth window-5-us-d 2) dt-tuesday-17-00))
        (is (= (nth window-5-us-d 3) dt-monday-17-00))
        (is (= (nth window-5-us-d 4) dt-prev-friday-17-00))))
    (testing "dt after trading hours"
      (let [window-5-us-d (trailing-window [:us :d] 5 dt-thursday-23-00)]
        (is (= (nth window-5-us-d 0) dt-thursday-17-00))
        (is (= (nth window-5-us-d 1) dt-wednesday-17-00))
        (is (= (nth window-5-us-d 2) dt-tuesday-17-00))
        (is (= (nth window-5-us-d 3) dt-monday-17-00))
        (is (= (nth window-5-us-d 4) dt-prev-friday-17-00))))
    )

  (deftest seq-range
    (testing "trailing-range"

      )
    (testing "calendar-seq->range"

      )
    (testing "get-bar-window"

      )))



; current-, prior-, next-close
