(ns ta.calendar.align
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype.argops :as argops]
   [tech.v3.tensor :as dtt]))

(defn align-to-calendar
  "aligns ds-bars to a calendar.
   missing bars will have empty values."
  [calendar-ds bars-ds]
  ;(info "bars-ds: " bars-ds)
  ;(info "bars-ds info" (tc/info bars-ds))
  ;(info "calendar-ds: " calendar-ds)
  ;(info "calendar-ds info: " (tc/info calendar-ds))
  (let [bars-ds-unique (tc/unique-by bars-ds :date)] ; todo: fix importer so duplicates are avoided.
    (-> (tc/left-join calendar-ds bars-ds-unique :date)
        (tc/order-by [:date] [:asc])
        (tc/set-dataset-name (-> bars-ds meta :name)))))

(defn- set-col! [ds col idx val]
  ;(println "set-close! idx: " idx " val: " val)
  ;(println "close: " (col ds))
  (dtt/mset! (dtt/select (col ds) idx) [val]))

(defn- vec-const [size val]
  (vec (repeat size val)))

(defn fill-missing-close [ds-bars-aligned]
  (let [close2 (vec-const (count (:close ds-bars-aligned)) 0.0)
        ds-bars-aligned (tc/add-columns ds-bars-aligned {:close2 close2})
        series-symbol (-> ds-bars-aligned meta :name)
        col-date-symbol (keyword (str series-symbol ".date"))
        idxs-existing (argops/argfilter
                       identity
                       (col-date-symbol ds-bars-aligned))
        idxs-missing (argops/argfilter
                      #(not %)
                      (col-date-symbol ds-bars-aligned))
        get-close-idx (fn [idx]
                        (if (= idx 0) 0.0
                            (-> ds-bars-aligned :close2 (nth (dec idx))))
                        #_(first
                           (dtt/select (:close2 ds-bars-aligned)
                                       [(dec idx)])))
        ; (-> ds-bars-aligned :close (nth idx)])
        ]
    ;(println "size close2: " (count close2))
    ;(println "col-date-symbol: " col-date-symbol)
    ; copy existing close values to close2
    (dtt/mset!
     (dtt/select (:close2 ds-bars-aligned) idxs-existing)
     (dtt/select (:close ds-bars-aligned) idxs-existing))
    ; roll forward missing close values
    (doall
     (for [idx idxs-missing]
       (set-col! ds-bars-aligned :close2
                 [idx]
                 (get-close-idx idx))))
    (-> ds-bars-aligned
        (tc/drop-columns [:close col-date-symbol])
        (tc/rename-columns {:close2 :close}))))

(comment

  (def ds-cal (tc/dataset {:date [1 2 3 4]}))
  (def ds-bars (tc/dataset {:date [0 1 2 2 3 5 6 7 8 8]
                            :low [0.0 0.1 0.2 0.22 0.3 0.5 0.6 0.7 0.8 0.88]
                            :close [0 1 2 2.2 3 5 6 7 8 8.8]}))

  (tc/unique-by ds-bars :date)

  (defn align-to-calendar2 [ds-bars ds-cal]
    (align-to-calendar ds-cal ds-bars))

  (-> ds-bars
      (tc/set-dataset-name "MSFT")
      (align-to-calendar2 ds-cal)
      (fill-missing-close))

;  
  )



