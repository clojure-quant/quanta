(ns quanta.notebook.calendar-live-time
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [ta.calendar.core :refer [calendar-seq-instant]]
   [manifold.stream :as s]
   [ta.calendar.generator :as ct]))

(def s (ct/create-live-calendar-time-generator))

(ct/add-calendar s [:us :m])
(ct/add-calendar s [:crypto :m])
(ct/add-calendar s [:crypto :m15])
(ct/add-calendar s [:crypto :m30])
(ct/add-calendar s [:crypto :h])
(ct/add-calendar s [:forex :m])
(ct/add-calendar s [:eu :m])

(s/consume
 (fn [msg]
   (warn "time event: " msg))
 (ct/get-time-stream s))

(ct/remove-calendar s [:eu :m])

(ct/show-calendars s)


