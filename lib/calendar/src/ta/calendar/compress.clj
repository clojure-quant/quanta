(ns ta.calendar.compress
  (:require
   [tick.core :as t]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [tablecloth.api :as tc]
   [ta.helper.date-ds :as h]))

(def midnight (t/time "00:00:00"))

;; MONTH 

(defn- year-month->date [year month]
  (-> (t/new-date year month 1)
      (.plusMonths 1)
      (.plusDays -1)
      (t/at midnight)
      (t/in "UTC")))

(defn month-end-date [dt]
  (let [y (-> dt t/year .getValue)
        m (-> dt t/month .getValue)]
    (year-month->date y m)))

(defn add-date-group-month [ds]
  (let [date-group-col (dtype/emap month-end-date :zoned-date-time (:date ds))]
    (tc/add-column ds :date-group date-group-col)))

;; YEAR

(defn- year->date [year]
  (->
   (t/new-date year 1 1)
   (.plusYears 1)
   (.plusDays -1)
   (t/at midnight)
   (t/in "UTC")))

(defn year-end-date [dt]
  (let [y (-> dt t/year .getValue)]
    (year->date y)))

(defn add-date-group-year [ds]
  (let [date-group-col (dtype/emap year-end-date :zoned-date-time (:date ds))]
    (tc/add-column ds :date-group date-group-col)))

; HOUR

(defn hour-end-date [dt]
  (let [h (-> dt t/hour .getValue)
        m (-> dt t/minute .getValue)]
    (if (= 0 m)
      dt
      (-> dt
          (t/with :minute-of-hour 0)
          (t/with :hour-of-day (inc h))))))

(defn add-date-group-hour [ds]
  (let [date-group-col (dtype/emap year-end-date :zoned-date-time (:date ds))]
    (tc/add-column ds :date-group date-group-col)))

;; COMPRESS

(defn compress-ds [grouped-ds]
  (->
   grouped-ds
   (tc/group-by [:date-group])
   (tc/aggregate {:open (fn [ds]
                          (-> ds :open first))
                  :high (fn [ds]
                          (->> ds
                               :high
                               (apply max)))
                  :low (fn [ds]
                         (->> ds
                              :low
                              (apply min)))
                  :close (fn [ds]
                           (-> ds :close last))
                  :volume (fn [ds]
                            (->> ds
                                 :volume
                                 (apply +)))
                  :count (fn [ds]
                           (->> ds
                                :close
                                (count)))})
   (tc/rename-columns {:date-group :date})))

(comment

  (year-month->date 2021 04)
  (month-end-date (t/instant))
  (month-end-date (t/instant "2023-01-01T15:30:00Z"))

  (def ds (tc/dataset [{:date (t/instant "2021-01-01T15:30:00Z")
                        :open 100
                        :high 110
                        :low 90
                        :close 105
                        :volume 100}
                       {:date (t/instant "2021-01-02T15:30:00Z")
                        :open 106
                        :high 115
                        :low 101
                        :close 109
                        :volume 100}
                       {:date (t/instant "2021-02-01T15:30:00Z")
                        :open 110
                        :high 121
                        :low 105
                        :close 116
                        :volume 100}]))
  ds

  (-> ds
      (add-date-group-year)
      (compress-ds))

  (-> ds
      (add-date-group-month)
      (compress-ds))

; 
  )

