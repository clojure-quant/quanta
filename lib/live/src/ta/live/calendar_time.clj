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
  (if (get @(:calendars state) calendar)
    (error "cannot add calendar: " calendar " - calendar already exists!")
    (let [_ (warn "creating chimes for calendar: " calendar)
          date-seq (calendar-seq-instant calendar)
          time-stream (get-time-stream state)
          closeable (chime/chime-at date-seq
                                    (fn [time]
                                      @(s/put! time-stream {:calendar calendar
                                                            :time time}))
                                    {:on-finished log-finished :error-handler log-error})]
      (swap! (:calendars state) assoc calendar closeable))))

(defn remove-calendar [state calendar]
  (if-let [c (get @(:calendars state) calendar)]
    (do (info "removing calendar: " calendar)
        (swap! (:calendars state) dissoc calendar)
        (info "closing old chime for calendar " calendar " ..")
        (.close c))
    (error "cannot remove calendar: not subscribed: " calendar)))

(defn show-calendars [state]
  (-> @(:calendars state)
      keys))

(comment

  (calendar-seq-instant [:us :d])
  (calendar-seq-instant [:crypto :d])

  (require '[tick.core :as t])

  (defn next-est [calendar]
    (let [dt (->> (calendar-seq-instant calendar)
                  (take 1)
                  first)
             ;; => #inst "2024-02-29T21:30:00.000000000-00:00"
          ]
         ;(t/in dt "UTC")
      (t/in dt "America/New_York")))

  (next-est [:crypto :d])
  (next-est [:forex :d])

  (next-est [:us :m])
  (next-est [:crypto :m])
  (next-est [:forex :m])

  ;
  )