(ns ta.calendar.align
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype.argops :as argops]
   [tech.v3.tensor :as dtt]))

(defn align-to-calendar 
  "aligns ds-bars to a calendar.
   missing bars will have empty values."
  [calendar ds-bars]
  (-> (tc/left-join calendar ds-bars :date)
      (tc/order-by [:date] [:asc])
      (tc/set-dataset-name (-> ds-bars meta :name))))

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
                                     [(dec idx)]))
                        )
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



