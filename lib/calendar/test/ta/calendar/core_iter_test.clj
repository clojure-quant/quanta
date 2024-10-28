(ns ta.calendar.core-iter-test
  (:require [clojure.test :refer :all]
            [tick.core :as t]
            [ta.helper.date :refer [at-time]]
            [ta.calendar.data.dates :refer :all]
            [quanta.calendar.core :refer [current-close current-open]]
            [ta.calendar.calendars :as cal]
            [ta.calendar.helper :as h]))

; CURRENT CLOSE
(defn test-current-close-on-interval-boundary [calendar-kw]
  (let [{:keys [open close timezone] :as cal} (calendar-kw cal/calendars)
        cal-thursday-open-plus-4h (-> (at-time (t/date "2024-02-08") open timezone) (t/>> (t/new-duration 4 :hours)))
        cal-thursday-open-plus-4h-minus-1m (-> cal-thursday-open-plus-4h (t/<< (t/new-duration 1 :minutes)))
        cal-thursday-open-plus-4h-minus-15m (-> cal-thursday-open-plus-4h (t/<< (t/new-duration 15 :minutes)))
        cal-thursday-open-plus-4h-minus-1h (-> cal-thursday-open-plus-4h (t/<< (t/new-duration 1 :hours)))
        cal-thursday-open-plus-4h-minus-4h (-> cal-thursday-open-plus-4h (t/<< (t/new-duration 4 :hours)))
        cal-wednesday-close (at-time (t/date "2024-02-07") close timezone)]
    (testing "dt on interval boundary"
      ; 1 min
      (is (t/= cal-thursday-open-plus-4h (current-close [calendar-kw :m] cal-thursday-open-plus-4h)))
      (is (not (t/= cal-thursday-open-plus-4h-minus-1m (current-close [calendar-kw :m] cal-thursday-open-plus-4h))))
      ; 15 min
      (is (t/= cal-thursday-open-plus-4h (current-close [calendar-kw :m15] cal-thursday-open-plus-4h)))
      (is (not (t/= cal-thursday-open-plus-4h-minus-15m (current-close [calendar-kw :m15] cal-thursday-open-plus-4h))))
      ; 1 hour
      (is (t/= cal-thursday-open-plus-4h (current-close [calendar-kw :h] cal-thursday-open-plus-4h)))
      (is (not (t/= cal-thursday-open-plus-4h-minus-1h (current-close [calendar-kw :h] cal-thursday-open-plus-4h))))
      ; 4 hour
      (is (t/= cal-thursday-open-plus-4h (current-close [calendar-kw :h4] cal-thursday-open-plus-4h)))
      (is (not (t/= cal-thursday-open-plus-4h-minus-4h (current-close [calendar-kw :h4] cal-thursday-open-plus-4h))))
      ; 1 day
      (is (t/= cal-wednesday-close (current-close [calendar-kw :d] cal-thursday-open-plus-4h)))
      (is (not (t/= cal-thursday-open-plus-4h (current-close [calendar-kw :d] cal-thursday-open-plus-4h))))
      )))

(defn test-current-close-on-trading-open [calendar-kw]
  ;; TODO make more generic. not all calendar open time is midnight...
  (let [{:keys [open close timezone] :as cal} (calendar-kw cal/calendars)
        cal-thursday-open (at-time (t/date "2024-02-08") open timezone)
        cal-wednesday-close (at-time (t/date "2024-02-07") close timezone)
        cal-thursday-open-plus-1m (-> cal-thursday-open (t/>> (t/new-duration 1 :minutes)))
        cal-thursday-open-plus-15m (-> cal-thursday-open (t/>> (t/new-duration 15 :minutes)))
        cal-thursday-open-plus-1h (-> cal-thursday-open (t/>> (t/new-duration 1 :hours)))
        cal-thursday-open-plus-4h (-> cal-thursday-open (t/>> (t/new-duration 4 :hours)))]
    (testing "dt on trading open boundary"
      ; 1 min
      (is (t/= cal-wednesday-close (current-close [calendar-kw :m] cal-thursday-open)))
      (is (not (t/= cal-thursday-open-plus-1m (current-close [calendar-kw :m] cal-thursday-open))))
      ; 15 min
      (is (t/= cal-wednesday-close (current-close [calendar-kw :m15] cal-thursday-open)))
      (is (not (t/= cal-thursday-open-plus-15m (current-close [calendar-kw :m15] cal-thursday-open))))
      ; 1 hour
      (is (t/= cal-wednesday-close (current-close [calendar-kw :h] cal-thursday-open)))
      (is (not (t/= cal-thursday-open-plus-1h (current-close [calendar-kw :h] cal-thursday-open))))
      ; 4 hour
      (is (t/= cal-wednesday-close (current-close [calendar-kw :h4] cal-thursday-open)))
      (is (not (t/= cal-thursday-open-plus-4h (current-close [calendar-kw :h4] cal-thursday-open))))
      ; 1 day
      (is (t/= cal-wednesday-close (current-close [calendar-kw :d] cal-thursday-open)))
      (is (not (t/= cal-thursday-open-plus-4h (current-close [calendar-kw :d] cal-thursday-open))))
      )))

(defn test-current-close-on-trading-close [calendar-kw]
  ;; TODO make more generic. not all calendar open time is midnight...
  (let [{:keys [close timezone] :as cal} (calendar-kw cal/calendars)
        cal-thursday-close (at-time (t/date "2024-02-08") close timezone)
        cal-friday-open  (at-time (t/date "2024-02-09") close timezone)
        cal-friday-open-plus-1m (-> cal-friday-open (t/>> (t/new-duration 1 :minutes)))
        cal-friday-open-plus-15m (-> cal-friday-open (t/>> (t/new-duration 15 :minutes)))
        cal-friday-open-plus-1h (-> cal-friday-open (t/>> (t/new-duration 1 :hours)))
        cal-friday-open-plus-4h (-> cal-friday-open (t/>> (t/new-duration 4 :hours)))]
    (testing "dt on trading close boundary"
      ; 1 min
      (is (t/= cal-thursday-close (current-close [calendar-kw :m] cal-thursday-close)))
      (is (not (t/= cal-friday-open-plus-1m (current-close [calendar-kw :m] cal-thursday-close))))
      ; 15 min
      (is (t/= cal-thursday-close (current-close [calendar-kw :m15] cal-thursday-close)))
      (is (not (t/= cal-friday-open-plus-15m (current-close [calendar-kw :m15] cal-thursday-close))))
      ; 1 hour
      (is (t/= cal-thursday-close (current-close [calendar-kw :h] cal-thursday-close)))
      (is (not (t/= cal-friday-open-plus-1h (current-close [calendar-kw :h] cal-thursday-close))))
      ; 4 hour
      (is (t/= cal-thursday-close (current-close [calendar-kw :h4] cal-thursday-close)))
      (is (not (t/= cal-friday-open-plus-4h (current-close [calendar-kw :h4] cal-thursday-close))))
      ; 1 day
      (is (t/= cal-thursday-close (current-close [calendar-kw :d] cal-thursday-close)))
      (is (not (t/= cal-friday-open-plus-4h (current-close [calendar-kw :d] cal-thursday-close))))
      )))


; CURRENT OPEN
(defn test-current-open-on-interval-boundary [calendar-kw]
  (let [{:keys [open timezone] :as cal} (calendar-kw cal/calendars)
        cal-thursday-open (at-time (t/date "2024-02-08") open timezone)
        cal-thursday-open-plus-4h (-> cal-thursday-open (t/>> (t/new-duration 4 :hours)))
        cal-thursday-open-plus-4h-minus-1m (-> cal-thursday-open-plus-4h (t/<< (t/new-duration 1 :minutes)))
        cal-thursday-open-plus-4h-minus-15m (-> cal-thursday-open-plus-4h (t/<< (t/new-duration 15 :minutes)))
        cal-thursday-open-plus-4h-minus-1h (-> cal-thursday-open-plus-4h (t/<< (t/new-duration 1 :hours)))
        cal-thursday-open-plus-4h-minus-4h (-> cal-thursday-open-plus-4h (t/<< (t/new-duration 4 :hours)))]
    (testing "dt on interval boundary"
      ; 1 min
      (is (t/= cal-thursday-open-plus-4h (current-open [calendar-kw :m] cal-thursday-open-plus-4h)))
      (is (not (t/= cal-thursday-open-plus-4h-minus-1m (current-open [calendar-kw :m] cal-thursday-open-plus-4h))))
      ; 15 min
      (is (t/= cal-thursday-open-plus-4h (current-open [calendar-kw :m15] cal-thursday-open-plus-4h)))
      (is (not (t/= cal-thursday-open-plus-4h-minus-15m (current-open [calendar-kw :m15] cal-thursday-open-plus-4h))))
      ; 1 hour
      (is (t/= cal-thursday-open-plus-4h (current-open [calendar-kw :h] cal-thursday-open-plus-4h)))
      (is (not (t/= cal-thursday-open-plus-4h-minus-1h (current-open [calendar-kw :h] cal-thursday-open-plus-4h))))
      ; 4 hour
      (is (t/= cal-thursday-open-plus-4h (current-open [calendar-kw :h4] cal-thursday-open-plus-4h)))
      (is (not (t/= cal-thursday-open-plus-4h-minus-4h (current-open [calendar-kw :h4] cal-thursday-open-plus-4h))))
      ; 1 day
      (is (t/= cal-thursday-open (current-open [calendar-kw :d] cal-thursday-open-plus-4h)))
      (is (not (t/= cal-thursday-open-plus-4h (current-open [calendar-kw :d] cal-thursday-open-plus-4h))))
      )))

(defn test-current-open-on-trading-open [calendar-kw]
  ;; TODO make more generic. not all calendar open time is midnight...
  (let [{:keys [open timezone] :as cal} (calendar-kw cal/calendars)
        cal-thursday-open (at-time (t/date "2024-02-08") open timezone)
        cal-thursday-open-minus-1m (-> cal-thursday-open (t/<< (t/new-duration 1 :minutes)))
        cal-thursday-open-minus-15m (-> cal-thursday-open (t/<< (t/new-duration 15 :minutes)))
        cal-thursday-open-minus-1h (-> cal-thursday-open (t/<< (t/new-duration 1 :hours)))
        cal-thursday-open-minus-4h (-> cal-thursday-open (t/<< (t/new-duration 4 :hours)))]
    (testing "dt on trading open boundary"
      ; 1 min
      (is (t/= cal-thursday-open (current-open [calendar-kw :m] cal-thursday-open)))
      (is (not (t/= cal-thursday-open-minus-1m (current-open [calendar-kw :m] cal-thursday-open))))
      ; 15 min
      (is (t/= cal-thursday-open (current-open [calendar-kw :m15] cal-thursday-open)))
      (is (not (t/= cal-thursday-open-minus-15m (current-open [calendar-kw :m15] cal-thursday-open))))
      ; 1 hour
      (is (t/= cal-thursday-open (current-open [calendar-kw :h] cal-thursday-open)))
      (is (not (t/= cal-thursday-open-minus-1h (current-open [calendar-kw :h] cal-thursday-open))))
      ; 4 hour
      (is (t/= cal-thursday-open (current-open [calendar-kw :h4] cal-thursday-open)))
      (is (not (t/= cal-thursday-open-minus-4h (current-open [calendar-kw :h4] cal-thursday-open))))
      ; 1 day
      (is (t/= cal-thursday-open (current-open [calendar-kw :d] cal-thursday-open)))
      (is (not (t/= cal-thursday-open-minus-4h (current-open [calendar-kw :d] cal-thursday-open))))
      )))

(defn test-current-open-on-trading-close [calendar-kw]
  ;; TODO make more generic. not all calendar open time is midnight...
  (let [{:keys [close open timezone] :as cal} (calendar-kw cal/calendars)
        cal-thursday-open (at-time (t/date "2024-02-08") open timezone)
        cal-thursday-close (at-time (t/date "2024-02-08") close timezone)
        cal-friday-open (at-time (t/date "2024-02-09") open timezone)
        aligned-close (if (h/midnight-close? close)
                        cal-friday-open
                        cal-thursday-close)
        cal-close-minus-1m (-> aligned-close (t/<< (t/new-duration 1 :minutes)))
        cal-close-minus-15m (-> aligned-close (t/<< (t/new-duration 15 :minutes)))
        cal-close-minus-1h (-> aligned-close (t/<< (t/new-duration 1 :hours)))
        cal-close-minus-4h (-> aligned-close (t/<< (t/new-duration 4 :hours)))]
    (testing "dt on trading close boundary"
      ; 1 min
      (is (t/= cal-close-minus-1m (current-open [calendar-kw :m] cal-thursday-close)))
      (is (not (t/= cal-friday-open (current-open [calendar-kw :m] cal-thursday-close))))
      ; 15 min
      (is (t/= cal-close-minus-15m (current-open [calendar-kw :m15] cal-thursday-close)))
      (is (not (t/= cal-friday-open (current-open [calendar-kw :m15] cal-thursday-close))))
      ; 1 hour
      (is (t/= cal-close-minus-1h (current-open [calendar-kw :h] cal-thursday-close)))
      (is (not (t/= cal-friday-open (current-open [calendar-kw :h] cal-thursday-close))))
      ; 4 hour
      (is (t/= cal-close-minus-4h (current-open [calendar-kw :h4] cal-thursday-close)))
      (is (not (t/= cal-friday-open (current-open [calendar-kw :h4] cal-thursday-close))))
      ; 1 day
      (is (t/= cal-thursday-open (current-open [calendar-kw :d] cal-thursday-close)))
      (is (not (t/= cal-friday-open (current-open [calendar-kw :d] cal-thursday-close))))
      )))

(deftest current-close-crypto
  (test-current-close-on-interval-boundary :crypto)
  (test-current-close-on-trading-open :crypto)
  (test-current-close-on-trading-close :crypto)
  (test-current-open-on-interval-boundary :crypto)
  (test-current-open-on-trading-open :crypto)
  (test-current-open-on-trading-close :crypto)
  )