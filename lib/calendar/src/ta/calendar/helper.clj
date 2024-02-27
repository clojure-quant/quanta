(ns ta.calendar.helper
  (:require
    [tick.core :as t]
    [ta.helper.date :refer [at-time]]))

(def day1 (t/new-duration 1 :days))

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
      (day-closed? calendar dt) false
      (intraday? calendar) (and (t/>= time open)
                                (t/<= time close))
      (overnight? calendar) (let [day-before (t/<< dt (t/new-duration 1 :days))
                                  day-after (t/>> dt (t/new-duration 1 :days))]
                              (or (and (t/<= time close) (day-open? calendar day-before))
                                  (and (t/>= time open) (day-open? calendar day-after)))))))

(defn time-closed? [calendar dt]
  (not (time-open? calendar dt)))

(defn before-trading-hours?
  "default behavoir: checks if dt < calendar open time inside the current trading day
   customization:
   - open and close can be custom values
   - the open time boundary can be included with the include-open? flag"
  ; default
  ([{:keys [open close] :as calendar} dt]
   (before-trading-hours? calendar dt open close false))
  ; include open flag
  ([{:keys [open close] :as calendar} dt include-open?]
   (before-trading-hours? calendar dt open close include-open?))
  ; custom open close
  ([calendar dt open close]
   (before-trading-hours? calendar dt open close false))
  ; base
  ([calendar dt open close include-open?]
    (let [lt (if include-open? t/<= t/<)
          time (t/time dt)]
      (cond
        (day-closed? calendar dt) false             ; no trading day
        ;; |...[... day ...]...|
        (intraday? calendar)  (lt time open)
        ;; |... old day ...]...[... new day ...|    ; with previous and next trading day part
        ;; |...................[... new day ...|    ; no previous trading day part
        (overnight? calendar) (let [day-before (t/<< dt (t/new-duration 1 :days))
                                    day-after (t/>> dt (t/new-duration 1 :days))]
                                (and (lt time open)
                                     (day-open? calendar day-after)
                                     (or (day-closed? calendar day-before)
                                         (t/> time close))))))))

(defn after-trading-hours?
  "default behavoir: checks if dt > calendar close time inside the current trading day
   customization:
   - open and close can be custom values
   - the close time boundary can be included with the include-close? flag"
  ; default
  ([{:keys [open close] :as calendar} dt]
   (after-trading-hours? calendar dt open close false))
  ; include close flag
  ([{:keys [open close] :as calendar} dt include-close?]
   (after-trading-hours? calendar dt open close include-close?))
  ; custom open close
  ([calendar dt open close]
   (after-trading-hours? calendar dt open close false))
  ; base
  ([calendar dt open close include-close?]
    (let [gt (if include-close? t/>= t/>)
          time (t/time dt)]
      (cond
        (day-closed? calendar dt) false             ; no trading day
        ;; |...[... day ...]...|
        (intraday? calendar) (gt time close)
        ;; |... old day ...]...[... new day ...|    ; with previous and next trading day part
        ;; |... old day ...]...................|    ; no next trading day part
        (overnight? calendar) (let [day-before (t/<< dt (t/new-duration 1 :days))
                                    day-after (t/>> dt (t/new-duration 1 :days))]
                                (and (gt time close)
                                     (day-open? calendar day-before)
                                     (or (day-closed? calendar day-after)
                                         (t/< time open))))))))

(defn trading-open-time [{:keys [open timezone] :as calendar} date]
  (at-time date open timezone))

(defn trading-close-time [{:keys [close timezone] :as calendar} date]
  (at-time date close timezone))