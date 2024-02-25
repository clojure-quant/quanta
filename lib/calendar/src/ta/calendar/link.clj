(ns ta.calendar.link
 (:require
  [tick.core :as t]
  [tablecloth.api :as tc]
  [tech.v3.datatype :as dtype]))

(defn make-aligner [ds col v]
  (let [m (dec (tc/row-count ds))
        c (atom 0)
        move-next (fn [] (swap! c inc))
        date-current (fn [] (-> ds :date (get (min @c m))))
        date-next    (fn [] (-> ds :date (get (min m (inc @c)))))
        get-val (fn [] (-> ds col (get (min @c m))))]
    (fn [date]
      ; todo: make it better. 
      ; in cases where move-next would need to be called, it will 
      ; just move-next once. really we want to recursively call our function.
      ; but this is difficult since we already have created a lot of state.
      (if (t/> (date-current) date)
        v
        (do
          (when (t/<= (date-next)  date)
            (move-next))
          (get-val))))))

  (defn col-type [ds col]
    (-> ds col meta :datatype))

  (defn link-bars 
    "returns timeseries values form remote-col, aligned to a size of bar-ds.
     both bar-ds and remote-ds need to be datasets with :date column.
     alignment rule is: last remote value is shown, except when remote value,
     does not have a value yet, in case nil-val is returned.
     Useful to link to remote time-series that are of lower frequency."
    [bar-ds remote-ds remote-col nil-val]
    (let [align (make-aligner remote-ds remote-col nil-val)
          t (col-type remote-ds remote-col)]
      (println "link-bars type: " t)
      (dtype/emap align t (:date bar-ds))))
      

(comment 
  (def daily-ds (tc/dataset [{:date (t/date-time "2024-01-01T17:00:00") :a 1}
                             {:date (t/date-time "2024-01-02T17:00:00") :a 2}
                             {:date (t/date-time "2024-01-03T17:00:00") :a 3}]))
  (col-type daily-ds :a)
  (-> daily-ds :a (get 2))
  (def hour-ds (tc/dataset [{:date (t/date-time "2024-01-01T15:00:00") } ; 0
                            {:date (t/date-time "2024-01-01T16:00:00") } ; 0
                            {:date (t/date-time "2024-01-01T17:00:00") } ; 1
                            {:date (t/date-time "2024-01-02T09:00:00") } ; 1
                            {:date (t/date-time "2024-01-02T16:00:00") } ; 1
                            {:date (t/date-time "2024-01-02T17:00:00") } ; 2
                            ]))
  
  (link-bars hour-ds daily-ds :a 0)
  ;; => [0 0 1 1 1 2]


 ; 
  )

