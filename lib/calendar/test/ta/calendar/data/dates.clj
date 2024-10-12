(ns ta.calendar.data.dates
  (:require [clojure.test :refer :all]
            [tick.core :as t]))

(defn to-est [dt-str]
  (t/in (t/date-time dt-str) "America/New_York"))

(defn to-utc [dt-str]
  (t/in (t/date-time dt-str) "UTC"))


;; EST

;; UTC
(def utc-thursday-23-55 (to-utc "2024-02-08T23:55:00"))
(def utc-thursday-23-56 (to-utc "2024-02-08T23:56:00"))
(def utc-thursday-23-57 (to-utc "2024-02-08T23:57:00"))
(def utc-thursday-23-58 (to-utc "2024-02-08T23:58:00"))
(def utc-thursday-23-59 (to-utc "2024-02-08T23:59:00"))

;
(def utc-friday-00-00 (to-utc "2024-02-09T00:00:00"))
(def utc-friday-00-01 (to-utc "2024-02-09T00:01:00"))
(def utc-friday-00-02 (to-utc "2024-02-09T00:02:00"))
(def utc-friday-00-03 (to-utc "2024-02-09T00:03:00"))
(def utc-friday-00-04 (to-utc "2024-02-09T00:04:00"))
(def utc-friday-00-05 (to-utc "2024-02-09T00:05:00"))

;
(def utc-friday-16-30 (to-utc "2024-02-09T16:30:00"))
(def utc-friday-16-29 (to-utc "2024-02-09T16:29:00"))
(def utc-friday-16-28 (to-utc "2024-02-09T16:28:00"))
(def utc-friday-16-27 (to-utc "2024-02-09T16:27:00"))
(def utc-friday-16-26 (to-utc "2024-02-09T16:26:00"))
(def utc-friday-16-25 (to-utc "2024-02-09T16:25:00"))


(def utc-monday-23-59-59-999 (to-utc "2024-02-05T23:59:59.999999999"))
(def utc-tuesday-23-59-59-999 (to-utc "2024-02-06T23:59:59.999999999"))
(def utc-wednesday-23-59-59-999 (to-utc "2024-02-07T23:59:59.999999999"))
(def utc-thursday-23-59-59-999 (to-utc "2024-02-08T23:59:59.999999999"))
(def utc-friday-23-59-59-999 (to-utc "2024-02-09T23:59:59.999999999"))
(def utc-saturday-23-59-59-999 (to-utc "2024-02-10T23:59:59.999999999"))
(def utc-sunday-23-59-59-999 (to-utc "2024-02-11T23:59:59.999999999"))
(def utc-monday-next-23-59-59-999 (to-utc "2024-02-12T23:59:59.999999999"))

(def utc-monday-00-00 (to-utc "2024-02-05T00:00:00"))
(def utc-tuesday-00-00 (to-utc "2024-02-06T00:00:00"))
(def utc-wednesday-00-00 (to-utc "2024-02-07T00:00:00"))
(def utc-thursday-00-00 (to-utc "2024-02-08T00:00:00"))
;(def utc-friday-00-00 (to-utc "2024-02-09T00:00:00"))
(def utc-saturday-00-00 (to-utc "2024-02-10T00:00:00"))
(def utc-sunday-00-00 (to-utc "2024-02-11T00:00:00"))
(def utc-monday-next-00-00 (to-utc "2024-02-12T00:00:00"))
(def utc-tuesday-next-00-00 (to-utc "2024-02-13T00:00:00"))

(def utc-monday-12-00 (to-utc "2024-02-05T12:00:00"))
(def utc-monday-next-12-00 (to-utc "2024-02-12T12:00:00"))
(def utc-tuesday-next-12-00 (to-utc "2024-02-13T12:00:00"))