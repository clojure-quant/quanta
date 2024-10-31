(ns quanta.calendar.core
  (:require
   [tick.core :as t]
   [ta.calendar.interval :refer [intervals
                                 get-calendar-day-duration
                                 get-calendar-month-duration] :as interval]
   [ta.calendar.calendars :refer [calendars]]
   [ta.calendar.helper :as helper]))

; to find a calendar we use one vector param: [:forex :d]

; the ns should have 

; in quanta.calendar.db there should be
; one function to load a calendar

; and then in quanta.calendar.core we only use a loaded calendar.
; this avoids many error checkings, I guess.

;; all function should have docstrings and be tested.

(defn now-calendar
  "returns a zoned-date-time of now with the timezone of the calendar"
  [calendar-kw]
  (let [calendar (calendar-kw calendars)]
    (helper/now-calendar calendar)))

(defn next-close
  "returns the close time of the current bar (next bar if the market is closed).
   if dt is an aligned bar close time, then a new bar has started. the close time of the new bar will be returned.
   dt has to be in the calendar timezone.
   dt can be unaligned close time."
  [[calendar-kw interval-kw] dt]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        next-close-dt (:next-close interval)]
    (next-close-dt calendar dt)))

(defn prior-close
  "returns the close time of the recently closed bar.
   if dt is an aligned bar close time, then the close time of the prior bar will be returned.
   dt has to be in the calendar timezone.
   dt can be unaligned close time."
  [[calendar-kw interval-kw] dt]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        _ (assert calendar)
        _ (assert interval)
        prior-close-dt (:prior-close interval)]
    (prior-close-dt calendar dt)))

(defn current-close
  "returns the close time of the recently closed bar.
   if dt is an aligned bar close time, then this close time will be returned.
   dt has to be in the calendar timezone.
   dt can be unaligned close time."
  [[calendar-kw interval-kw] dt]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        _ (assert calendar)
        _ (assert interval)
        _ (assert dt "current close dt is nil.")
        current-close-dt (:current-close interval)]
    (current-close-dt calendar dt)))

(defn next-open
  "returns the open time of the next bar.
   dt has to be in the calendar timezone.
   dt can be unaligned open time."
  [[calendar-kw interval-kw] dt]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        _ (assert calendar)
        _ (assert interval)
        next-open-dt (:next-open interval)]
    (next-open-dt calendar dt)))

(defn prior-open
  "returns the open time of the current bar (last bar when the market is closed).
   if dt is an aligned bar open time, then the open time of the prior bar will be returned.
   dt has to be in the calendar timezone.
   dt can be unaligned open time."
  [[calendar-kw interval-kw] dt]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        _ (assert calendar)
        _ (assert interval)
        prior-open-dt (:prior-open interval)]
    (prior-open-dt calendar dt)))

(defn current-open
  "returns the open time of the current bar (last bar when the market is closed).
   if dt is an aligned bar open time, then this open time will be returned.
   dt has to be in the calendar timezone.
   dt can be unaligned open time."
  [[calendar-kw interval-kw] dt]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        _ (assert calendar)
        _ (assert interval)
        _ (assert dt "current open dt is nil.")
        current-open-dt (:current-open interval)]
    (current-open-dt calendar dt)))

(defn close->open-dt
  "converts bar close time to bar open time"
  [[calendar-kw interval-kw] dt]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        _ (assert calendar)
        _ (assert interval)
        _ (assert dt "current close dt is nil.")
        current-close-dt (:current-close interval)
        current-open-dt (:current-open interval)
        prior-open-dt (:prior-open interval)
        aligned-close-dt (current-close-dt calendar dt)]
    (if (= aligned-close-dt dt)
      (prior-open-dt calendar aligned-close-dt)              ; dt is aligned close -> new candle started. prior open needed
      (current-open-dt calendar aligned-close-dt))))         ; dt is not alined close -> unfinished candle. current open needed

(defn open->close-dt
  "converts bar open time to bar close time"
  [[calendar-kw interval-kw] dt]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        _ (assert calendar)
        _ (assert interval)
        _ (assert dt "current open dt is nil.")
        current-open-dt (:current-open interval)
        next-close-dt (:next-close interval)
        aligned-open-dt (current-open-dt calendar dt)]
    (next-close-dt calendar aligned-open-dt)))

(defn calendar-seq
  ([[calendar-kw interval-kw]]
   (let [now (now-calendar calendar-kw)
         cur-dt (current-close [calendar-kw interval-kw] now)]
     (calendar-seq [calendar-kw interval-kw] cur-dt)))
  ([[calendar-kw interval-kw] dt]
   (let [cur-dt (current-close [calendar-kw interval-kw] dt)
         next-dt (partial next-close [calendar-kw interval-kw])]
     (iterate next-dt cur-dt))))

(defn calendar-seq-instant [[calendar-kw interval-kw]]
  (->> (calendar-seq [calendar-kw interval-kw])
       (map t/instant)))

(defn calendar-seq-prior [[calendar-kw interval-kw] dt]
  (let [cur-dt (current-close [calendar-kw interval-kw] dt)
        prior-fn (partial prior-close [calendar-kw interval-kw])]
    (iterate prior-fn cur-dt)))

(defn calendar-seq-prior-open
  "like calendar-seq-prior but with bar open time"
  [[calendar-kw interval-kw] dt]
  (let [cur-dt (current-open [calendar-kw interval-kw] dt)
        prior-fn (partial prior-open [calendar-kw interval-kw])]
    (iterate prior-fn cur-dt)))

(defn trailing-window
  "returns a calendar-seq for a calendar of n rows
   if end-dt specified then last date equals end-date,
   otherwise end-dt is equal to the most-recent close of the calendar"
  ([calendar n end-dt]
   (let [[calendar-kw interval-kw] calendar]
     (take n (calendar-seq-prior [calendar-kw interval-kw] end-dt))))
  ([calendar n]
   (let [[calendar-kw interval-kw] calendar
         now (now-calendar calendar-kw)
         cur-dt (current-close [calendar-kw interval-kw] now)]
     (take n (calendar-seq-prior [calendar-kw interval-kw] cur-dt)))))

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
  [[calendar-kw interval-kw] {:keys [start end] :as window}]
  (let [seq (calendar-seq-prior [calendar-kw interval-kw] end)
        after-start? (fn [dt] (t/>= dt start))]
    (take-while after-start? seq)))

(defn fixed-window-open
  "like fixed-window but with bar open time
   window start and end should be also bar open times"
  [[calendar-kw interval-kw] {:keys [start end] :as window}]
  (let [seq (calendar-seq-prior-open [calendar-kw interval-kw] end)
        after-start? (fn [dt] (t/>= dt start))]
    (take-while after-start? seq)))

(defn calendar-seq->range [cal-seq]
  {:start (last cal-seq)
   :end  (first cal-seq)})

(defn get-bar-window [[calendar-kw interval-kw] bar-end-dt]
  ; TODO: improve
  ; for intraday bars this works fine
  ; for the first bar if the day this is incorrect.
  {:start (prior-close [calendar-kw interval-kw] bar-end-dt)
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