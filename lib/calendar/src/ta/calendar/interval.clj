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


(def intervals
  {:d {:next-close day/next-close
       :prior-close day/prior-close
       :current-close (gen-current-close align-d day/next-close)}
   :h {:next-close next-hour
       :prior-close prior-hour
       :current-close (gen-current-close align-h next-hour)
       :duration (* 60 60)}
   :m {:next-close next-minute
       :prior-close prior-minute
       :current-close (gen-current-close align-m next-minute)
       :duration 60
       }
   :m30 {:next-close next-minute30
         :prior-close prior-minute30
         :current-close (gen-current-close align-h next-minute)
         :duration 30
         }
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
