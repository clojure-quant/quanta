(ns ta.warehouse.duckdb
  (:require
    [taoensso.timbre :as timbre :refer [info warn error]]
    [clojure.java.io :as java-io]
    [tablecloth.api :as tc]
    [tmducken.duckdb :as duckdb]
    [tick.core :as tick]))

;; https://github.com/techascent/tmducken


(defn- exists-db? [path]
  (.exists (java-io/file path)))

(defn duckdb-start [db-filename]
  (duckdb/initialize! {:duckdb-home "./binaries"})
  (let [new? (exists-db? db-filename)
        db (duckdb/open-db db-filename)
        conn (duckdb/connect db)]
    {:db db
     :conn conn
     :new? new?
     }))

(defn duckdb-stop [{:keys [db conn] :as session}]
  (duckdb/disconnect conn))

;; work with duckdb

(defn append-bars
  ([session ds]
   (append-bars session ds false))
  ([session ds create-table?]
   (let [ds (tc/set-dataset-name ds "bars")]
     (info "duckdb append-bars # " (tc/row-count ds))
     (when create-table?
       (duckdb/create-table! (:conn session) ds))
     (duckdb/insert-dataset! (:conn session) ds))))

(defn get-bars [session asset]
  (-> (duckdb/sql->dataset
       (:conn session)
       (str "select * from bars where asset = '" asset "' order by date"))
      (tc/rename-columns {"date" :date
                          "open" :open
                          "high" :high
                          "low" :low
                          "close" :close
                          "volume" :volume
                          "asset" :asset})))


(defn delete-bars [session]
  (duckdb/sql->dataset
   (:conn session)
   (str "delete from bars")))

 ;; CREATE INDEX s_idx ON films (revenue);


(defn now []
  (-> (tick/now)
      ;(tick/date-time)
      (tick/instant)
      ))

(def empty-ds 
  (-> 
    (tc/dataset [{:open 0.0 :high 0.0 :low 0.0 :close 0.0
                  :volume 0 :asset "000"
                  :date (now)
                  :epoch 0 :ticks 0}]) 
     (tc/set-dataset-name "bars")
   ))

(defn init-tables [session]
  (let [exists? (:new? session)]
    (when (not exists?)
      (println "init duck-db tables")    
      (duckdb/create-table! (:conn session) empty-ds))))
 
(comment

  (require '[tech.v3.dataset :as ds])
  (def stocks
    (ds/->dataset "https://github.com/techascent/tech.ml.dataset/raw/master/test/data/stocks.csv"
                  {:key-fn keyword
                   :dataset-name :stocks}))
  stocks
  (tc/info stocks)

  (require '[modular.system])

  (def db (:duckdb modular.system/system))
  db


  (get-bars db "MSFT")

  (now)
  empty-ds
  (tc/info empty-ds)
  (init-tables db)

  (duckdb/create-table! (:conn db) empty-ds)
  (duckdb/insert-dataset! (:conn db) empty-ds)
    
  (get-bars db "000")

  (get-bars db "EUR/USD")
  (get-bars db "USD/JPY")
  

  (exists-db?  "../../output/duckdb/bars")
  

  (duckdb/insert-dataset! db stocks)
  (ds/head (duckdb/sql->dataset db "select * from stocks"))
  (def stmt (duckdb/prepare db "select * from stocks "))
  (stmt)

  (def r (stmt))

  r


  ;
  )

