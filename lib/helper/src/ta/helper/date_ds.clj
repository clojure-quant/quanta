(ns ta.helper.date-ds
  (:require
   [tech.v3.dataset :as tds]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.datatype.datetime :as datetime]
   [tablecloth.api :as tc]
   [tick.core :as tick]
   [ta.helper.date :as dt]
   [ta.helper.ds :refer [cols-of-type]]))

(defn days-ago [n]
  (-> (tick/now)
      (tick/date-time)
      (tick/<< (tick/new-duration n :days))))

(defn days-ago-instant [n]
  (-> (tick/now)
      (tick/- (tick/new-duration n :days))))

(defn ds-epoch [ds]
  (tds/column-map ds :epoch #(* 1000 (dt/->epoch-second %)) [:date]))

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
                     (datetime/long-temporal-field :months))})
      (tc/add-column :year-month (fn [ds]
                                   (-> (dfn/* (:year ds) 100)
                                       (dfn/+ (:month ds)))))))

(comment

  (->  (tc/dataset {:date [(tick/new-date 2021 10 28)
                           (tick/new-date 2021 10 29)
                           (tick/new-date 2021 11 01)
                           (tick/new-date 2021 11 02)]})
       (add-year-and-month-date-as-local-date))
;
  )
(defn year [v]
  (let [n (count v)]
    ;(println "year vec count: " n " v: " v)
    (dtype/clone
     (dtype/make-reader
      :int32
      n
      (-> (tick/year (v idx)) .getValue)))))

(defn day-of-month [v]
  (let [n (count v)]
    ;(println "year vec count: " n " v: " v)
    (dtype/clone
     (dtype/make-reader
      :int32
      n
      (tick/day-of-month (v idx))))))

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
              (tds/column-map :date-close #(tick/date-time %) [:date-close])
              (tds/column-map :date-open #(tick/date-time %) [:date-open])))

      (do (println "already local-date")
          ds))))

(defn convert-col-instant->localdatetime [ds col]
  (println "converting col " col "to local-datetime")
  (tds/column-map ds col #(tick/date-time %) [col]))

(defn ds-convert-col-instant->localdatetime [ds]
  (let [cols (cols-of-type ds :packed-instant)]
    (println "converting cols: " cols)
    (reduce convert-col-instant->localdatetime ds cols)))

(defn month-as-int [dt]
  (-> dt tick/month .getValue))

(comment

  (days-ago 50)

  (-> (month-as-int (tick/now)) class)
  ;  
  )

(defn month-begin? [date-vec]
  (let [l (count date-vec)]
    (dtype/make-reader :bool l
                       (if (> idx 0)
                         (let [m-cur (-> (date-vec idx) month-as-int)
                               m-prior (-> (date-vec (dec idx)) month-as-int)]
                           (not (= m-cur m-prior)))
                         false))))

(defn month-end? [date-vec]
  (let [l (count date-vec)
        idx-max (dec l)]
    (dtype/make-reader :bool l
                       (if (< idx idx-max)
                         (let [m-cur (-> (date-vec idx) month-as-int)
                               m-next (-> (date-vec (inc idx)) month-as-int)]
                           (not (= m-next m-cur)))
                         false))))

(comment
  (month-end? [(tick/now)])

  (month-begin? [(tick/new-date 2021 10 28)
                 (tick/new-date 2021 10 29)
                 (tick/new-date 2021 11 01)
                 (tick/new-date 2021 11 02)])

  (month-end? [(tick/new-date 2021 10 28)
               (tick/new-date 2021 10 29)
               (tick/new-date 2021 11 01)
               (tick/new-date 2021 11 02)])

  (month-end? [(tick/new-date 2021 12 28)
               (tick/new-date 2021 12 29)
               (tick/new-date 2022  1 01)
               (tick/new-date 2022  1 02)])

;
  )
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

  (->> (tc/dataset [{:date-close (tick/now)
                     :date-open (tick/now)
                     :bongo 3
                     :signal true}])
       ds-convert-col-instant->localdatetime
       tc/columns
       (map meta))

; 
  )
