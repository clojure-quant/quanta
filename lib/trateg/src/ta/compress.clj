(ns ta.compress
  (:require
   [tick.core :as tick]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [tablecloth.api :as tc]
   [ta.helper.date-ds :as h]))

 (def midnight (tick/time "00:00:00"))
 
;; MONTH GROUP
 
(defn- year-month->date [year month]
 (->
   (tick/new-date year month 1)
   (.plusMonths 1)
   (.plusDays -1)
   (tick/at midnight)))
  
(defn- year-month->date-col [ds]
  (let [year (:year ds)
        month (:month ds)]
  (dtype/make-reader 
   :local-date-time
   (tc/row-count ds) 
   (year-month->date (get year idx) (get month idx)))))

(defn add-date-group-col-month [ds]
  (let [ds-year-month (h/add-year-and-month-date-as-local-date ds)
        date-group-col (year-month->date-col ds-year-month)]
    (tc/add-column ds-year-month :date-group date-group-col)))

;; YEAR

(defn- year->date [year]
  (->
   (tick/new-date year 1 1)
   (.plusYears 1)
   (.plusDays -1)
   (tick/at midnight)))

(defn- year->date-col [ds]
  (let [year (:year ds)]
    (dtype/make-reader
     :local-date-time
     (tc/row-count ds)
     (year->date (get year idx)))))

(defn add-date-group-col-year [ds]
  (let [ds-year-month (h/add-year-and-month-date-as-local-date ds)
        date-group-col (year->date-col ds-year-month)]
    (tc/add-column ds-year-month :date-group date-group-col)))


;; COMPRESS

(defn compress-ds [add-date-group-col ds] 
  (->
    ds
   (add-date-group-col)
   (tc/group-by [:date-group])
   (tc/aggregate {:open (fn [ds]
                          (-> ds :open first ))
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
                                 (count)))
                  })
     (tc/rename-columns {:date-group :date})             
                  ))

(comment 
  
  (year-month->date 2021 04)

  (require '[ta.helper.date :refer [parse-date]])
  (def ds (tc/dataset [{:date (parse-date "2021-01-01")
                        :open 100
                        :high 110
                        :low 90
                        :close 105
                        :volume 100}
                       {:date (parse-date "2021-01-02")
                        :open 106
                        :high 115
                        :low 101
                        :close 109
                        :volume 100}
                       {:date (parse-date "2021-02-01")
                        :open 110
                        :high 121
                        :low 105
                        :close 116
                        :volume 100}
                       ]))
  ds

  (compress-ds add-date-group-col-month ds)
  (compress-ds add-date-group-col-year ds) 

  (require '[ta.warehouse :as wh])
 
  (def btc (wh/load-series {:symbol "BTCUSD"
                            :frequency "D"
                            :warehouse :crypto}))
  
  (compress-ds add-date-group-col-year btc) 
 
  
 ; 
    )

