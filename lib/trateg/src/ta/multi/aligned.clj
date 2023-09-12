(ns ta.multi.aligned
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.datatype.argops :as argops]
   [tech.v3.tensor :as dtt]
   [ta.warehouse :as wh]
   [ta.data.settings :refer [determine-wh]]))

(defn filter-range [ds-bars {:keys [start end]}]
  (tc/select-rows
   ds-bars
   (fn [row]
     (let [date (:date row)]
       (and
        (or (not start) (t/>= date start))
        (or (not end) (t/<= date end)))))))

(defn load-symbol-full [symbol interval]
  (let [w (determine-wh symbol)
        ds-bars (wh/load-symbol w interval symbol)]
    ds-bars))

(defn load-symbol-window [symbol interval date-start date-end]
  (let [w (determine-wh symbol)
        ds-bars (wh/load-symbol w interval symbol)]
    (-> ds-bars
        (filter-range {:start date-start :end date-end}))))

(defn align-to-calendar [calendar bars]
  (-> (tc/left-join calendar bars :date)
      (tc/order-by [:date] [:asc])
      (tc/set-dataset-name (-> bars meta :name))))

(defn load-series [symbol {:keys [interval start end calendar]}]
  (let [w (determine-wh symbol)
        has-series? (wh/exists-symbol? w interval symbol)]
    (when has-series?
        (let [ds-bars (load-symbol-window symbol interval start end)
              ds-bars (tc/drop-columns ds-bars [:symbol])
              ds-bars-aligned (align-to-calendar calendar ds-bars)]
      ds-bars-aligned))))

(defn set-col! [ds col idx val]
  ;(println "set-close! idx: " idx " val: " val)
  ;(println "close: " (col ds))
  (dtt/mset! (dtt/select (col ds) idx) [val]))

(defn vec-const [size val]
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

(defn load-aligned-filled [symbol calendar]
   (when-let [ds-bars (load-series symbol calendar)]
     (fill-missing-close ds-bars)))


(comment
  (require '[ta.helper.date :refer [parse-date]])
  (require '[tech.v3.dataset.print :refer [print-range]])
  (require '[ta.multi.calendar :refer [daily-calendar]])

  (load-symbol-full "GOOGL" "D")
  (meta (load-symbol-full "GOOGL" "D"))

  (def calendar 
    (daily-calendar (parse-date "2023-09-01")
                    (parse-date "2023-10-01")))

  (load-symbol-window "GOOGL" "D" (parse-date "2023-09-01")
                                  (parse-date "2023-10-01"))
  (load-series "GOOGL" calendar)

  (load-series "DAX0" calendar)


  (-> (load-series "GOOGL" calendar)
      :GOOGL.date
      ;meta
      )

 ;:left-outer-join.date
  
  (def ds (-> (load-series "GOOGL" calendar)
              fill-missing-close))
  
  (keys (tc/columns ds :as-map))
  
   (print-range ds :all )
  
   (set-col! ds :close [1 11] 9.9)
   (set-col! ds :close [10 11] 9.9)
   (print-range ds :all)
  
   (dtt/select (:close ds) [0 1 2 3 4 5 19])

   (dtt/mset! (dtt/select (col ds) idx) [val])

  (dtt/mget (:close ds) [10])

  (-> (load-series "GOOGL" calendar)
      fill-missing-close
      ;meta
      ;(:close )
     ; (:GOOGL.date)
      (print-range :all)
      )
   (load-aligned-filled "GOOGL" calendar)


 ; 
  )