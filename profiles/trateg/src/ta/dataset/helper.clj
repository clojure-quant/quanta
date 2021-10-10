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

(defn pprint-all [ds]
  (print-range ds :all))

(defn pprint-dataset [ds]
  (let [l (ds-rows ds)]
    (if (< l 11)
      (print-range ds :all)
      (do
        (println "printing first+last 5 rows - total rows: " l)
        (print-range ds (concat (range 5)
                                (range (- l 6) (- l 1))))))))

(defn rows
  "Get the rows of the dataset as a list of flyweight maps.  This is a shorter form
  of `mapseq-reader`.
```clojure
user> (take 5 (ds/rows stocks))
({\"date\" #object[java.time.LocalDate 0x6c433971 \"2000-01-01\"],
  \"symbol\" \"MSFT\",
  \"price\" 39.81}
 {\"date\" #object[java.time.LocalDate 0x28f96b14 \"2000-02-01\"],
  \"symbol\" \"MSFT\",
  \"price\" 36.35}
 {\"date\" #object[java.time.LocalDate 0x7bdbf0a \"2000-03-01\"],
  \"symbol\" \"MSFT\",
  \"price\" 43.22}
 {\"date\" #object[java.time.LocalDate 0x16d3871e \"2000-04-01\"],
  \"symbol\" \"MSFT\",
  \"price\" 28.37}
 {\"date\" #object[java.time.LocalDate 0x47094da0 \"2000-05-01\"],
  \"symbol\" \"MSFT\",
  \"price\" 25.45})
```"
  [ds]
  (dataset/mapseq-reader ds))

(defn row-at
  "Get the row at an individual index.  If indexes are negative then the dataset
  is indexed from the end.
```clojure
user> (ds/row-at stocks 1)
{\"date\" #object[java.time.LocalDate 0x534cb03b \"2000-02-01\"],
 \"symbol\" \"MSFT\",
 \"price\" 36.35}
user> (ds/row-at stocks -1)
{\"date\" #object[java.time.LocalDate 0x6bf60ed5 \"2010-03-01\"],
 \"symbol\" \"AAPL\",
 \"price\" 223.02}
```"
  [ds idx]
  ((rows ds) idx))

(defn last-row [ds]
  (row-at ds -1))