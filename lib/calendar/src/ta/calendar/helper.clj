(ns ta.calendar.helper
  (:require
    [tick.core :as t]))


(defn day-open? [{:keys [week] :as calendar} dt]
  (let [day (t/day-of-week dt)]
    (contains? week day)))

(defn day-closed? [calendar dt]
  (not (day-open? calendar dt)))

(defn intraday? [{:keys [open close] :as calendar}]
  (t/< open close))

(defn overnight? [{:keys [open close] :as calendar}]
  (t/>= open close))

(defn time-open? [{:keys [open close] :as calendar} dt]
  (let [time (t/time dt)]
    (cond
      (intraday? calendar) (and (t/>= time open)
                                (t/<= time close))
      (overnight? calendar) (let [day-before (t/<< dt (t/new-duration 1 :days))
                                  day-after (t/>> dt (t/new-duration 1 :days))]
                              (or (and (t/<= time close) (day-open? calendar day-before))
                                  (and (t/>= time open) (day-open? calendar day-after)))))))

(defn time-closed? [calendar dt]
  (not (time-open? calendar dt)))

(defn before-trading-hours? [{:keys [open close] :as calendar} dt]
  (let [time (t/time dt)
        day-before (t/<< dt (t/new-duration 1 :days))]
    (cond
      (intraday? calendar) (t/< time open)
      (overnight? calendar) (and (t/< time open)
                                 ; TODO: nur für wochenende????
                                 (day-closed? calendar day-before)))))

(defn after-trading-hours? [{:keys [open close] :as calendar} dt]
  (let [time (t/time dt)
        day-after (t/>> dt (t/new-duration 1 :days))]
    (cond
      (intraday? calendar) (t/> time close)
      (overnight? calendar) (and (t/> time close)
                                 (day-closed? calendar day-after)))))

(defn trading-open-time [{:keys [open timezone] :as calendar} d]
  (-> d
      (t/at open)
      (t/in timezone)))

(defn trading-close-time [{:keys [close timezone] :as calendar} d]
  (-> d
      (t/at close)
      (t/in timezone)))