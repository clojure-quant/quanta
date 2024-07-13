(ns quanta.trade.position.exit.time
  (:require
   [tick.core :as t]
   [missionary.core :as m]
   [ta.calendar.core :refer [calendar-seq]]))

(defn get-exit-rule [algo-opts]
  (let [{:keys [exit]} algo-opts
        rule (->> exit
                  (partition 2)
                  (filter (fn [[rule rule-opts]]
                            (= rule :time)))
                  first)]
    (when rule
      (into [] rule))))

(defn get-time-bars [algo-opts]
  (let [[_ bars] (get-exit-rule algo-opts)]
    bars))

(defn get-exit-time [algo-opts entry-date]
  (let [{:keys [calendar]} algo-opts
        [exchange-kw interval-kw] calendar
        bars (get-time-bars algo-opts)]
    (when bars
      (let [cal-seq (calendar-seq exchange-kw interval-kw entry-date)
            window (take bars cal-seq)]
        #_{:start (first window)
           :end (last window)}
        (last window)))))

(defn time-trigger [exit-time]
  (let [exit-long (-> exit-time t/instant t/long)
        now-long (-> t/instant t/long)
        diff-ms (* 1000 (- exit-long now-long))
        diff-ms (max diff-ms 1)]
    (m/sleep diff-ms :time)))


(comment
  (get-exit-rule {:exit [:profit 2.0
                         :loss 0.3
                         :time 1]})
  ;; => [:time 1]

  (get-exit-rule {:exit [:profit 2.0
                         :loss 0.3]})
  ;; => nil

  
  (get-exit-time {:calendar [:crypto :m]
                  :exit [:profit 2.0
                         :loss 0.3
                         :time 10]} (t/instant))
 ;; => #time/zoned-date-time "2024-07-13T16:17Z[UTC]"

  (get-exit-time {:calendar [:crypto :m]
                  :exit [:profit 2.0
                         :loss 0.3
                         :time 70]} (t/instant))
  ;; => #time/zoned-date-time "2024-07-13T17:17Z[UTC]"

  (get-exit-time {:calendar [:crypto :m]
                  :exit [:profit 2.0
                         :loss 0.3]} (t/instant))
   ;; => nil

  
  (m/? (-> (get-exit-time {:calendar [:crypto :m]
                           :exit [:profit 2.0
                                  :loss 0.3
                                  :time 2]} (t/instant))
           (time-trigger)))
  ;; returns :time after 2 minutes.
  
; 
  )


