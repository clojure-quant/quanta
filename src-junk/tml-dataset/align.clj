




;; Check that the `:date` columns of all datasets are equal.
(->> datasets
     (map :date)
     (apply =)
     assert)

(def full-symbols-set
  (let [symbol->row-count (->> datasets
                               (map (fn [ds]
                                      {:symbol (dataset->symbol ds)
                                       :row-count (tablecloth/row-count ds)})))
        max-row-count (->> symbol->row-count
                           (map :row-count)
                           (apply max))]
    (->> symbol->row-count
         (filter #(-> % :row-count (= max-row-count)))
         (map :symbol)
         set)))

(def full-datasets
  (->> datasets
       (filter #(-> % dataset->symbol full-symbols-set))))

(def full-symbols
  (->> full-datasets
       (mapv dataset->symbol)))