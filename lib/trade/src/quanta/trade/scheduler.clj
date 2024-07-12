(ns quanta.trade.scheduler
  (:require
   [tick.core :as t]
   [ta.calendar.core :refer [current-close next-close]]
   [missionary.core :as m]
   [ta.calendar.calendars :refer [get-calendar-list]]
   [ta.calendar.interval :refer [get-interval-list]]))

(defn log [& data]
  (let [s (with-out-str (apply println data))]
    (println s)
    (spit "/home/florian/repo/clojure-quant/quanta/broker.txt" s :append true)))

(defn scheduler
  "returns a missionary flow
   fires all upcoming timestamps for a calendar
   "
  [calendar]
  (m/ap
   (log "starting scheduler for calendar: " calendar)
   (let [[market-kw interval-kw] calendar]
     (loop [dt (t/now)
            current-dt (current-close market-kw interval-kw dt)]
       (let [current-dt-inst (t/instant current-dt)
             diff-ms (* 1000 (- (t/long current-dt-inst) (t/long dt)))]
         (when (> diff-ms 0)
           (log "sleeping for ms: " diff-ms " until: " current-dt)
           (m/? (m/sleep diff-ms current-dt))
           (log "finished sleeping")
           :bongo)
         (m/amb
          current-dt
          (recur (t/now)
                 (next-close market-kw interval-kw current-dt))))))))

(defn all-calendars []
  (->> (for [c (get-calendar-list)
             i (get-interval-list)]
         (let [cal [c i]]
           [cal (m/stream (scheduler cal))]))
       (into {})))

(def calendar-dict (all-calendars))

(defn get-calendar-flow [calendar]
  (get calendar-dict calendar))

(comment
  (count (all-calendars))
  calendar-dict

  (get-calendar-flow [:crypto :h])

  ; one robot.
  (m/?
   (m/reduce (fn [_ dt]
               (log "robot processing date: " dt))
             nil
             (scheduler [:crypto :m])))

  (def multirobot
    (let [s (get-calendar-flow [:crypto :m])
          robot1 (m/reduce (fn [_ dt]
                             (log "robot-1 processing date: " dt))
                           nil
                           s)
          robot2 (m/reduce (fn [_ dt]
                             (log "robot-2 processing date: " dt))
                           nil
                           s)]
      (m/join vector robot1 robot2)))

  (def dispose!
    (multirobot
     #(prn ::success %)
     #(prn ::crash %)))
  
  (dispose!)

;  
  )





