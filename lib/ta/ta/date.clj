(ns ta.date
  "reading and writing of CSV files that contain bar-series"
  (:require
   [clojure.data.csv :as csv]
   [clojure.java.io :as io]
   [clojure.string :as str]
   ;[cljc.java-time.core :as t]
   [cljc.java-time.local-date :as ld]
   [cljc.java-time.format.date-time-formatter :refer [iso-date]]
  ; [clj-time.format :as fmt] ;wrapper to joda-date-time
   ;[tick.alpha.api :as t]
   [tick.alpha.api :as t]
   ;[tick.timezone]
   ;[tick.locale-en-us]
   )
  (:import
   [java.time LocalDate LocalTime ZonedDateTime ZoneId]
   java.time.format.DateTimeFormatter))

(def date-fmt (DateTimeFormatter/ofPattern "yyyy-MM-dd"))
(def EST (ZoneId/of "America/New_York"))

(defn parse-date [date]
  (try
     (LocalDate/parse date date-fmt)
    (catch Exception _
      nil)))

(defn dt-now []
  (t/date (t/now)))

(comment
  (parse-date "2021-05-16")
  (type (ld/now))
  (ld/after (ld/now)  (parse-date "2021-05-16"))
  (type (t/now))
  (t/date (t/now))
  (dt-now)
  (t/new-duration 80 :days)
 ; 
  )

