(ns ta.warehouse.overview
  (:require
   [tablecloth.api :as tc]
   [ta.helper.ds :refer [ds->map show-meta cols-of-type]]
   [ta.helper.date-ds :refer [ds-convert-col-instant->localdatetime]]
   [ta.warehouse :as wh]))

(defn load-datasets [w frequency symbols]
  (->> symbols
       (map (fn [symbol]
              (-> (wh/load-symbol w frequency symbol)
                  (tc/add-column :symbol symbol)
                  (tc/drop-columns [:volume])
                  )))))

(defn concatenate-datasets [seq-ds-bar]
  (if (empty? seq-ds-bar)
      nil
    (->> seq-ds-bar
         (apply tc/concat))))

(defn overview-view [ds-concatenated
                     {:keys [grouping-columns pivot?]
                      :or {grouping-columns [:symbol]
                           pivot? false}}]
  (when ds-concatenated
  (-> ds-concatenated
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

(defn warehouse-overview [w frequency & options]
  (let [options (if options options {})
        symbols (wh/symbols-available w frequency)
        datasets (load-datasets w frequency symbols)]
    (-> datasets
        concatenate-datasets
        (overview-view options))))

(defn overview-map [w f]
  (let [ds-overview (warehouse-overview w f)
        m (-> ds-overview
              ds-convert-col-instant->localdatetime
              ds->map)]
    ;(println "overview-types: " (show-meta ds-overview))
    ;(println "overview type packet-instant" (cols-of-type ds-overview :packed-instant))
    ;(println "overview-map: " m)
    m))


(comment

  (wh/symbols-available :stocks "D")
  (wh/symbols-available :crypto "D")

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
