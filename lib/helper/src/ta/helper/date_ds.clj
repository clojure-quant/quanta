(ns ta.helper.date-ds
  (:require
   [tech.v3.dataset :as tds]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.datatype.datetime :as datetime]
   [tablecloth.api :as tc]
   [tick.core :as t]
   ;[ta.helper.date :as dt]
   [ta.helper.ds :refer [cols-of-type]]))

(defn now []
  (-> (t/now)
      (t/date-time)))

(defn days-ago [n]
  (-> (t/now)
      (t/date-time)
      (t/<< (t/new-duration n :days))))

(defn days-ago-instant [n]
  (-> (t/now)
      (t/- (t/new-duration n :days))))

;(defn ds-epoch [ds]
;  (tds/column-map ds :epoch #(* 1000 (dt/->epoch-second %)) [:date]))

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

  (->  (tc/dataset {:date [(t/new-date 2021 10 28)
                           (t/new-date 2021 10 29)
                           (t/new-date 2021 11 01)
                           (t/new-date 2021 11 02)]})
       (add-year-and-month-date-as-local-date))
;
  )

; (require '[clj-time.coerce])
; (clj-time.coerce/to-long dt)

(defn ensure-roundtrip-date-localdatetime [ds]
  (let [t (->> ds tc/columns (map meta) (filter #(= (:name %) :date-close)) first :datatype)]
    (if (= t :packed-instant)
      (do (println "converting to local-datetime")
          (-> ds
              (tds/column-map :date-close #(t/date-time %) [:date-close])
              (tds/column-map :date-open #(t/date-time %) [:date-open])))

      (do (println "already local-date")
          ds))))

(defn convert-col-instant->localdatetime [ds col]
  (println "converting col " col "to local-datetime")
  (tds/column-map ds col #(t/date-time %) [col]))

(defn ds-convert-col-instant->localdatetime [ds]
  (let [cols (cols-of-type ds :packed-instant)]
    (println "converting cols: " cols)
    (reduce convert-col-instant->localdatetime ds cols)))

(defn month-as-int [dt]
  (-> dt t/month .getValue))

(comment

  (days-ago 50)

  (-> (month-as-int (t/now)) class)
  ;  
  )

(defn month-begin? [date-vec]
  (let [l (count date-vec)]
    (dtype/make-reader :boolean l
                       (if (> idx 0)
                         (let [m-cur (-> (date-vec idx) month-as-int)
                               m-prior (-> (date-vec (dec idx)) month-as-int)]
                           (not (= m-cur m-prior)))
                         false))))

(defn month-end? [date-vec]
  (let [l (count date-vec)
        idx-max (dec l)]
    (dtype/make-reader :boolean l
                       (if (< idx idx-max)
                         (let [m-cur (-> (date-vec idx) month-as-int)
                               m-next (-> (date-vec (inc idx)) month-as-int)]
                           (not (= m-next m-cur)))
                         false))))

(comment
  (month-end? [(t/now)])

  (month-begin? [(t/new-date 2021 10 28)
                 (t/new-date 2021 10 29)
                 (t/new-date 2021 11 01)
                 (t/new-date 2021 11 02)])

  (month-end? [(t/new-date 2021 10 28)
               (t/new-date 2021 10 29)
               (t/new-date 2021 11 01)
               (t/new-date 2021 11 02)])

  (month-end? [(t/new-date 2021 12 28)
               (t/new-date 2021 12 29)
               (t/new-date 2022  1 01)
               (t/new-date 2022  1 02)])

;
  )
(comment
  (-> (t/now)
      (t/year)
      .getValue
      class)

  (into [] (month [(t/now)]))
  (into [] (year [(t/now)]))

  (-> (t/now)
      (t/date-time)

      class)

  (->> (tc/dataset [{:date-close (t/now)
                     :date-open (t/now)}])
       ensure-roundtrip-date-localdatetime
       ;tc/columns
       ;(map meta)
       )

  (->> (tc/dataset [{:date-close (t/now)
                     :date-open (t/now)
                     :bongo 3
                     :signal true}])
       ds-convert-col-instant->localdatetime
       tc/columns
       (map meta))

; 
  )
