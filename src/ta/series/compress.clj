(ns ta.series.compress
  (:require
   [clj-time.core :as t]
   [taoensso.tufte :as tufte :refer (defnp p profiled profile)]))

(defn- apply-safe [fun list]
  (let [list-not-nil (remove nil? list)]
    (if (empty? list-not-nil) nil (apply fun list-not-nil))))

(defn- group-summary-
  "aggregates series elements in a group 
  (example: array daily elements => month summary [open/high/low/close/volume)"
  [group]
  (let [elements (get group 1)
        open (:open (first elements))
        close (:close (last elements))
        ;close-adj (:close-adj (last elements))
        high  (apply-safe max (map :high elements))
        low   (apply-safe min (map :low elements))
        volume (reduce + (remove nil? (map :volume elements)))
        count (count elements)
        compressed-bar {:open open
                        :high high
                        :low low
                        :close close
                        :volume volume
                        :count count}]

    compressed-bar
    ;(if (nil? close-adj)
    ;  compressed-bar
    ;  (assoc compressed-bar :close-adj close-adj))
    ))

(defn- compress-group-
  "compresses a group (date:end of group, open/high/low/close/volume"
  [grouper group]
  (let [summary (group-summary- group)
        summary-with-date (assoc summary :date ((:date-fn grouper) (get group 0)))]
    summary-with-date))

(defn compress
  "compresses daily->monthly series.
   series has to be sorted by date
   grouper defines how to make groups"
  [grouper series]
  (p :compress-ts
     (let [; via group-by
           ;groups (p :compress-make-group (group-by (:group-fn grouper) series ))

           ; via partition-by
           groups (p :compress-make-partition (doall (partition-by (:group-int grouper) series)))
           groups (p :compress-sum-partition (map #(conj [] ((:group-fn grouper) (first %)) %) groups))

           ; partially applied function:
           process-group  (partial compress-group- grouper)]

       (p :compress-sum-group
          (->> (map process-group groups)
               (sort-by :date)
               (vec))))))

(def year
  (memoize t/year))

(def month
  (memoize t/month))

; group by month (group algo, and algo to determine end date)
(def group-month
  {:group-int (fn [series-element]
                (let [date (:date series-element)]
                  (+ (* 100 (t/year date)) (t/month date))))

   :group-fn (fn [series-element]
               (let [date (:date series-element)]
                 (assoc {} :year (t/year date) :month (t/month date))))
   :date-fn (fn [group] (->
                         (t/date-time (:year group) (:month group) 1)
                         (.plusMonths 1)
                         (.plusDays -1)))})

(comment ; ***********************************************************************

  (def dt (t/date-time 1986 10 14))
  (.plusDays dt -1)
  ((:date-fn group-month) {:month 2 :year 2018})
  (apply max (remove nil? [1 2 3 nil]))




 ; comment end
  )
