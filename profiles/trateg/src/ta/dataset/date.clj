(ns ta.dataset.date
  (:require
   [tick.alpha.api :as tick]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.datetime :as datetime]
   [tablecloth.api :as tablecloth]
   [ta.data.date :as dt]))

(defn days-ago [n]
  (-> (tick/now)
      (tick/- (tick/new-duration n :days))
      (tick/date)))

(defn days-ago-instant [n]
  (-> (tick/now)
      (tick/- (tick/new-duration n :days))))

(defn ds-epoch [ds]
  (dataset/column-map ds :epoch #(* 1000 (dt/->epoch %)) [:date]))

(defn select-rows-since [ds date]
  (-> ds
      (tablecloth/select-rows
       (fn [row]
         (-> row
             :date
             (tick/>= date))))))

(defn add-year-and-month-date-as-local-date [ds]
  (-> ds
      (tablecloth/add-columns
       {:year  #(->> %
                     :date
                     (datetime/long-temporal-field :years))
        :month #(->> %
                     :date
                     (datetime/long-temporal-field :months))})))

(defn year [v]
  (let [n (count v)]
    ;(println "year vec count: " n " v: " v)
    (dtype/clone
     (dtype/make-reader
      :object
      n
      (tick/year (v idx))))))

(defn month [v]
  (let [n (count v)]
    ;(println "year vec count: " n " v: " v)
    (dtype/clone
     (dtype/make-reader
      :object
      n
      (tick/month (v idx))))))

(defn add-year-and-month-date-as-instant [ds]
  (-> ds
      (tablecloth/add-columns
       {:year  (year (:date ds))
        :month (month (:date ds))})))

; (require '[clj-time.coerce])
; (clj-time.coerce/to-long dt)

