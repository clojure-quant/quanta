(ns quanta.notebook.calendar-live-time
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [manifold.stream :as s]
   [ta.live.calendar-time :as ct]))

(def s (ct/create-live-calendar-time-generator))

(ct/add-calendar s [:us :m])
(ct/add-calendar s [:crypto :m])
(ct/add-calendar s [:forex :m])
(ct/add-calendar s [:eu :m])

(s/consume
 (fn [msg]
   (info "time event: " msg))
 (ct/get-time-stream s))

(ct/remove-calendar s [:eu :m])

(ct/show-calendars s)