(ns demo.studies.helper.experiments-helpers
  (:require ;[notespace.kinds :as kind]
            [ta.dataset.helper :as helper]
            [tablecloth.api :as tablecloth]
            [tech.v3.dataset.print :as print]
            [tech.v3.dataset :as dataset]
            [tech.v3.datatype.datetime :as datetime]
            [tech.v3.datatype :as dtype]))

(defn add-year-and-month [ds]
  (-> ds
      (tablecloth/add-columns
       {:year  #(->> %
                     :date
                     (datetime/long-temporal-field :years))
        :month #(->> %
                     :date
                     (datetime/long-temporal-field :months))})))


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


(defn returns [integrated-values]
  (let [n (count integrated-values)]
    (dtype/clone
     (dtype/make-reader
      :float32
      n
      (if (= idx 0)
        0
        (- (integrated-values idx)
           (integrated-values (dec idx))))))))


(comment
  (->> [1 8 0 -9 1 4]
       (reductions +)
       vec
       returns)
  ;; #array-buffer<float32> [6]
  ;; [0.000, 8.000, 0.000, -9.000, 1.000, 4.000]
  )
