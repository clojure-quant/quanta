(ns ta.calendar.core
  (:require
   [tick.core :as t]
   [ta.calendar.interval :refer [intervals
                                 get-calendar-day-duration
                                 get-calendar-month-duration] :as interval]
   [ta.calendar.calendars :refer [calendars]]
   [ta.calendar.core :as cal]))

(defn now-calendar [calendar-kw]
  (let [calendar (calendar-kw calendars)]
    (interval/now-calendar calendar)))

(defn next-close 
  "dt needs to be calendar-time,
   use current-close to align clock-time to calendar-time"
  [calendar-kw interval-kw dt]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        ;_ (println "calendar: " calendar)
        ;_ (println "interval: " interval)
        next-close-dt (:next-close interval)]
    (next-close-dt calendar dt)))

(defn prior-close 
  "dt needs to be calendar-time,
   use current-close to align clock-time to calendar-time"
  [calendar-kw interval-kw dt]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        _ (assert calendar)
        _ (assert interval)
        ;_ (println "calendar: " calendar)
        ;_ (println "interval: " interval)
        prior-close-dt (:prior-close interval)]
    (prior-close-dt calendar dt)))

(defn current-close 
  "use this function to align clock-time to calendar-time once"
  [calendar-kw interval-kw & [dt]]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        ;_ (println "calendar: " calendar)
        ;_ (println "interval: " interval)
        _ (assert calendar)
        _ (assert interval)
        current-close-dt (:current-close interval)]
    (if dt
      (current-close-dt calendar dt)
      (current-close-dt calendar (t/now)))))

(defn current-close2 [[calendar-kw interval-kw] dt]
  ; 2 reasons for this variation:
  ; 1. calendar argument vector (not two args) this is our new syntax
  ; 2. no optional arg, so in compression nothign goes wrong.
  ; current-close should be replaced by this version.
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        _ (assert calendar)
        _ (assert interval)
        current-close-dt (:current-close interval)
        _ (assert dt "current close dt is nil.")]
    (current-close-dt calendar dt)))

(defn calendar-seq ; todo: [cal interval] instead of 2 parameter
  ([calendar-kw interval-kw]
   (let [cur-dt (current-close calendar-kw interval-kw)]
     (calendar-seq calendar-kw interval-kw cur-dt)))
  ([calendar-kw interval-kw dt]
   (let [cur-dt (current-close calendar-kw interval-kw dt)
         next-dt (partial next-close calendar-kw interval-kw)]
     (iterate next-dt cur-dt))))

(defn calendar-seq-instant [[calendar-kw interval-kw]]
  (->> (calendar-seq calendar-kw interval-kw)
       (map t/instant)))

(defn calendar-seq-prior [calendar-kw interval-kw dt] ; todo: [cal interval] instead of 2 parameter
  (let [cur-dt (current-close calendar-kw interval-kw dt)
        prior-fn (partial prior-close calendar-kw interval-kw)]
    (iterate prior-fn cur-dt)))

(defn trailing-window
  "returns a calendar-seq for a calendar of n rows
   if end-dt specified then last date equals end-date,
   otherwise end-dt is equal to the most-recent close of the calendar"
  ([calendar n end-dt]
   (let [[calendar-kw interval-kw] calendar]
     (take n (calendar-seq-prior calendar-kw interval-kw end-dt))))
  ([calendar n]
   (let [[calendar-kw interval-kw] calendar
         cur-dt (current-close calendar-kw interval-kw)]
     (take n (calendar-seq-prior calendar-kw interval-kw cur-dt)))))

(defn trailing-range
  "returns a calendar-range for a calendar of n rows
   if end-dt specified then last date equals end-date,
   otherwise end-dt is equal to the most-recent close of the calendar"
  ([calendar n end-dt]
   (let [window (trailing-window calendar n end-dt)]
     {:end (first window)
      :start (last window)}))
  ([calendar n]
   (let [window (trailing-window calendar n)]
     {:end (first window)
      :start (last window)})))

(defn fixed-window
  [[calendar-kw interval-kw] {:keys [start end]}]
  (let [seq (calendar-seq-prior calendar-kw interval-kw end)
        after-start? (fn [dt] (t/>= dt start))]
    (take-while after-start? seq)))

(defn calendar-seq->range [cal-seq]
  {:start (last cal-seq)
   :end  (first cal-seq)})

(defn get-bar-window [[calendar-kw interval-kw] bar-end-dt]
   ; TODO: improve
   ; for intraday bars this works fine
   ; for the first bar if the day this is incorrect.
  {:start (prior-close calendar-kw interval-kw bar-end-dt)
   :end bar-end-dt})

(defn get-bar-duration
  "returns duration in seconds of the given calendar"
  [[calendar-kw interval-kw]]
  (case interval-kw
    ; TODO
    ;:Y
    ;:M (get-calendar-month-duration calendar-kw)
    ;:W
    :d (get-calendar-day-duration calendar-kw)
    (get-in intervals [interval-kw :duration])))
