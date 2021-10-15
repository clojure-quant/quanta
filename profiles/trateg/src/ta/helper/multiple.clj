(ns ta.helper.multiple
  (:require
   [tablecloth.api :as tc]))

(defn assert-datasets-equal
  "Check that the `:date `columns of all datasets are equal."
  [datasets]
  (->> datasets
       (map :date)
       (apply =)
       assert))

(defn dataset->symbol [ds]
  (-> ds :symbol first))

(defn make-full-symbols-set [datasets]
  (let [symbol->row-count (->> datasets
                               (map (fn [ds]
                                      {:symbol (dataset->symbol ds)
                                       :row-count (tc/row-count ds)})))
        max-row-count (->> symbol->row-count
                           (map :row-count)
                           (apply max))]
    (->> symbol->row-count
         (filter #(-> % :row-count (= max-row-count)))
         (map :symbol)
         set)))

(defn make-full-symbols [full-datasets]
  (->> full-datasets
       (mapv dataset->symbol)))

(defn make-full-datasets [datasets]
  (let [full-symbols-set (make-full-symbols-set datasets)]
    (->> datasets
         (filter #(-> % dataset->symbol full-symbols-set)))))


