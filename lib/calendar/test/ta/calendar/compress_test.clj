;(ns ta.calendar.compress-test
;  (:require
;   [clojure.test :refer :all]
;   [clj-time.core :as t]
;   [ta.data.csv :refer [load-bars-file save-bars-file]]
;   [ta.calendar.compress :refer [compress group-month]]))
;
;;; Note that compress requires datetime and not zoned date.
;;; Therefore we cannot use trateg parser that produces zoned date time.
;
;(def bars  (load-bars-file "test/csv/compress.csv"))
;
;;; Look into the csv file test/compress.csv;
;;; I patched it, so we can check the high/low more visually
;;; November high 99.99   volumes 1 2 3 = 6
;;; December low 11.11
;
;(def expected
;  [{:date (t/date-time 2019 11 30)
;    :open 20.25 :high 99.99 :low 20.01 :close 22.47 :volume 6 :count 18}
;   {:open 22.29 :high 23.22 :low 11.11 :close 22.09 :volume 0 :count 21
;    :date (t/date-time 2019 12 31)}])
;
;(deftest compress-test
;  (is (= expected
;         (take 2 (compress group-month bars)))))
