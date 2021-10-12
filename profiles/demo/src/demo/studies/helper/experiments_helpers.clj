(ns demo.studies.helper.experiments-helpers
  (:require
   [tech.v3.dataset.print :as print]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype.datetime :as datetime]
   [tech.v3.datatype :as dtype]
   [tablecloth.api :as tablecloth]
   [ta.dataset.helper :as helper]))

(defn symbols-overview [concatenated-dataset
                        {:keys [grouping-columns pivot?]
                         :or {grouping-columns [:symbol]
                              pivot? true}}]
  (-> concatenated-dataset
      (tablecloth/group-by grouping-columns)
      (tablecloth/aggregate {:min (fn [ds]
                                    (->> ds
                                         :close
                                         (apply min)))
                             :max (fn [ds]
                                    (->> ds
                                         :close
                                         (apply max)))
                             :count tablecloth/row-count})
      ((if pivot?
         #(tablecloth/pivot->wider % :symbol [:min :max :count])
         identity))))
