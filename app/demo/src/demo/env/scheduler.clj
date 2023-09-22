(ns demo.env.scheduler
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [chime.core :as chime]
   [ta.data.import :refer [import-list]]
   [demo.env.provider :refer [instrument-category->provider]]
   )
  (:import
   [java.time Instant Duration LocalTime ZonedDateTime ZoneId Period]))

; https://github.com/jarohen/chime

(defn local-time-now []
  (-> (LocalTime/now)
      (.adjustInto (ZonedDateTime/now (ZoneId/of "America/New_York")))))

(defn local-time-at [h]
  (-> (LocalTime/of h 0 0)
      (.adjustInto (ZonedDateTime/now (ZoneId/of "America/New_York")))))

(defn local-time-at-future [h]
  (let [now (local-time-now)
        h (local-time-at h)
        r (compare now h)]
    (if (= 1 r)
      (.plusDays h 1)
      h)))

(comment
  (LocalTime/now)
  (LocalTime/of 5 0 0)
  (local-time-now)
  (local-time-at 5)
  (local-time-at-future 5)
  (local-time-at-future 20)
  (compare (local-time-now) (local-time-at 5))
  (compare (local-time-at 3) (local-time-at 5))
  (compare (local-time-at 6) (local-time-at 5))
  ;
  )
(defn daily-at-hour [h]
  (chime/periodic-seq
   (-> (local-time-at-future h) ; only run if hour is in the future.
       .toInstant)
   (Period/ofDays 1)))

(defn every-hour []
  (-> (chime/periodic-seq
       (Instant/now)
       (Duration/ofMinutes 60))
      ; excludes *right now*
      rest))

(defn every-x-minutes [x]
  (-> (chime/periodic-seq
       (Instant/now)
       (Duration/ofMinutes x))
      ; excludes *right now*
      rest))

; stop

(defn stop-chime [c]
  (try
    (.close c)
    (catch Exception ex
      (error "Exception in stopping chime-fn!" ex))))

(defn stop! [chimes]
  (info "stopping chime schedules..")
  (doall (map stop-chime chimes)))

;; helpers

(defn log-finished []
  (warn "Chime Schedule finished!"))

(defn log-error [ex]
  (error "exception running scheduled chime-fn: " ex)
  true)

;; start - production

(defn import-new-data [time]
  (info "importing new data time: " time)
  (import-list instrument-category->provider 
               :all {:frequency "D"} :append))


(defn start! [& args] ; make it compatible with clip
  (info "Starting Chime Scheduler.. ")
  (let [chimes [;(chime/chime-at (every-hour) import-periodic {:on-finished log-finished :error-handler log-error})
                (chime/chime-at (daily-at-hour 20) import-new-data {:on-finished log-finished :error-handler log-error})
                
                ]]
    chimes))

(comment
  (def chimes (start!))
  (stop! chimes)
;
  )
;; start - DEV


(comment

  (import-new-data "test")
  

 ; 
  )
