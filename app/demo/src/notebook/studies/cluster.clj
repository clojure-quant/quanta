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
   [ta.db.asset.db :as asset-db]
   [ta.helper.returns :refer [diff]]))

; fidelity-select are mutualfunds, which are not supported by kibot. 
;(def assets (load-list "fidelity-select"))
(def assets (load-list "../resources/symbollist/equity-sector/.edn"))

assets
(count assets)
;; => 13

(def db (modular.system/system :bardb-dynamic))

(def window (cal/trailing-window2 [:us :d] 1000))

window

(defn load-ds [asset]
  (get-bars-aligned-filled db {:calendar [:us :d]
                               :import :kibot
                               :asset asset} window))

(load-ds "MSFT")
(load-ds "FSAGX")

(defn add-calcs [bars-ds]
  (tc/add-columns bars-ds {:return (diff (:close bars-ds))}))

((comp add-calcs load-ds) "MSFT")

; we need to remove 2 rows, because the second row has huge gain, as the
; price has been 0 on the first row.
(defn remove-first-2-rows [ds-bars]
  (tc/select-rows ds-bars (range 2 (tc/row-count ds-bars))))

(-> (load-ds "MSFT")
    add-calcs
    remove-first-2-rows)

(def bar-ds-list
  (map (comp remove-first-2-rows add-calcs load-ds) assets))

bar-ds-list

; since we have requested the 1000 most recent bars,
; we should see only 1000 here, but we removed 2 bars, so we have 998 everywhere.
(map tc/row-count bar-ds-list)


(def corrs
  (->> bar-ds-list
       (map #(-> % :return standardize))
       stats/covariance-matrix
       tensor/->tensor))

corrs
;; return correlation between all 13 assets.
;; => #tech.v3.tensor<object>[13 13]
;;    [[ 1.000 0.4630 0.8323 0.6085 0.5907 0.5986 0.5682 0.6493 0.7242 0.5648 0.2635 0.3240 0.6301]
;;     [0.4630  1.000 0.5556 0.6721 0.5963 0.5592 0.5978 0.6163 0.6521 0.5822 0.3315 0.6325 0.5464]
;;     [0.8323 0.5556  1.000 0.6291 0.7585 0.7650 0.6902 0.7480 0.8495 0.7005 0.3844 0.4421 0.7917]
;;     [0.6085 0.6721 0.6291  1.000 0.6225 0.5878 0.6490 0.6654 0.6944 0.5855 0.3225 0.5830 0.5596]
;;     [0.5907 0.5963 0.7585 0.6225  1.000 0.9775 0.7210 0.7477 0.8843 0.7964 0.5681 0.5216 0.8022]
;;     [0.5986 0.5592 0.7650 0.5878 0.9775  1.000 0.6765 0.7353 0.8853 0.7838 0.5533 0.4563 0.8039]
;;     [0.5682 0.5978 0.6902 0.6490 0.7210 0.6765  1.000 0.7145 0.7639 0.6501 0.3952 0.7078 0.6738]
;;     [0.6493 0.6163 0.7480 0.6654 0.7477 0.7353 0.7145  1.000 0.8008 0.6894 0.4380 0.5560 0.7178]
;;     [0.7242 0.6521 0.8495 0.6944 0.8843 0.8853 0.7639 0.8008  1.000 0.8518 0.5316 0.5575 0.8808]
;;     [0.5648 0.5822 0.7005 0.5855 0.7964 0.7838 0.6501 0.6894 0.8518  1.000 0.6312 0.5027 0.7673]
;;     [0.2635 0.3315 0.3844 0.3225 0.5681 0.5533 0.3952 0.4380 0.5316 0.6312  1.000 0.3252 0.4654]
;;     [0.3240 0.6325 0.4421 0.5830 0.5216 0.4563 0.7078 0.5560 0.5575 0.5027 0.3252  1.000 0.4666]
;;     [0.6301 0.5464 0.7917 0.5596 0.8022 0.8039 0.6738 0.7178 0.8808 0.7673 0.4654 0.4666  1.000]]

(def clustering
  (-> bar-ds-list
      (->> (map #(-> % :return standardize)))
      (clustering/k-means 5)))

(:sizes clustering)

(-> {:asset assets
     :name (map asset-db/instrument-name assets)
     :cluster (:clustering clustering)}
    tc/dataset
    (tc/order-by :cluster)
    (print/print-range :all))

;; the instruments should be all in db.

(asset-db/instrument-name "IYK")
(asset-db/instrument-name "IYM")

;; create a lookup-dictionary (a map), so we can find the
;; cluster number for each asset
(def symbol->cluster
  (zipmap assets
          (:clustering clustering)))

symbol->cluster


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
