(ns ta.calendar.day
  (:require
   [tick.core :as t]))

; week 

(defn day-open? [{:keys [week] :as calendar} dt]
  (let [day (t/day-of-week dt)]
    (contains? week day)))

(defn day-closed? [calendar dt]
  (not (day-open? calendar dt)))

; helper fns

(def day1 (t/new-duration 1 :days))

(defn- at-time [dt time timezone]
  (-> (t/at dt time)
      (t/in timezone)))

; next

(defn next-day [{:keys [open timezone] :as calendar} dt]
  (let [dt-next (t/>> dt day1)
        day-next (t/date dt-next)]
    (if (day-closed? calendar day-next)
      (next-day calendar dt-next)
      (at-time day-next open timezone)
      )))

(defn next-open [{:keys [open timezone] :as calendar} dt]
  (let [next-dt (next-day calendar dt)
        next-day (t/date next-dt)]
    (at-time next-day open timezone)))

(defn next-close [{:keys [close timezone] :as calendar} dt]
  (let [next-dt (next-day calendar dt)
        next-day (t/date next-dt)]
    (at-time next-day close timezone)))


; prior

(defn prior-day [{:keys [open timezone] :as calendar} dt]
  (let [dt-prior (t/<< dt day1)
        day-prior (t/date dt-prior)]
    (if (day-closed? calendar day-prior)
      (prior-day calendar dt-prior)
      (at-time day-prior open timezone))))

(defn prior-open [{:keys [open timezone] :as calendar} dt]
  (let [prior-dt (prior-day calendar dt)
        prior-day (t/date prior-dt)]
    (at-time prior-day open timezone)))

(defn prior-close [{:keys [close timezone] :as calendar} dt]
  (let [prior-dt (prior-day calendar dt)
        prior-day (t/date prior-dt)]
    (at-time prior-day close timezone)))

(comment 
  (require '[ta.calendar.calendars :refer [calendars]])
  (def us (:us calendars))
  us

  (require '[ta.calendar.interval :refer [now-calendar]])
  (now-calendar us)

  (next-day us (now-calendar us))
  (next-open us (now-calendar us))
  (next-close us (now-calendar us))

  (prior-day us (now-calendar us))
  (prior-open us (now-calendar us))
  (prior-close us (now-calendar us))
  
  
  ;
  )