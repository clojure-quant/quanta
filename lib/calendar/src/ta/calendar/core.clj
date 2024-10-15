(ns ta.calendar.core
  (:require
   [tick.core :as t]
   [quanta.calendar.core :as cal]
   [ta.calendar.interval :refer [intervals
                                 get-calendar-day-duration
                                 get-calendar-month-duration] :as interval]
   [ta.calendar.calendars :refer [calendars]]))

; TODO:
;   this namespace is a wrapper and should be only used for legacy support
;   use quanta.calendar.core instead

(defn now-calendar [calendar-kw]
  (cal/now-calendar calendar-kw))

(defn next-close
  [calendar-kw interval-kw dt]
  (cal/next-close [calendar-kw interval-kw] dt))

(defn prior-close
  [calendar-kw interval-kw dt]
  (cal/prior-close [calendar-kw interval-kw] dt))

(defn current-close
  [calendar-kw interval-kw & [dt]]
  (cal/current-close [calendar-kw interval-kw] dt))

(defn current-close2 [[calendar-kw interval-kw] dt]
  (cal/current-close [calendar-kw interval-kw] dt))

(defn calendar-seq
  ([calendar-kw interval-kw]
   (cal/calendar-seq [calendar-kw interval-kw]))
  ([calendar-kw interval-kw dt]
   (cal/calendar-seq [calendar-kw interval-kw] dt)))

(defn calendar-seq-instant [[calendar-kw interval-kw]]
  (cal/calendar-seq-instant [calendar-kw interval-kw]))

(defn calendar-seq-prior [[calendar-kw interval-kw] dt]
  (cal/calendar-seq-prior [calendar-kw interval-kw] dt))

(defn trailing-window
  ([calendar n end-dt]
   (cal/trailing-window calendar n end-dt))
  ([calendar n]
   (cal/trailing-window calendar n)))

(defn trailing-range
  ([calendar n end-dt]
   (cal/trailing-range calendar n end-dt))
  ([calendar n]
   (cal/trailing-range calendar n)))

(defn fixed-window
  [[calendar-kw interval-kw] window]
  (cal/fixed-window [calendar-kw interval-kw] window))

(defn calendar-seq->range [cal-seq]
  (cal/calendar-seq->range cal-seq))

(defn get-bar-window [[calendar-kw interval-kw] bar-end-dt]
  (cal/get-bar-window [calendar-kw interval-kw] bar-end-dt))

(defn get-bar-duration
  [[calendar-kw interval-kw]]
  (cal/get-bar-duration [calendar-kw interval-kw]))
