(ns ta.indicator.date
  (:require
   [tech.v3.dataset :as tds]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.datatype.datetime :as datetime]
   [tablecloth.api :as tc]
   [tick.core :as t]))

(defn year [v]
  (let [n (count v)]
    ;(println "year vec count: " n " v: " v)
    (dtype/clone
     (dtype/make-reader
      :int32
      n
      (-> (t/year (v idx)) .getValue)))))

(defn add-year [ds]
  (tc/add-column ds :year (year (:date ds))))

(defn month [v]
  (let [n (count v)]
    ;(println "year vec count: " n " v: " v)
    (dtype/clone
     (dtype/make-reader
      :int32
      n
      (-> (t/month (v idx)) .getValue)))))

(defn add-month [ds]
  (tc/add-column ds :month (month (:date ds))))

(defn add-year-month [ds]
  (tc/add-column ds :year-month (dfn/+ (dfn/* 100 (year (:date ds)))
                                       (month (:date ds)))))

(defn day-of-month [v]
  (let [n (count v)]
    ;(println "year vec count: " n " v: " v)
    (dtype/clone
     (dtype/make-reader
      :int32
      n
      (t/day-of-month (v idx))))))

(defn add-day [ds]
  (tc/add-column ds :day (day-of-month (:date ds))))

(defn add-year-month-day [ds]
  (tc/add-column ds :year-month-day (dfn/+ (dfn/* 10000 (year (:date ds)))
                                           (dfn/* 100 (month (:date ds)))
                                           (day-of-month (:date ds)))))

(defn select-rows-since [ds date]
  (-> ds
      (tc/select-rows
       (fn [row]
         (-> row
             :date
             (t/>= date))))))

(defn select-rows-interval [ds date-start date-end]
  (tc/select-rows ds
                  (fn [row]
                    (let [date (:date row)]
                      (and (t/>= date date-start)
                           (t/<= date date-end))))))

(comment

  (-> (tc/dataset {:date (t/instant)})
      add-year
      add-month
      add-day)

;  
  )
