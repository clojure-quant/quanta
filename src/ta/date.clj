(ns ta.date
  (:require
   [clojure.string :as str]
   ;[cljc.java-time.core :as t]
   [cljc.java-time.local-date :as ld]
   [cljc.java-time.format.date-time-formatter :as fmt :refer [of-pattern iso-date]]
   ;[tick.alpha.api :as t]
   [tick.alpha.api :as t]
   ;[tick.timezone]
   ;[tick.locale-en-us]
   ))
   


; For example, for the method java.time.LocalDate/parse, 
;there is a corresponding function cljc.java-time.local-date/parse


(defn dt-now []
  (t/date (t/now)))

(def date-fmt (of-pattern "yyyy-MM-dd"))


(defn parse-date [date]
  (try
     (ld/parse date date-fmt)
    (catch Exception _
      nil)))



;  (map clj-time.coerce/to-long (get-ts model [:date])))

;(defn zoned-time-to-epoch-milli [zdt]
;  (-> zdt .toInstant .toEpochMilli))

(defn ->epoch [ldt]
  (ld/of-epoch-day ldt))

;(def EST (ZoneId/of "America/New_York"))

(comment
  (parse-date "2021-05-16")
  (type (ld/now))
  (ld/after (ld/now)  (parse-date "2021-05-16"))
  (type (t/now))
  (t/date (t/now))
  (dt-now)

  (-> dt-now ->epoch)

  (t/new-duration 80 :days)
 ; 
  )

