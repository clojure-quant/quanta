(ns notebook.studies.cluster
  (:require
   [tech.v3.dataset.print :as print]
   [tech.v3.datatype.functional :as fun]
   [tech.v3.tensor :as tensor]
   [tablecloth.api :as tc]
   [fastmath.stats :as stats]
   [fastmath.clustering :as clustering]
   [loom.graph]
   [loom.alg]
   [modular.system]
   [ta.math.stats :refer [standardize]]
   [ta.calendar.core :as cal]
   [ta.db.asset.symbollist :refer [load-list]]
   [ta.db.bars.aligned :refer [get-bars-aligned-filled]]
   ;[ta.db.bars.overview:refer [load-datasets concatenate-datasets overview-view]]
   ;[ta.helper.multiple :as m :refer [make-full-datasets make-full-symbols]]
   ))

(def assets
  (load-list "fidelity-select"))

assets

(def db (modular.system/system :bardb-dynamic))

(def window (cal/trailing-window2 [:us :d] 1000))

window

(defn load-ds [asset]
  (get-bars-aligned-filled db {:calendar [:us :d]
                               :import :kibot
                               :asset asset
                               } window))

(load-ds "MSFT")
(load-ds "FSAGX")

(def bar-ds-list
  (map load-ds assets))

bar-ds-list

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
