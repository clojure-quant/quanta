(ns ta.helper.print
  (:require
   [tech.v3.dataset :as dataset]
  ; [tech.v3.datatype.functional :as fun]
  ; [tech.v3.datatype :as dtype]
   [tech.v3.dataset.print :refer [print-range]]
   [tablecloth.api :as tc]
   [ta.helper.print]))

;tc/select
;tick/epoch

(defn print-overview [ds]
  (let [l (tc/row-count ds)]
    (if (< l 11)
      (print-range ds :all)
      (do
        (println "printing first+last 5 rows - total rows: " l)
        (print-range ds (concat (range 5)
                                (range (- l 6) (- l 1))))))))

(defn print-all [ds]
  (print-range ds :all))

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