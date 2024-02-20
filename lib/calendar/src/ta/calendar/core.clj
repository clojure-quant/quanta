(ns ta.calendar.core
  (:require
   [tick.core :as t]
   [ta.calendar.interval :refer [intervals] :as interval]
   [ta.calendar.calendars :refer [calendars]]
   [ta.calendar.core :as cal]))

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
        _ (assert calendar)
        _ (assert interval)
        ;_ (println "calendar: " calendar)
        ;_ (println "interval: " interval)
        prior-close-dt (:prior-close interval)]
    (prior-close-dt calendar dt)))

(defn current-close [calendar-kw interval-kw & [dt]]
  (let [calendar (calendar-kw calendars)
        interval (interval-kw intervals)
        ;_ (println "calendar: " calendar)
        ;_ (println "interval: " interval)
        _ (assert calendar)
        _ (assert interval)
        current-close-dt (:current-close interval)]
    (if dt
      (current-close-dt calendar dt)
      (current-close-dt calendar))))


(defn calendar-seq [calendar-kw interval-kw]
  (let [start (current-close calendar-kw interval-kw)
        next-dt (partial next-close calendar-kw interval-kw)]
  (iterate next-dt start)))

(defn calendar-seq-instant [calendar-kw interval-kw]
  (->> (calendar-seq calendar-kw interval-kw)
       (map t/instant)))

(defn calendar-seq-prior [calendar-kw interval-kw dt]
  (let [cur-dt (current-close calendar-kw interval-kw dt)
        prev-dt (prior-close calendar-kw interval-kw dt)
        start-dt (if (t/= cur-dt dt) ; dt on interval boudary
                   cur-dt
                   prev-dt)
        prior-fn (partial prior-close calendar-kw interval-kw)]
    (iterate prior-fn start-dt)))

(defn trailing-window 
  ([calendar-kw interval-kw n end-dt]
   (take n (calendar-seq-prior calendar-kw interval-kw end-dt)))
  ([calendar-kw interval-kw n]
   (take n (calendar-seq-prior calendar-kw interval-kw (now-calendar calendar-kw)))))

(defn trailing-window2
  ([[calendar-kw interval-kw] n end-dt] ; dt-end
   (take n (calendar-seq-prior calendar-kw interval-kw end-dt)))
  ([[calendar-kw interval-kw ] n]
   (let [end (current-close calendar-kw interval-kw)
         end-dt (prior-close calendar-kw interval-kw end)]
     (take n (calendar-seq-prior calendar-kw interval-kw end-dt)))))

(defn trailing-range-current 
   [calendar n]
  (let [[calendar-kw interval-kw] calendar
        time (now-calendar calendar-kw)
        end-dt (prior-close calendar-kw interval-kw time)
        window (trailing-window calendar-kw interval-kw n end-dt)]
     {:end (first window)
      :start (last window)}))

(defn fixed-window
  [[calendar-kw interval-kw] {:keys [start end]}] 
  (let [seq (calendar-seq-prior calendar-kw interval-kw end)
        after-start? (fn [dt] (t/>= dt start))]
   (take-while after-start? seq)  
    ))

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
  (if (= interval-kw :d)
      1000 ; TODO: improve. should depend on open/close time or the calendar-kw.
      (get-in intervals [interval-kw :duration])))


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
   (take 5 (calendar-seq-instant :us :day))
  
   (take 5 (calendar-seq :eu :day))
   (take 30 (calendar-seq :us :day))
   (take 100 (calendar-seq :eu :h))
   (take 100 (calendar-seq-prior :eu :h))


    (take 5 (calendar-seq :eu :h))
  
  (trailing-window :us :day 5)
  (trailing-window :us :day 10)
  (trailing-window :us :h 5)
   

   (get-bar-duration [:us :d])
   (get-bar-duration [:us :m])

   (-> (fixed-window [:us :d] {:start (t/date-time "2023-01-01T00:00:00")
                               :end (t/date-time "2023-02-01T00:00:00")})
       calendar-seq->range
    )
   
   (-> (trailing-window :us :d 5)
       calendar-seq->range
    
    )
   
   

   
    
  
 ; 
  )