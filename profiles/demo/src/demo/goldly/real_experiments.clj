(ns demo.goldly.real-experiments
  (:require [notespace.api]
            [tablecloth.api :as tablecloth]))

(require '[notespace.kinds :as kind]
         '[ta.dataset.helper :as helper]
         '[tablecloth.api :as tablecloth]
         '[tech.v3.dataset.print :as print]
         '[tech.v3.dataset :as dataset]
         '[tech.v3.datatype :as dtype]
         '[tech.v3.tensor :as tensor]
         '[tech.v3.datatype.datetime :as datetime]
         '[tech.v3.datatype.functional :as fun]
         '[tech.v3.datatype.statistics :as dtype-stats]
         '[fastmath.stats :as stats]
         '[fastmath.clustering :as clustering]
         '[ta.warehouse :as wh]
         '[demo.goldly.experiments-helpers :as experiments-helpers])

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


(def datasets
  (->> symbols
       (map (fn [symbol]
              (-> (wh/load-ts w symbol)
                  (tablecloth/add-column :symbol symbol)
                  (tablecloth/add-column :return #(-> %
                                                      :close
                                                      experiments-helpers/returns)))))))

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
       (map dataset->symbol)))

(def concatenated-dataset
  (->> full-datasets
      (apply tablecloth/concat)
      experiments-helpers/add-year-and-month))

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

corrs
#tech.v3.tensor<object>[38 38]
[[ 1.000 0.2063 0.9213 ... 0.2799 0.2769 0.2779]
 [0.2063  1.000 0.2261 ... 0.6172 0.6164 0.6170]
 [0.9213 0.2261  1.000 ... 0.3076 0.3034 0.3052]
 ...
 [0.2799 0.6172 0.3076 ...  1.000 0.9981 0.9990]
 [0.2769 0.6164 0.3034 ... 0.9981  1.000 0.9996]
 [0.2779 0.6170 0.3052 ... 0.9990 0.9996  1.000]]

(fun/sq corrs)
#tech.v3.tensor<object>[38 38]
[[  1.000 0.04255  0.8488 ... 0.07835 0.07669 0.07722]
 [0.04255   1.000 0.05112 ...  0.3810  0.3800  0.3807]
 [ 0.8488 0.05112   1.000 ... 0.09460 0.09205 0.09313]
 ...
 [0.07835  0.3810 0.09460 ...   1.000  0.9962  0.9980]
 [0.07669  0.3800 0.09205 ...  0.9962   1.000  0.9992]
 [0.07722  0.3807 0.09313 ...  0.9980  0.9992   1.000]]

;; "explained variance"
;; 47% of MSFT's variance is explained by SPY
;; 25% of XOM's variance is explained by SPY


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




