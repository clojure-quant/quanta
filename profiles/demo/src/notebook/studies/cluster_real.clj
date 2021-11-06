(ns notebook.studies.cluster-real
  (:require
   [tech.v3.dataset.print :as print]
   [tech.v3.datatype.functional :as fun]
   [tech.v3.tensor :as tensor]
   [tablecloth.api :as tc]
   [fastmath.stats :as stats]
   [fastmath.clustering :as clustering]
   [loom.graph]
   [loom.alg]
   [ta.warehouse :as wh]
   [ta.warehouse.overview :refer [load-datasets concatenate-datasets overview-view]]
   [ta.helper.stats :refer [standardize]]
   [ta.helper.multiple :as m :refer [make-full-datasets make-full-symbols]]))

(def symbols
  (wh/load-list "fidelity-select"))

(def symbol->name
  (->> "../resources/etf/fidelity-select.edn"
       slurp
       read-string
       (map (juxt :symbol :name))
       (into {})))

symbol->name

(def concatenated-dataset
  (-> (load-datasets :stocks "D" symbols)
      (concatenate-datasets)))

(-> concatenated-dataset
    (tc/random 10))

(->> concatenated-dataset
     tc/columns
     (map meta))

(-> concatenated-dataset
    (overview-view {:grouping-columns [:symbol :year :month]})
    (print/print-range :all))

(-> concatenated-dataset
    (overview-view {})
    (print/print-range :all))

(-> concatenated-dataset
    (overview-view {:pivot? false})
    (print/print-range :all))

(m/symbol-count-table concatenated-dataset)

(def full-datasets
  (make-full-datasets concatenated-dataset))

full-datasets

(def full-symbols
  (make-full-symbols concatenated-dataset))

full-symbols

(def corrs
  (->> full-datasets
       (map #(-> % :return standardize))
       stats/covariance-matrix
       tensor/->tensor))

corrs

(def clustering
  (-> full-datasets
      (->> (map #(-> % :return standardize)))
      (clustering/k-means 5)))

(:sizes clustering)

(-> {:symbol  full-symbols
     :name (map symbol->name full-symbols)
     :cluster (:clustering clustering)}
    tc/dataset
    (tc/order-by :cluster)
    (print/print-range :all))

(def symbol->cluster
  (zipmap full-symbols
          (:clustering clustering)))

(defn edges [threshold]
  (let [n (count full-symbols)]
    (-> (for [j     (range n)
              i     (range j)
              :let  [r (corrs i j)]
              :when (-> r
                        fun/sq
                        (>= threshold))]
          {:i    i
           :j    j
           :sign (fun/signum r)}))))

(-> edges
    tc/dataset
    (print/print-range :all))

(let [threshold 0.6
      stylesheet    [{:selector "node"
                      :style    {:width  20
                                 :height 10
                                 :shape  "rectangle"}}
                     {:selector "edge"
                      :style    {:width 5
                                 :line-color "purple"}}]
      nodes (->> full-symbols
                 (map-indexed (fn [i symbol]
                                {:data {:id i
                                        :label (name symbol)}})))
      edges (->> threshold
                 edges
                 (map (fn [{:keys [i j #_sign]}]
                        {:data {:id (str i "-" j)
                                :source i
                                :target j}})))
      elements (concat nodes edges)]
  ^:R
  [:p/cytoscape   {;:box :lg
                   :stylesheet stylesheet
                   :elements   elements
                   :layout     {:name "cose"}
                   :style      {:border "9px solid #39b"
                                :width  "800px"
                                :height "800px"}}])

(-> (->> 0.6
         edges
         (map (fn [{:keys [i j #_sign]}]
                [i j]))
         (apply loom.graph/graph)
         loom.alg/connected-components
         (map-indexed
          (fn [i comp-indexes]
            (tc/dataset
             {:component      (str "comp" i)
              :symbol (map full-symbols comp-indexes)})))
         (apply tc/concat))
    (tc/add-columns
     {:name    #(->> % :symbol (mapv symbol->name))
      :cluster #(->> % :symbol (mapv symbol->cluster))})
    (print/print-range :all))
