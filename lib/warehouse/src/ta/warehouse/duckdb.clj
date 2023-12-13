(ns ta.warehouse.duckdb
  (:require
   [tablecloth.api :as tc]
   [tmducken.duckdb :as duckdb]))

;; https://github.com/techascent/tmducken

(defn duckdb-start [db-filename]
  (duckdb/initialize! {:duckdb-home "./binaries"})
  (let [db (duckdb/open-db db-filename)
        conn (duckdb/connect db)]
    {:db db
     :conn conn}))

(defn duckdb-stop [{:keys [db conn] :as session}]
  (duckdb/disconnect conn))

;; work with duckdb

(defn append-dataset
  ([session ds]
   (append-dataset session ds false))
  ([session ds create-table?]
   (let [ds (tc/set-dataset-name ds "bars")]
     (when create-table?
       (duckdb/create-table! (:conn session) ds))
     (duckdb/insert-dataset! (:conn session) ds))))

(defn get-bars [session symbol]
  (-> (duckdb/sql->dataset
       (:conn session)
       (str "select * from bars where symbol = '" symbol "' order by date"))
      (tc/rename-columns {"date" :date
                          "open" :open
                          "high" :high
                          "low" :low
                          "close" :close
                          "volume" :volume
                          "symbol" :symbol})))


(defn delete-bars [session]
  (duckdb/sql->dataset
   (:conn session)
   (str "delete from bars")))

 ;; CREATE INDEX s_idx ON films (revenue);

(comment

  (require '[tech.v3.dataset :as ds])
  (def stocks
    (ds/->dataset "https://github.com/techascent/tech.ml.dataset/raw/master/test/data/stocks.csv"
                  {:key-fn keyword
                   :dataset-name :stocks}))

  (require '[modular.system])
  (def session (:duckdb modular.system/system))

  (get-bars session "MSFT")


  (duckdb/create-table! conn stocks)

  (duckdb/insert-dataset! conn stocks)
  (ds/head (duckdb/sql->dataset conn "select * from stocks"))
  (def stmt (duckdb/prepare conn "select * from stocks "))
  (stmt)

  (def r (stmt))

  r


  ;
  )

