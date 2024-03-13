(ns notebook.data.bardb.duck-bug
   (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [tmducken.duckdb :as duckdb]))
 
 (def db (duckdb/open-db "/tmp/duckdb-test"))

 (def conn (duckdb/connect db))


(defn make-ds [v]
  (let [table-name "test"]
    (-> (tc/dataset v)
        (tc/set-dataset-name table-name))))
 
(def empty-ds (make-ds [{:date (t/now)
                         :open 0.0 :high 0.0 :low 0.0 :close 0.0
                         :volume 0.0 ; crypto volume is double.
                         :ticks 0
                         :asset "000"}]))
 
 empty-ds


(duckdb/create-table! conn empty-ds)
 
(def data-ds (make-ds [{:date (t/now)
                        :open 1.0 :high 2.0 :low 3.0 :close 4.0
                        :volume 0.0 ; crypto volume is double.
                        :ticks 0
                        :asset "1"}]))

(duckdb/insert-dataset! conn data-ds)
;; => 1

(def data2-ds (make-ds [{:asset "2"; NOTE asset is the first key
                        :date (t/now)
                        :open 1.0 :high 2.0 :low 3.0 :close 4.0
                        :volume 0.0 ; crypto volume is double.
                        :ticks 0
                        }]))
 
 (duckdb/insert-dataset! conn data2-ds)
 ;; => Execution error at tmducken.duckdb/insert-dataset!$fn$check-error (duckdb.clj:320).
 ;;    {:address 0x00007F53A4467DB0 }

