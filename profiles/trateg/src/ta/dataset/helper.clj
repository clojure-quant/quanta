(ns ta.dataset.helper
  (:require
   [tick.alpha.api :as tick]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype.functional :as fun]
   [tech.v3.datatype :as dtype]
   [tech.v3.dataset.print :refer [print-range]]
   [tablecloth.api :as tablecloth]
   [fastmath.core :as math]
   [fastmath.stats :as stats]
   [ta.data.date :as dt]))

(defn days-ago [n]
  (-> (tick/now)
      (tick/- (tick/new-duration n :days))
      (tick/date)))

(defn ds-epoch [ds]
  (dataset/column-map ds :epoch #(* 1000 (dt/->epoch %)) [:date]))

;tablecloth/select
;tick/epoch

(defn random-dataset [n]
  (tablecloth/dataset
   {:date (->> (range n)
               (map days-ago)
               reverse)
    :close (repeatedly n rand)}))

(defn select-recent-rows [ds date]
  (-> ds
      (tablecloth/select-rows
       (fn [row]
         (-> row
             :date
             (tick/>= date))))))

(defn random-datasets [m n]
  (repeatedly m #(random-dataset n)))

(defn standardize [xs]
  (-> xs
      (fun/- (fun/mean xs))
      (fun// (fun/standard-deviation xs))))

(defn rand-numbers [n]
  (dtype/clone
   (dtype/make-reader :float32 n (rand))))

(defn ds-rows [ds]
  (-> (tablecloth/shape ds) first))

(defn pprint-dataset [ds]
  (let [l (ds-rows ds)]
    (if (< l 11)
      (print-range ds :all)
      (do
        (println "printing first+last 5 rows - total rows: " l)
        (print-range ds (concat (range 5)
                                (range (- l 6) (- l 1))))))))

