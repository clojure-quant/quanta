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
        prev-close (at-time (if (h/midnight-close? close)
                                           (t/date "2024-02-08")
                                           (t/date "2024-02-07")) close timezone)
        next-close (at-time (if (h/midnight-close? close)
                              (t/date "2024-02-09")
                              (t/date "2024-02-08")) close timezone)]
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
      (is (t/= prev-close (current-close [calendar-kw :d] cal-thursday-open-plus-4h)))
      (is (not (t/= next-close (current-close [calendar-kw :d] cal-thursday-open-plus-4h))))
      )))

(defn test-current-close-on-trading-open [calendar-kw]
  ;; TODO make more generic. not all calendar open time is midnight...
  (let [{:keys [open close timezone] :as cal} (calendar-kw cal/calendars)
        cal-thursday-open (at-time (t/date "2024-02-08") open timezone)
        cal-thursday-open-plus-1m (-> cal-thursday-open (t/>> (t/new-duration 1 :minutes)))
        cal-thursday-open-plus-15m (-> cal-thursday-open (t/>> (t/new-duration 15 :minutes)))
        cal-thursday-open-plus-1h (-> cal-thursday-open (t/>> (t/new-duration 1 :hours)))
        cal-thursday-open-plus-4h (-> cal-thursday-open (t/>> (t/new-duration 4 :hours)))
        cal-thursday-open-plus-1d (-> cal-thursday-open (t/>> (t/new-duration 1 :days)))
        current-close-1m (if (and (t/= open close) (h/midnight-close? close))
                           cal-thursday-open
                           cal-thursday-open-plus-1m)
        current-close-15m (if (and (t/= open close) (h/midnight-close? close))
                           cal-thursday-open
                           cal-thursday-open-plus-15m)
        current-close-1h (if (and (t/= open close) (h/midnight-close? close))
                           cal-thursday-open
                           cal-thursday-open-plus-1h)
        current-close-4h (if (and (t/= open close) (h/midnight-close? close))
                           cal-thursday-open
                           cal-thursday-open-plus-4h)
        current-close-1d (if (and (t/= open close) (h/midnight-close? close))
                           cal-thursday-open
                           cal-thursday-open-plus-1d)]
    (testing "dt on trading open boundary"
      ; 1 min
      (is (t/= current-close-1m (current-close [calendar-kw :m] cal-thursday-open)))
      ;(is (not (t/= cal-thursday-open-plus-1m (current-close [calendar-kw :m] cal-thursday-open))))
      ; 15 min
      (is (t/= current-close-15m (current-close [calendar-kw :m15] cal-thursday-open)))
      ;(is (not (t/= cal-thursday-open-plus-15m (current-close [calendar-kw :m15] cal-thursday-open))))
      ; 1 hour
      (is (t/= current-close-1h (current-close [calendar-kw :h] cal-thursday-open)))
      ;(is (not (t/= cal-thursday-open-plus-1h (current-close [calendar-kw :h] cal-thursday-open))))
      ; 4 hour
      (is (t/= current-close-4h (current-close [calendar-kw :h4] cal-thursday-open)))
      ;(is (not (t/= cal-thursday-open-plus-4h (current-close [calendar-kw :h4] cal-thursday-open))))
      ; 1 day
      (is (t/= current-close-1d (current-close [calendar-kw :d] cal-thursday-open)))
      ;(is (not (t/= cal-thursday-open-plus-4h (current-close [calendar-kw :d] cal-thursday-open))))
      )))

(defn test-current-close-on-trading-close [calendar-kw]
  ;; TODO make more generic. not all calendar open time is midnight...
  (let [{:keys [close timezone] :as cal} (calendar-kw cal/calendars)
        cal-thursday-close (at-time (if (h/midnight-close? close)
                                      (t/date "2024-02-09")
                                      (t/date "2024-02-08")) 
                                    close timezone)
        cal-thursday-close-plus-1m (-> cal-thursday-close (t/>> (t/new-duration 1 :minutes)))
        cal-thursday-close-plus-15m (-> cal-thursday-close (t/>> (t/new-duration 15 :minutes)))
        cal-thursday-close-plus-1h (-> cal-thursday-close (t/>> (t/new-duration 1 :hours)))
        cal-thursday-close-plus-4h (-> cal-thursday-close (t/>> (t/new-duration 4 :hours)))
        cal-close-plus-1d (-> cal-thursday-close (t/>> (t/new-duration 1 :days)))]
    (testing "dt on trading close boundary"
      ; 1 min
      (is (t/= cal-thursday-close (current-close [calendar-kw :m] cal-thursday-close)))
      (is (not (t/= cal-thursday-close-plus-1m (current-close [calendar-kw :m] cal-thursday-close))))
      ; 15 min
      (is (t/= cal-thursday-close (current-close [calendar-kw :m15] cal-thursday-close)))
      (is (not (t/= cal-thursday-close-plus-15m (current-close [calendar-kw :m15] cal-thursday-close))))
      ; 1 hour
      (is (t/= cal-thursday-close (current-close [calendar-kw :h] cal-thursday-close)))
      (is (not (t/= cal-thursday-close-plus-1h (current-close [calendar-kw :h] cal-thursday-close))))
      ; 4 hour
      (is (t/= cal-thursday-close (current-close [calendar-kw :h4] cal-thursday-close)))
      (is (not (t/= cal-thursday-close-plus-4h (current-close [calendar-kw :h4] cal-thursday-close))))
      ; 1 day
      (is (t/= cal-thursday-close (current-close [calendar-kw :d] cal-thursday-close)))
      (is (not (t/= cal-thursday-close-plus-4h (current-close [calendar-kw :d] cal-thursday-close))))
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
        cal-thursday-close (at-time (if (h/midnight-close? close)
                                      (t/date "2024-02-09")
                                      (t/date "2024-02-08"))
                                    close timezone)
        cal-close-minus-1m (-> cal-thursday-close (t/<< (t/new-duration 1 :minutes)))
        cal-close-minus-15m (-> cal-thursday-close (t/<< (t/new-duration 15 :minutes)))
        cal-close-minus-1h (-> cal-thursday-close (t/<< (t/new-duration 1 :hours)))
        cal-close-minus-4h (-> cal-thursday-close (t/<< (t/new-duration 4 :hours)))
        cal-close-minus-1d (-> cal-thursday-close (t/<< (t/new-duration 1 :days)))]
    (testing "dt on trading close boundary"
      ; 1 min
      (is (t/= cal-thursday-close (current-open [calendar-kw :m] cal-thursday-close)))
      (is (not (t/= cal-close-minus-1m (current-open [calendar-kw :m] cal-thursday-close))))
      ; 15 min
      (is (t/= cal-thursday-close (current-open [calendar-kw :m15] cal-thursday-close)))
      (is (not (t/= cal-close-minus-15m (current-open [calendar-kw :m15] cal-thursday-close))))
      ; 1 hour
      (is (t/= cal-thursday-close (current-open [calendar-kw :h] cal-thursday-close)))
      (is (not (t/= cal-close-minus-1h (current-open [calendar-kw :h] cal-thursday-close))))
      ; 4 hour
      (is (t/= cal-thursday-close (current-open [calendar-kw :h4] cal-thursday-close)))
      (is (not (t/= cal-close-minus-4h (current-open [calendar-kw :h4] cal-thursday-close))))
      ; 1 day
      (is (t/= cal-thursday-close (current-open [calendar-kw :d] cal-thursday-close)))
      (is (not (t/= cal-close-minus-1d (current-open [calendar-kw :d] cal-thursday-close))))
      )))

(deftest current-close-crypto
  (test-current-close-on-interval-boundary :crypto)
  (test-current-close-on-trading-open :crypto)
  (test-current-close-on-trading-close :crypto)
  (test-current-open-on-interval-boundary :crypto)
  (test-current-open-on-trading-open :crypto)
  (test-current-open-on-trading-close :crypto)
  )