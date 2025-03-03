(ns quanta.calendar.combined
  "the main usecase for a combined calendar is historical simulations."
  (:require
   [tick.core :as t]
   [ta.calendar.core :as cal]))

;; calendar-reader

(defn- calendar-seq-reader
  "returns a reader, a function without arguments that always returns the 
   next date for the sequence"
  [dt-start [calendar-kw interval-kw]]
  (let [start (cal/prior-close calendar-kw interval-kw dt-start)
        state (atom start)
        read-next (fn []
                    (let [dt (cal/next-close calendar-kw interval-kw @state)]
                      (reset! state dt)
                      dt))]
    read-next))

(defn- create-readers [dt-start windows]
  ; note: needs to return a vector, as only vectors can be randomly accessed.
  (->> (map #(calendar-seq-reader dt-start %) windows)
       (into [])))

(defn- get-next-date [readers idx]
  ;(println "get-next-date idx: " idx)
  (let [reader (get readers idx)]
    (reader)))

;; combiner

(defn- create-initial-state [readers]
  (->> (map-indexed (fn [idx _reader]
                      ;(println "idx: " idx)
                      [idx (get-next-date readers idx)])
                    readers)
       (into {})
       atom))

(defn- get-next-index [state]
  (->> (map (fn [[k v]]
              {:idx k :dt v}) @state)
       (sort-by :dt t/<)
       first
       :idx))

(defn- get-next-date-state [windows readers state]
  (let [idx-next (get-next-index state)
        dt (get @state idx-next)
        window (get windows idx-next)
        ;_ (println "idx-next: " idx-next "dt: " dt)    
        dt-next (get-next-date readers idx-next)]
    ;(println "idx-next: " idx-next "dt: " dt " dt-next: " dt-next)
    (swap! state assoc idx-next dt-next)
    {:calendar window
     :time dt}))

(defn- calendar-seq-combined [start-dt windows]
  (let [readers (create-readers start-dt windows)
        state (create-initial-state readers)
        next (fn []
               (get-next-date-state windows readers state))]
    (repeatedly next)))

(defn- end-dates [end-dt windows]
  (map #(cal/next-close (first %) (last %) end-dt) windows))

(defn combined-event-seq
  [{:keys [start end]} windows]
  (let [windows (into [] windows) ; windows needs to be a vector
        end-dts (end-dates end windows)
        end-dt-max (apply t/max end-dts)
        seq (calendar-seq-combined start windows)
        not-end?  (fn [{:keys [window time]}]
                    (t/<= time end-dt-max))]
    (take-while not-end? seq)
     ;end-dt-max
    ))

(comment

  ;; window
  (require '[ta.calendar.window :refer [recent-days-window]])

  (def r (cal/trailing-range [:us :d] 10))
  r

;; reader

  (def reader (calendar-seq-reader r [:us :d]))
  ; reader will return with each call the next date
  (reader)

  (def calendars [[:crypto :h]
                  [:crypto :m]])

  (end-dates (:start days10) calendars)
  (combined-event-seq days10 calendars)

  (def readers (create-readers (:start days10) calendars))

  (count readers)
  (get readers 0)
  (get readers 1)
  (get-next-date readers 0)
  (get-next-date readers 1)

;; combiner test 
  (def state (create-initial-state readers))
  @state

  (get @state 0)
  (get @state 1)
  (get-next-index state)

  (get-next-date-state calendars readers state)

  ;; combiner

  (def c (calendar-seq-combined (:start days10) calendars))

  (take 1 c)

  (require '[clojure.pprint :refer [print-table]])
  (->> (take 10 c)
       (print-table))

  (combined-event-seq r calendars)

  (->> (combined-event-seq r calendars)
       (print-table))

;  
  )

