(ns ta.calendar.core
  (:require
   [ta.calendar.interval :refer [intervals] :as interval]
   [ta.calendar.calendars :refer [calendars]]))

(defn now-calendar [calendar-kw]
  (let [calendar (calendar-kw calendars)]
    (interval/now-calendar calendar)))

(defn next-close [calendar-kw interval-kw dt]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        ;_ (println "calendar: " calendar)
        ;_ (println "interval: " interval)
        next-close-dt (:next-close interval)]
    (next-close-dt calendar dt)))

(defn prior-close [calendar-kw interval-kw dt]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        ;_ (println "calendar: " calendar)
        ;_ (println "interval: " interval)
        prior-close-dt (:prior-close interval)]
    (prior-close-dt calendar dt)))

(defn current-close [calendar-kw interval-kw]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        ;_ (println "calendar: " calendar)
        ;_ (println "interval: " interval)
        current-close-dt (:current-close interval)]
    (current-close-dt calendar)))


(defn calendar-seq [calendar-kw interval-kw]
  (let [start (current-close calendar-kw interval-kw)
        next-dt (partial next-close calendar-kw interval-kw)]
  (iterate next-dt start)))

(defn calendar-seq-prior [calendar-kw interval-kw]
  (let [end (current-close calendar-kw interval-kw)
        end-dt (prior-close calendar-kw interval-kw end)
        prior-dt (partial prior-close calendar-kw interval-kw)]
    (iterate prior-dt end-dt)))

(defn trailing-window 
  ([calendar-kw interval-kw n] ; dt-end
    (take n (calendar-seq-prior calendar-kw interval-kw))))

(comment 
   (now-calendar :us)
   (now-calendar :eu)
  
   (next-close :us :day (now-calendar :us))
   (next-close :us :h (now-calendar :us))

   (prior-close :us :day (now-calendar :us))
   (prior-close :us :h (now-calendar :us))

   (current-close :us :day)
   (current-close :us :h)
   (current-close :us :m)
  
   (take 5 (calendar-seq :us :day))
   (take 5 (calendar-seq :eu :day))
   (take 30 (calendar-seq :us :day))
  
    (take 5 (calendar-seq :eu :h))
  
  (trailing-window :us :day 5)
  (trailing-window :us :day 10)
  (trailing-window :us :h 5)
   
    
  
 ; 
  )