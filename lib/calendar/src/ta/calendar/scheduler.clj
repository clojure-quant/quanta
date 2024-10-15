(ns ta.calendar.scheduler
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [chime.core :as chime]
   [ta.calendar.core :refer [calendar-seq-instant]]))

(defn- log-finished []
  (warn "scheduler chime Schedule finished!"))

(defn- log-error [ex]
  (error "scheduler chime exception: " ex)
  true ; continue schedule, if one scheduled fn throws an exception.
  )

(defn start [{:keys [calendar future-only?]
              :or {future-only? true}} f]
  (let [[calendar-kw interval-kw] calendar
        _ (info "scheduler start: " calendar-kw interval-kw "future-only: " future-only?)
        date-seq (calendar-seq-instant [calendar-kw interval-kw])
        date-seq (if future-only?
                   (rest date-seq)
                   date-seq)]
    (info "next start date: " (first date-seq))
    (chime/chime-at
     date-seq f
     {:on-finished log-finished
      :error-handler log-error})))

(def scheduler-state
  (atom {}))

(defn- has-category? [category]
  (contains? @scheduler-state category))

(defn- create-category [category]
  (swap! scheduler-state assoc category '()))

(defn- add-fn-to-category [category scheduled-fn]
  (swap! scheduler-state update-in [category] conj scheduled-fn))

(comment
  @scheduler-state
  (reset! scheduler-state {})
  (create-category [:us :m])
  (has-category? [:us :m])
  (has-category? [:us :h])
  (add-fn-to-category [:us :m] "test1")
  (add-fn-to-category [:us :m] "test2")
  (add-fn-to-category [:us :m] "test3")
  (create-category [:us :h])
  (add-fn-to-category [:us :h] "algo1")
  (add-fn-to-category [:us :h] "algo2")
  (add-fn-to-category [:us :h] "algo3")
  ;
  )

(defn run-fn-safe [time scheduled-fn]
  (try
    (scheduled-fn time)
    (catch Exception ex
      (error "scheduled time: " time "exception: " ex))))

(defn execute-category [category time]
  (when-let [category-data (get @scheduler-state category)]
    (doall (map (partial run-fn-safe time) category-data))))

(defn schedule-fn [{:keys [calendar future-only?]
                    :or {future-only? true} :as opts} scheduled-fn]
  (when-not (has-category? calendar)
    (create-category calendar)
    (start opts (partial execute-category calendar)))
  (add-fn-to-category calendar scheduled-fn))

(comment
  (defn print [title]
    (fn [dt]
      (info "calendar event " title  " @: " dt)))

  (start {:calendar [:us :m]} (print "*minute*"))
  (start [:us :h] (print "*hour*"))
  (start [:us :day] (print "*day*"))

  (defn print-time [time]
    (info "scheduled fn: print time: **** " time " *** "))

  (schedule-fn {:calendar [:us :m]} print-time)

  (schedule-fn [:us :h] print-time)

 ; 
  )