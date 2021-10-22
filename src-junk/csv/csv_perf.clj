(ns series.csv-perf

  (:require
   [clojure-csv.core :as csv]
   [clojure.java.io :as io]

   [ultra-csv.core :as ultra]
   [semantic-csv.core :as semantic]
   [incanter.core :as ic]
   [incanter.io :as ii]

   [series.csvlib :as csvlib]))

(comment

  (def file-name "../DAILY/TGT UN Equity.csv")

  (ii/read-dataset file-name :header true :delim \,)
;; INCANTER PACKAGE - 233 msec
  (println "INCANTER")
  (time
   (ic/$ (range 0 5) '(:date :PX_LAST)
         (ii/read-dataset file-name :header true :delim \,)))

; CSVLIB PACKAGE - 69 msec
  ("println CSVLIB")
  (time
   (ic/$ (range 0 5) '("date" "PX_LAST")
         (ic/to-dataset
          (csvlib/read-csv file-name :headers? true))))

;; ULTRA-CSV PACKAGE - 135 msec
  (println "ULTRA-CSV")
  (time
   (ic/$ (range 0 5) '(:date :PX_LAST)
         (ic/to-dataset
          (ultra/read-csv file-name))))

;; SEMANTIC-CSV PACKAGE - 73 msec
  (println "SEMANTIC-CSV")
  (time
   (ic/$ (range 0 5) '(:date :PX_LAST)
         (ic/to-dataset
          (with-open [in-file (io/reader file-name)]
            (->> (csv/parse-csv in-file) semantic/mappify doall))))))
