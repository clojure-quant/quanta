(ns ta.backtest.date
  (:require
   [tick.alpha.api :as tick]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.datetime :as datetime]
   [tablecloth.api :as tc]
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
      (tc/select-rows
       (fn [row]
         (-> row
             :date
             (tick/>= date))))))

(defn add-year-and-month-date-as-local-date [ds]
  (-> ds
      (tc/add-columns
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
      :int32
      n
      (-> (tick/year (v idx)) .getValue)))))

(defn month [v]
  (let [n (count v)]
    ;(println "year vec count: " n " v: " v)
    (dtype/clone
     (dtype/make-reader
      :int32
      n
      (-> (tick/month (v idx)) .getValue)))))

(defn add-year-and-month-date-as-instant [ds]
  (-> ds
      (tc/add-columns
       {:year  (year (:date ds))
        :month (month (:date ds))})))

; (require '[clj-time.coerce])
; (clj-time.coerce/to-long dt)

(defn ensure-roundtrip-date-localdatetime [ds]
  (let [t (->> ds tc/columns (map meta) (filter #(= (:name %) :date-close)) first :datatype)]
    (if (= t :packed-instant)
      (do (println "converting to local-datetime")
          (-> ds
              (dataset/column-map :date-close #(tick/date-time %) [:date-close])
              (dataset/column-map :date-open #(tick/date-time %) [:date-open])))

      (do (println "already local-date")
          ds))))

(comment
  (-> (tick/now)
      (tick/year)
      .getValue
      class)

  (into [] (month [(tick/now)]))
  (into [] (year [(tick/now)]))

  (-> (tick/now)
      (tick/date-time)

      class)

  (->> (tc/dataset [{:date-close (tick/now)
                     :date-open (tick/now)}])
       ensure-roundtrip-date-localdatetime
       ;tc/columns
       ;(map meta)
       )
; 
  )
