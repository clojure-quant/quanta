(ns quanta.calendar.scheduler
  (:require
   [tick.core :as t]
   [missionary.core :as m]
   [ta.calendar.core :refer [current-close next-close]]
   [ta.calendar.calendars :refer [get-calendar-list]]
   [ta.calendar.interval :refer [get-interval-list]])
  (:import [missionary Cancelled]))

;; move-in from quanta.algo.dag.calendar.live
;; OUTDATED. MOVED TO QUANTA-CALENDAR.

(defn scheduler
  "returns a missionary flow
   fires current and all upcoming timestamps for a calendar"
  [calendar]
  ;(m/stream
  (m/signal
   (m/ap
    (let [[market-kw interval-kw] calendar
          dt (t/instant)
          current-close (current-close market-kw interval-kw dt)]
      (println "live-calendar " calendar " starting with: " current-close)
      (m/amb current-close)
      (try
        (loop [dt dt
               current-dt current-close]
          (let [current-dt-inst (t/instant current-dt)
                diff-ms (* 1000 (- (t/long current-dt-inst) (t/long dt)))]
            (when (> diff-ms 0)

              (println "live-calendar " calendar " sleeping for ms: " diff-ms " until: " current-dt)
              (m/? (m/sleep diff-ms current-dt))
              (println "live-calendar " calendar " finished sleeping")))
          (m/amb
           current-dt
           (recur (t/instant)
                  (next-close market-kw interval-kw current-dt))))
        (catch Cancelled cancel
          (println "live-calendar " calendar " stopped.")))))))

(defn all-calendars []
  (->> (for [c (get-calendar-list)
             i (get-interval-list)]
         (let [cal [c i]]
           [cal (scheduler cal)]))
       (into {})))

(def calendar-dict (all-calendars))

(defn get-calendar-flow [calendar]
  (get calendar-dict calendar))

(comment

  (t/now)
  (t/instant)

  (current-close :crypto :m)
  ;; => #time/zoned-date-time "2024-10-02T00:19Z[UTC]"

  (next-close :crypto :m
              (current-close :crypto :m))
  ;; => #time/zoned-date-time "2024-10-02T00:20Z[UTC]"

  (class (t/now))
  (class (t/instant))

  (m/? (->> (scheduler [:us :d])
            (m/eduction (take 1))
            (m/reduce conj)))

  (m/? (->> (scheduler [:forex :m])
            (m/eduction (take 2))
            (m/reduce conj)))

  (m/? (->> (scheduler [:crypto :m])
            (m/eduction (take 2))
            (m/reduce conj)))

  (get-calendar-flow [:forex :m])
  (get-calendar-flow [:forex :m333])

 ;
  )
