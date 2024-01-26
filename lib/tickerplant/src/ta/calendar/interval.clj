(ns ta.calendar.interval
  (:require
   [tick.core :as t]
   [ta.calendar.day :as day]
   [ta.calendar.intraday :as intraday]))

(defn now-in-zone [zone]
  (-> (t/now)
      (t/in zone)))

(defn now-calendar [{:keys [timezone] :as calendar}]
  (now-in-zone timezone))

; align fns

(defn align-m [dt]
  (-> dt
      (t/with :second-of-minute 0)
      (t/with :nano-of-second 0)))

(defn align-h [dt]
  (-> dt
      (align-m)
      (t/with :minute-of-hour 0)))

 (defn align-d [dt]
  (-> dt
      (align-m)
      (t/with :minute-of-hour 0)))

(defn gen-current-close [align-fn next-fn]
  (fn [calendar]
    ;(println "current-close for calendar: " calendar)
    (let [now (now-calendar calendar)
          now-aligned (align-fn now)
          dt-next (next-fn calendar now-aligned)]
      dt-next)))


 (defn next-hour [calendar dt]
   (intraday/next-intraday (t/new-duration 1 :hours) calendar dt))
 
 (defn next-minute [calendar dt]
  (intraday/next-intraday (t/new-duration 1 :minutes) calendar dt))

  (defn prior-hour [calendar dt]
   (intraday/prior-intraday (t/new-duration 1 :hours) calendar dt))
 
 (defn prior-minute [calendar dt]
   (intraday/prior-intraday (t/new-duration 1 :minutes) calendar dt))


(def intervals
  {:day {:next-close day/next-close
         :prior-close day/prior-close
         :current-close (gen-current-close align-d day/next-close)}
   :h {:next-close next-hour
       :prior-close prior-hour
       :current-close (gen-current-close align-h next-hour)}
   :m {:next-close next-minute
       :prior-close prior-minute
       :current-close (gen-current-close align-m next-minute)}
   })


(comment
  (now-in-zone "Europe/Paris")
  (now-in-zone "America/New_York")

  (-> (now-in-zone "America/New_York")
      (t/with  :minute-of-hour  0)
      (t/with :second-of-minute 0)
      (t/with :nano-of-second 0))

  (require '[cljc.java-time.zone-id :refer [get-available-zone-ids]])
  (->> (get-available-zone-ids)
       (map str)
       (sort)
       println)
  
  (:h intervals)
  (gen-current-close align-d day/next-close)

  (def day (:day intervals))
  day
  (require '[ta.calendar.calendars :refer [calendars]])
  (def us (:us calendars))
  us
  (def hour (:h intervals))
  hour

  (def next-close-day (:next-close day))
  (next-close-day us (now-calendar us))
  (def next-close-hour (:next-close hour))
  (next-close-hour us (now-calendar us))
  
  (def current-close-day (:current-close day))
  (current-close-day us)

 ; 
  )
