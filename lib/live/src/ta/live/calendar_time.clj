(ns ta.live.calendar-time
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [manifold.stream :as s]
   [chime.core :as chime]
   [ta.calendar.core :refer [calendar-seq-instant]]))

(defn create-live-calendar-time-generator []
  {:time-stream (s/stream) ; same msg-type {:calendar :time} as multi-calendar playback.
   :calendars (atom {})})

(defn get-time-stream [state]
  (:time-stream state))

(defn- log-finished []
  (warn "bar-generator chime Schedule finished!"))

(defn- log-error [ex]
  (error "bar-generator chime exception: " ex)
  true)

(defn add-calendar [state calendar]
  (let [date-seq (calendar-seq-instant calendar)
        time-stream (get-time-stream state)
        closeable (chime/chime-at date-seq
                                  (fn [time]
                                    (s/put! time-stream {:calendar calendar 
                                                         :time time}))
                                  {:on-finished log-finished :error-handler log-error})]
    (swap! (:calendars state) assoc calendar closeable)
    ))

(defn remove-calendar [state calendar]
  (if-let [c @(:calendars state)]
    (c)
    (swap! (:calendars state) dissoc calendar)))

