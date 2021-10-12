(ns demo.studies.real-experiments
  (:require
   [tech.v3.dataset.print :as print]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype :as dtype]
   [tech.v3.tensor :as tensor]
   [tech.v3.datatype.datetime :as datetime]
   [tech.v3.datatype.functional :as fun]
   [tech.v3.datatype.statistics :as dtype-stats]
   [tablecloth.api :as tablecloth]
   [fastmath.stats :as stats]
   [fastmath.clustering :as clustering]
   [loom.graph]
   [loom.alg]
   [ta.warehouse :as wh]
   [ta.dataset.helper :as helper]
   [ta.dataset.date :refer [add-year-and-month]]
   [ta.dataset.returns :refer [returns]]
   [demo.studies.helper.experiments-helpers :as experiments-helpers]))

(defonce w
  (wh/init {:series "../db/"
            :list   "../resources/etf/"}))

(def symbols
  (wh/load-list w "fidelity-select"))

(def symbol->name
  (->> "../resources/etf/fidelity-select.edn"
       slurp
       read-string
       (map (juxt :symbol :name))
       (into {})))

(defonce datasets
  (->> symbols
       (map (fn [symbol]
              (-> (wh/load-ts w symbol)
                  (tablecloth/add-column :symbol symbol)
                  (tablecloth/add-column :return #(-> %
                                                      :close
                                                      returns)))))))

(defn dataset->symbol [ds]
  (-> ds :symbol first))

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

(def concatenated-dataset
  (->> full-datasets
       (apply tablecloth/concat)
       add-year-and-month))

(-> concatenated-dataset
    (tablecloth/random 10))

(-> concatenated-dataset
    (experiments-helpers/symbols-overview {:grouping-columns [:symbol :year :month]})
    (print/print-range :all))

(-> concatenated-dataset
    (experiments-helpers/symbols-overview {})
    (print/print-range :all))

(-> concatenated-dataset
    (experiments-helpers/symbols-overview {:pivot? false})
    (print/print-range :all))

(def corrs
  (->> full-datasets
       (map #(-> % :return helper/standardize))
       stats/covariance-matrix
       tensor/->tensor))

(def clustering
  (-> full-datasets
      (->> (map #(-> % :return helper/standardize)))
      (clustering/k-means 5)))

(:sizes clustering)

(-> {:symbol  full-symbols
     :name (map symbol->name full-symbols)
     :cluster (:clustering clustering)}
    tablecloth/dataset
    (tablecloth/order-by :cluster)
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
    tablecloth/dataset
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
                 (map (fn [{:keys [i j sign]}]
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
         (map (fn [{:keys [i j sign]}]
                [i j]))
         (apply loom.graph/graph)
         loom.alg/connected-components
         (map-indexed
          (fn [i comp-indexes]
            (tablecloth/dataset
             {:component      (str "comp" i)
              :symbol (map full-symbols comp-indexes)})))
         (apply tablecloth/concat))
    (tablecloth/add-columns
     {:name    #(->> % :symbol (mapv symbol->name))
      :cluster #(->> % :symbol (mapv symbol->cluster))})
    (print/print-range :all))
