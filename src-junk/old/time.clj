(ns junk.time
  (:require
   [clojure.string :as str]
   [clj-time.core :as t]
   [clj-time.format :as fmt]
   [clj-time.periodic :as per]
   [clj-time.coerce :as coerce]
   [clojure.pprint :as p]))

; http://www.rkn.io/2014/02/13/clojure-cookbook-date-ranges/

(->> "1/31/1993 12:00:00 AM"
     (fmt/parse (fmt/formatter "M/d/yyyy H:m:s a"))
      ;(println)
     (t/month))

(t/date-time 1986 10 30)

(t/after? (t/date-time 1986 10) (t/date-time 1986 9))

(t/date-time 1986 10 30)

(fmt/show-formatters)

(fmt/parse (:basic-date-time-no-ms fmt/formatters) "20181020T140412Z")

(t/now)

(.plusDays (t/now) 30)

(.plusYears (t/now) 30)
