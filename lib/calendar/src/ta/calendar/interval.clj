(ns ta.calendar.interval
  (:require
   [tick.core :as t]
   [ta.calendar.day :as day]
   [ta.calendar.intraday :as intraday]
   [ta.helper.date :refer [align-field now-in-zone]]))

(defn now-calendar [{:keys [timezone] :as calendar}]
  (now-in-zone timezone))

(defn current-close-aligned [calendar dt align-unit next-fn]
  (let [zoned (t/in dt (:timezone calendar))
        aligned (align-field zoned align-unit)
        dt-next (next-fn calendar aligned)]
    dt-next))

(defn gen-current-close [align-unit next-fn]
  (fn ([calendar] (current-close-aligned calendar (t/now) align-unit next-fn))
      ([calendar dt] (current-close-aligned calendar dt align-unit next-fn))))

(defn gen-intraday-step-fn [n unit]
  ; close
  {:next-close     (fn ([calendar] (intraday/next-close-dt calendar n unit))
                       ([calendar dt] (intraday/next-close-dt calendar n unit dt)))

   :prior-close    (fn ([calendar] (intraday/prior-close-dt calendar n unit))
                       ([calendar dt] (intraday/prior-close-dt calendar n unit dt)))

   :current-close  (fn ([calendar] (intraday/current-close-dt calendar n unit))
                       ([calendar dt] (intraday/current-close-dt calendar n unit dt)))

   ; open
   :next-open      (fn ([calendar] (intraday/next-open-dt calendar n unit))
                       ([calendar dt] (intraday/next-open-dt calendar n unit dt)))

   :prior-open     (fn ([calendar] (intraday/prior-open-dt calendar n unit))
                       ([calendar dt] (intraday/prior-open-dt calendar n unit dt)))

   :current-open   (fn ([calendar] (intraday/current-open-dt calendar n unit))
                       ([calendar dt] (intraday/current-open-dt calendar n unit dt)))

   ; duration
   :duration       (t/divide (t/new-duration n unit) (t/new-duration 1 :seconds))
   })

(def intervals
  {:d   {:next-close    day/next-close-dt
         :prior-close   day/prior-close-dt
         :current-close (gen-current-close :minutes day/current-close)}
   :h   (gen-intraday-step-fn 1 :hours)
   :m   (gen-intraday-step-fn 1 :minutes)
   :m15 (gen-intraday-step-fn 15 :minutes)
   :m30 (gen-intraday-step-fn 30 :minutes)
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
