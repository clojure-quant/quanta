(ns ta.db.bars.duckdb
  (:require
   [clojure.set :refer [subset?]]
   [de.otto.nom.core :as nom]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [clojure.java.io :as java-io]
   [tmducken.duckdb :as duckdb]
   [ta.calendar.calendars :refer [get-calendar-list]]
   [ta.db.bars.protocol :refer [bardb barsource]]))

;; https://github.com/techascent/tmducken

(defn- exists-db? [path]
  (.exists (java-io/file path)))

(defn- duckdb-start-impl [db-filename]
  (duckdb/initialize! {:duckdb-home "./binaries"})
  (let [new? (exists-db? db-filename)
        db (duckdb/open-db db-filename)
        conn (duckdb/connect db)]
    {:db db
     :conn conn
     :new? new?}))

(defn- duckdb-stop-impl [{:keys [db conn] :as session}]
  (duckdb/disconnect conn))

;; bar-category

(defn bar-category->table-name [[calendar interval]]
  (str (name calendar) "_" (name interval)))

;; work with duckdb

(defn- date-type [ds]
  (-> ds :date meta :datatype))

(defn- ensure-date-instant
  "duckdb needs one fixed type for the :date column.
   we use instant, which techml calls packed-instant"
  [ds]
  (let [t (date-type ds)
        instant? (= t :packed-instant)]
    (if instant?
      ds
      (tc/add-column ds :date (map t/instant (:date ds))))))

(defn- ensure-col-float64
  "duckdb needs one fixed type for the :volume column.
   many data-sources return volume as int, so we might have to convert it."
  [ds col]
  (let [t (-> ds col meta :datatype)]
    (if (= t :float64)
      ds
      (tc/add-column ds col (map double (col ds))))))

(defn- has-col [ds col]
  (->> ds
       tc/columns
       (map meta)
       (filter #(= col (:name %)))
       empty?
       not
       ;(map :name)
       ))

(defn- ensure-epoch [ds]
  (if (has-col ds :epoch)
    ds
    (tc/add-column ds :epoch 0)))

(defn- ensure-ticks [ds]
  (if (has-col ds :ticks)
    ds
    (tc/add-column ds :ticks 0)))

(defn- ensure-asset [ds asset]
  (if (has-col ds :asset)
    ds
    (tc/add-column ds :asset asset)))

(defn- ds-cols [ds]
  (->> ds tc/column-names (into #{})))

(defn- has-dohlcv? [ds]
  (subset? #{:date :open :high :low :close :volume} (ds-cols ds)))

(defn order-columns [ds]
  (tc/dataset [(:date ds)
               (:asset ds)
               (:open ds)
               (:high ds)
               (:low ds)
               (:close ds)
               (:volume ds)
               (:epoch ds)
               (:ticks ds)]))

(defn order-columns-strange [ds]
  ; see: https://github.com/techascent/tmducken/issues/18
  (tc/dataset [(:open ds)
               (:epoch ds)
               (:date ds)
               (:close ds)
               (:volume ds)
               (:high ds)
               (:low ds)
               (:ticks ds)
               (:asset ds)]))

(defn append-bars [session {:keys [calendar asset]} ds]
  (assert (has-dohlcv? ds) "ds needs to have columns [:date :open :high :low :close :volume]")
  (let [table-name (bar-category->table-name calendar)
        ds (-> ds
               (ensure-date-instant)
               (ensure-col-float64 :volume)
               (ensure-col-float64 :open)
               (ensure-col-float64 :high)
               (ensure-col-float64 :low)
               (ensure-col-float64 :close)
               (ensure-epoch)
               (ensure-ticks)
               (ensure-asset asset)
               (order-columns-strange)
               (tc/set-dataset-name table-name))]
    (info "duckdb append-bars # " (tc/row-count ds))
    ;(info "duckdb append-bars ds-meta: " (tc/info ds))
    ;(info "session: " session)
    ;(info "ds: " ds)
    ;(info "date col type: " (date-type ds))
    (duckdb/insert-dataset! (:conn session) ds)))

(defn keywordize-columns [ds]
  (tc/rename-columns
   ds
   {"date" :date
    "open" :open
    "high" :high
    "low" :low
    "close" :close
    "volume" :volume
    "asset" :asset
    "epoch" :epoch
    "ticks" :ticks}))

(defn sql-query-bars-for-asset [calendar asset]
  (let [table-name (bar-category->table-name calendar)]
    (str "select * from " table-name " where asset = '" asset "' order by date")))

(defn get-bars-full [session calendar asset]
  (debug "get-bars " asset)
  (let [query (sql-query-bars-for-asset calendar asset)]
    (-> (duckdb/sql->dataset (:conn session) query)
        (keywordize-columns))))

(defn sql-query-bars-for-asset-since [calendar asset since]
  (let [table-name (bar-category->table-name calendar)]
    (str "select * from " table-name
         " where asset = '" asset "'"
         " and date > '" since "'"
         " order by date")))

(defn get-bars-since [session calendar asset since]
  (debug "get-bars-since " asset since)
  (let [query (sql-query-bars-for-asset-since calendar asset since)]
    (-> (duckdb/sql->dataset (:conn session) query)
        (keywordize-columns))))

(defn sql-query-bars-for-asset-window [calendar asset dstart dend]
  (let [table-name (bar-category->table-name calendar)]
    (str "select * from " table-name
         " where asset = '" asset "'"
         " and date >= '" dstart "'"
         " and date <= '" dend "'"
         " order by date")))

(defn get-bars-window [session calendar asset dstart dend]
  (debug "get-bars-window " asset dstart dend)
  (let [query (sql-query-bars-for-asset-window calendar asset dstart dend)]
    (debug "sql-query: " query)
    (-> (duckdb/sql->dataset (:conn session) query)
        (keywordize-columns))))

(defn ensure-instant [dt]
  (when dt
    (if (t/zoned-date-time? dt)
      (t/instant dt)
      dt)))

(defn get-bars
  "returns bars for asset/calendar + window"
  [session {:keys [asset calendar]} {:keys [start end] :as window}]
  (let [; v0.10 of tmlducken cannot do queries with date
        ; being zoned-datetime
        start (ensure-instant start)
        end (ensure-instant end)
        bar-ds (cond
                 (and start end)
                 (get-bars-window session calendar asset start end)

                 start
                 (get-bars-since session calendar asset start)

                 :else
                 (get-bars-full session calendar asset))]
    (cond
      (or (nil? bar-ds)
          (= 0 (tc/row-count bar-ds)))
      (nom/fail ::get-bars-duckdb {:message (str "asset " asset " has no bars in duckdb!")})

      :else
      bar-ds)))

(defn sql-delete-bars-asset [session calendar asset]
  (let [table-name (bar-category->table-name calendar)]
    (str "delete from " table-name
         " where asset = '" asset "'")))

(defn delete-bars [session calendar asset]
  (duckdb/run-query!
   (:conn session)
   (sql-delete-bars-asset session calendar asset)))

;; CREATE INDEX s_idx ON films (revenue);

(defn- now []
  (-> (t/now)
      ;(tick/date-time)
      (t/instant)))

(defn empty-ds [calendar]
  (let [table-name (bar-category->table-name calendar)]
    (-> (tc/dataset [{:open 0.0 :high 0.0 :low 0.0 :close 0.0
                      :volume 0.0 ; crypto volume is double.
                      :asset "000"
                      :date (now)
                      :epoch 0 :ticks 0}])
        (tc/set-dataset-name table-name))))

(defn create-table [session calendar]
  (let [ds (empty-ds calendar)]
    (duckdb/create-table! (:conn session) ds)))

(defn make-table-defs []
  (let [cals (get-calendar-list)
        make-one-cal (fn [c] [[c :d]
                              [c :h]
                              [c :m]])]
    (->> (map make-one-cal cals)
         (apply concat)
         (into []))))

(def fixed-table-defs
  [[:us :m]
   [:us :h]
   [:us :d]
   [:forex :d]
   [:forex :m]
   [:crypto :d]
   [:crypto :m]])

(defn init-tables [session]
  (let [exists? (:new? session)]
    (when (not exists?)
      (println "init duck-db tables")
      (doall (map (partial create-table session)
                  (make-table-defs))))))

(defrecord bardb-duck [db conn new?]
  barsource
  (get-bars [this opts window]
    (info "get-bars " opts window)
    (get-bars this opts window))
  bardb
  (append-bars [this opts ds-bars]
    ;(info "this: " this)
    (append-bars  this opts ds-bars)))

(defn start-bardb-duck [opts]
  (let [{:keys [db conn new?]} (duckdb-start-impl opts)]
    (bardb-duck. db conn new?)))

(defn stop-bardb-duck [state]
  (duckdb-stop-impl state))

(comment

  (make-table-defs)

  (-> (now) type)

  (require '[modular.system])
  (def db (:duckdb modular.system/system))
  (def db (duckdb-start-impl "../../output/duckdb/bars"))
  db

  (bar-category->table-name [:us :m])
  (create-table db [:us :m])
  (create-table db [:us :h])

  (require '[tech.v3.dataset :as ds])
  (def stocks
    (ds/->dataset "https://github.com/techascent/tech.ml.dataset/raw/master/test/data/stocks.csv"
                  {:key-fn keyword
                   :dataset-name :stocks}))
  stocks
  (tc/info stocks)

  (get-bars db [:us :m] "EUR/USD")
  (get-bars db [:us :m] "ETHUSDT")

  (sql-query-bars-for-asset-since
   [:us :m] "EUR/USD" "2024-01-26T19:35:00Z")

  (require '[tick.core :as t])
  (def dt (t/instant "2024-01-26T19:35:00Z"))
  dt

  (get-bars-since db [:us :m] "EUR/USD" "2024-01-26T19:35:00Z")
  (get-bars-since db [:us :m] "EUR/USD" "2024-01-26 19:35:00")
  (get-bars-since db [:us :m] "EUR/USD" dt)

  (def dt2 (t/instant "2024-01-26T16:35:00Z"))
  (get-bars-since db [:us :m] "EUR/USD" dt2)

  (get-bars-window db [:us :m] "EUR/USD"
                   "2024-01-26T19:35:00Z"
                   "2024-01-26T19:45:00Z")

  (get-bars-window db [:us :m] "ETHUSDT"
                   "2024-01-29T18:56:00Z"
                   "2024-01-29T19:00:00Z")

  (get-bars-window db [:us :m] "ETHUSDT"
                   (tick/instant "2024-01-29T18:56:00Z")
                   (tick/instant "2024-01-29T19:00:00Z"))

  (get-bars-since db [:us :m] "EUR/USD" time)
  (get-bars-since db [:us :m] "EUR/USD" (str time))
  (t/inst time)
  (format-date time)

  (str time)

  (t/inst "2023-01-01 0:0:0")

  (now)
  empty-ds
  (tc/info empty-ds)
  (init-tables db)

  (duckdb/create-table! (:conn db) empty-ds)
  (duckdb/insert-dataset! (:conn db) empty-ds)

  (exists-db?  "../../output/duckdb/bars")

  (duckdb/insert-dataset! db stocks)
  (ds/head (duckdb/sql->dataset db "select * from stocks"))
  (def stmt (duckdb/prepare db "select * from stocks "))
  (stmt)

  (def r (stmt))

  r

;
  )

