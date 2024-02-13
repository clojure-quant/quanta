(ns ta.calendar.core-test
  (:require [clojure.test :refer :all]
    [tick.core :as t]
    [ta.calendar.calendars :as cal]
    [ta.calendar.core :refer [trailing-window calendar-seq]]
    [ta.calendar.day :refer [next-open next-close
                             prior-open prior-close]]))

; trailing-window / calendar-seq-prior
(defn to-est [dt-str]
  (t/in (t/date-time dt-str) "America/New_York"))

;(def window-10d (trailing-window :us :day 10 (to-est "2024-02-11T08:00:00")))
(def window-10d (trailing-window :us :day 10 (to-est "2024-02-10T18:00:00"))) ; TODO: BUG - takes the current date (even its not a trading day)
(def next-10 (take 10 (calendar-seq :us :m)))

(defn print-seq [s]
  (for [index (range 0 (count s))
        :let [element (nth s index)]]
    (println index ": " element)))
(doall (print-seq next-10))

(deftest trailing-window--test
  (testing "trailing window 20 h"
    ;(is )
    ))

; calendar-seq-instant

; current-, prior-, next-close
