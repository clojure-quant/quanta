(ns ta.db.bars.overview
  (:require
   [ta.db.bars.protocol :as b]
   [tablecloth.api :as tc]))

(defn- load-datasets [db opts window assets]
  (->> assets
       (map (fn [asset]
              (-> (b/get-bars db (assoc opts :asset asset) window)
                  (tc/add-column :asset asset)
                  (tc/drop-columns [:volume]))))))

(defn- concatenate-datasets [seq-ds-bar]
  (if (empty? seq-ds-bar)
    nil
    (->> seq-ds-bar
         (apply tc/concat))))

(defn- overview-view [concatenated-ds
                      {:keys [grouping-columns pivot?]
                       :or {grouping-columns [:symbol]
                            pivot? false}}]
  (when concatenated-ds
    (-> concatenated-ds
        (tc/group-by grouping-columns)
        (tc/aggregate {:count tc/row-count
                       :first-date (fn [ds]
                                     (->> ds
                                          :date
                                          first))
                       :last-date (fn [ds]
                                    (->> ds
                                         :date
                                         last))
                       :min (fn [ds]
                              (->> ds
                                   :close
                                   (apply min)))
                       :max (fn [ds]
                              (->> ds
                                   :close
                                   (apply max)))})
        ((if pivot?
           #(tc/pivot->wider % :symbol [:min :max :count])
           identity)))))

(defn warehouse-overview [db opts window assets & options]
  (let [options (if options options {})
        datasets (load-datasets db opts window assets)]
    (-> datasets
        concatenate-datasets
        (overview-view options))))

; [ta.helper.ds :refer [ds->map show-meta cols-of-type]]
;   [ta.helper.date-ds :refer [ds-convert-col-instant->localdatetime]]
;
;(defn overview-map [w f]
;  (let [ds-overview (warehouse-overview w f)
;        m (-> ds-overview
;              ds-convert-col-instant->localdatetime
;              ds->map)]
;    ;(println "overview-types: " (show-meta ds-overview))
;    ;(println "overview type packet-instant" (cols-of-type ds-overview :packed-instant))
;    ;(println "overview-map: " m)
;    m))

(comment
  (load-datasets :crypto "D" (wh/symbols-available :crypto "D"))
  (load-datasets :stocks "D" (wh/symbols-available :stocks "D"))

  (warehouse-overview :stocks "D")
  (warehouse-overview :crypto "D")
  (warehouse-overview :crypto "15")

  (wh/symbols-available :shuffled "D")

  (-> ;(wh/load-symbol :crypto "D" "BTCUSD")
   (wh/load-symbol :stocks "D" "MSFT")
   :date
   meta
   :datatype)
   ; crypto is instant

;
  )
