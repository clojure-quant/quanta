(ns ta.helper.ds
  (:require
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]))

(defn ds->map [ds]
  (into [] (tds/mapseq-reader ds)))

(defn show-meta [ds]
  (->> ds tc/columns (map meta) (map (juxt :name :datatype))))

(defn cols-of-type [ds t]
  (->> ds
       tc/columns
       (map meta)
       (filter #(= t (:datatype %)))
       (map :name)))

(defn drop-instant-cols [ds]
  (tc/drop-columns ds #(= :packed-instant %) :datatype))

