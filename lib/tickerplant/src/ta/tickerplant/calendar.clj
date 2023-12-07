(ns ta.tickerplant.calendar
  (:require
    [taoensso.timbre :as timbre :refer [info warn error]]
    [chime.core :as chime])
   (:import
    [java.time Instant Duration LocalTime ZonedDateTime ZoneId Period]))
  

(defn every-minute []
  (-> (chime/periodic-seq
       (Instant/now)
       (Duration/ofMinutes 1))
      ; excludes *right now*
      rest))

(defn date-seq [calendar]
  (every-minute)
  
  )