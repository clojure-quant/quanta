(ns ta.warehouse.overview
  (:require
   [tech.v3.dataset.print :as print]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype.datetime :as datetime]
   [tech.v3.datatype :as dtype]
   [tablecloth.api :as tablecloth]
   [ta.warehouse :as wh]
   [ ta.backtest.date :refer [add-year-and-month-date-as-instant]]))

(defn load-datasets [w frequency symbols]
  (->> symbols
       (map (fn [symbol]
              (-> (wh/load-symbol w frequency symbol)
                  (tablecloth/add-column :symbol symbol)
                  ;add-year-and-month-date-as-instant
                  #_(tablecloth/add-column :return #(-> %
                                                        :close
                                                        returns)))))))

(defn concatenate-datasets [seq-ds-bar]
  (->> seq-ds-bar
       (apply tablecloth/concat)))

(defn dataset->symbol [ds]
  (-> ds :symbol first))

(defn overview-view [ds-concatenated
                     {:keys [grouping-columns pivot?]
                      :or {grouping-columns [:symbol]
                           pivot? false}}]
  (-> ds-concatenated
      (tablecloth/group-by grouping-columns)
      (tablecloth/aggregate {:count tablecloth/row-count
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
         #(tablecloth/pivot->wider % :symbol [:min :max :count])
         identity))))

(defn warehouse-overview [w frequency & options]
  (let [options (if options options {})
        symbols (wh/symbols-available w frequency)
        datasets (load-datasets w frequency symbols)]
    (-> datasets
        concatenate-datasets
        (overview-view options))))

(comment

  (def w {:series "../db/crypto/"})
  (def w {:series "../db/random/"})

  (wh/symbols-available w "D")

  (warehouse-overview w "D")
  (warehouse-overview w "15")

  (warehouse-overview  "D")

;  
  )