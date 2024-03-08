(ns notebook.playground.bardb.performance-test
  (:require
    [taoensso.timbre :refer [trace debug info warn error]]
    [clojure.string :as str]
    [tick.core :as t]
    [tmducken.duckdb :as duckdb]
    [tech.v3.dataset :as ds]
    [clojure.java.io :as java-io]
    [ta.db.bars.protocol :refer [bardb barsource]]
    [ta.db.bars.protocol :as b]
    [ta.db.bars.duckdb :refer [start-bardb-duck stop-bardb-duck]]
    [ta.calendar.calendars :refer [calendars]]
    [ta.calendar.core :as cal]
    [ta.algo.env :as algo-env-impl]
    [ta.algo.env.protocol :as algo-env]
    ;[ta.algo.backtest :refer [run-backtest]]
    [ta.engine.protocol :as eng]
    [ta.calendar.combined :refer [combined-event-seq]]))

(defn- duckdb-start [db-filename]
    (duckdb/initialize! {:duckdb-home "../../app/demo/binaries"})
    (let [db (duckdb/open-db db-filename)
          conn (duckdb/connect db)]
        {:db db
         :conn conn}))

;; RANDOM CANDLE

(defn vec->sql-str [arr]
    (str "[" (str/join ", " (map #(str "'" % "'") arr)) "]"))

(defn gen-asset-names
  ([n]
   (gen-asset-names 1 n))
  ([from n]
   (map #(str "ASSET_" %) (range from (inc n)))))

(defn create-partition-table [conn table-name from to]
  (duckdb/run-query! conn
                     (format "CREATE TABLE %s PARTITION OF %s
                                FOR VALUES FROM ('%s') TO ('%s');",
                             (str table-name"_"from)
                             table-name
                             from
                             to)))

(defn create-ohcl-table [conn table-name]
    (duckdb/run-query! conn
       (format "CREATE TABLE %s (
                  date TIMESTAMP,
                  open DOUBLE,
                  high DOUBLE,
                  low DOUBLE,
                  close DOUBLE,
                  volume DOUBLE,
                  ticks BIGINT,
                  epoch BIGINT,
                  asset VARCHAR);", table-name)))

(defn insert-random-candles [conn table-name from to assets]
    (duckdb/run-query! conn (format
        "INSERT INTO %s (date, open, high, low, close, volume, ticks, epoch, asset)
         SELECT date,
           round(random() * 1000) AS open,
           round(random() * 1000) AS high,
           round(random() * 1000) AS low,
           round(random() * 1000) AS close,
           round(random() * 1000) AS volume,
           round(random() * 10) AS ticks,
           round(random() * 10) AS epoch,
           (%s)[i] as asset
         FROM generate_series(TIMESTAMP '%s', TIMESTAMP '%s', INTERVAL 1 MINUTE) AS t1(date),
              generate_series(1,%d) AS t2(i),
         ORDER BY date;", table-name (vec->sql-str assets) from to (count assets))))

(defn get-total-count [conn table-name]
  (-> (duckdb/sql->dataset conn (format "select count(*) from %s", table-name)) (first) (second) (first)))

(defn measure-insert [conn table-name from to assets]
    (println "ingesting..." from "-" to ", " (first assets) "-" (last assets))
    (time (insert-random-candles conn table-name from to assets))
    (println "total rows: " (get-total-count conn table-name))
    (println "-------------------------------------------------------------------------------"))

(defn get-range-start-dt [to-year n]
  (let [from-year (- to-year (dec n))]
    (str from-year "-01-01 00:00:00")))

(defn generate-year-ranges [start-year end-year]
  (for [year (range start-year (inc end-year))]
    [(str year "-01-01 00:00:00") (str year "-12-31 23:59:00")]))

(defn create-new-db [path {:keys [years assets]} [calendar-kw interval-kw]]
  (let [table-name (str (name calendar-kw) "_" (name interval-kw))
        path (str path"/duckdb-partitioning_"table-name"_"assets"a_"years"y.db")
        {:keys [db conn new?] :as state} (start-bardb-duck path)
        to-year 2023
        from-year (- to-year (dec years))
        year-ranges (generate-year-ranges from-year to-year)]
    (create-ohcl-table conn table-name)
    (doseq [[from to] year-ranges]
      (doseq [asset-range (partition-all 10 (range 1 (inc assets)))]
        (measure-insert conn
                        table-name
                        from
                        to
                        (gen-asset-names (first asset-range) (last asset-range)))))
    (stop-bardb-duck state)))

(defn create-hive-partition-by-year [conn table-name path]
  (duckdb/run-query! conn
    (str "COPY (select *, EXTRACT(YEAR FROM date) AS year
                from " table-name ")
          TO '"path"' (FORMAT PARQUET, PARTITION_BY (year));")))

(defn create-hive-partition-by-asset [conn table-name path]
  (duckdb/run-query! conn
    (str "COPY " table-name "
          TO '"path"' (FORMAT PARQUET, PARTITION_BY (asset));")))

(defn trailing-window-test [db start end]
  (loop [cur (cal/current-close :crypto :m end)]
    (if (t/>= cur start)
      (let [table-name "crypto_m"
            asset "ASSET_1"
            dstart (str (t/date cur)
                        " "
                        (t/time cur)":00")
            dend dstart
            query (str "select * from " table-name
                       " where asset = '" asset "'"
                       " and date = '" dstart "'"
                       ;" and date >= '" dstart "'"
                       ;" and date <= '" dend "'"
                       " order by date")
            res (duckdb/sql->dataset (:conn db) query)]
        (println res)
        ;(b/get-bars db {:asset "ASSET_1" :calendar [:crypto :m]} {:start (t/instant "2023-01-01T00:00:00Z")
        ;                                                          :end (t/instant "2023-01-01T00:10:00Z")})
        (recur (cal/prior-close :crypto :m cur))))))



(defn secret [env spec time]
  (str "the spec is: " spec " (calculated: " time ")"))

(def spec {:type :trailing-bar
           :calendar [:crypto :m]
           :data 42
           :asset "ASSET_1"
           :trailing-n 500000
           :algo 'notebook.playground.bardb.performance-test/secret})

(defn run-backtest [env window]
  (let [engine (algo-env/get-engine env)
        cals (eng/active-calendars engine)
        event-seq (combined-event-seq window cals)]
    (info "backtesting window: " window " ..")
    (doall (map #(eng/set-calendar! engine %) event-seq))
    (info "backtesting window: " window "finished!")
    :backtest-finished))

(defn backtest-algo
  "run a single bar-strategy with data powered by bar-db-kw.
   returns the result of the strategy."
  [bar-db algo-spec dt]
  (let [env (algo-env-impl/create-env-javelin bar-db)
        strategy (algo-env/add-algo env algo-spec)
        calendar [:us :m]
        window (cal/trailing-range calendar 1 dt)]
    (run-backtest env window)
    strategy))

(defn backtest-algo-date
  "run a single bar-strategy with data powered by bar-db-kw
   as of date dt. returns the result of the strategy."
  [bar-db algo-spec dt]
  (let [
        ;bar-db  (modular.system/system bar-db-kw)
        env (algo-env-impl/create-env-javelin bar-db)
        strategy (algo-env/add-algo env algo-spec)
        engine (algo-env/get-engine env)
        calendars (eng/active-calendars engine)
        prior-dates (map (fn [[calendar-kw interval-kw]]
                           (cal/current-close calendar-kw interval-kw dt))
                         calendars)
        ;prior-dates-sorted (sort prior-dates)
        event-seq (map (fn [cal dt]
                         {:calendar cal :time dt}) calendars prior-dates)]
    (info "event-seq: " event-seq)
    (doall (map #(eng/set-calendar! engine %) event-seq))
    strategy))

(defn test-duckdb [_]
  ;(require '[ta.db.bars.duckdb :refer [start-bardb-duck]])
  ;
  ;(let [db (start-bardb-duck "~/Desktop/tmp/duckdb-partitioning_crypto_m_1a_100y.db")]
  ;  (time (backtest-algo-date db spec (t/in (t/date-time "2023-12-31T23:59:00") "America/New_York"))))

  (require '[ta.db.bars.duckdb :refer [start-bardb-duck]])
  (let [db-name "duckdb-partitioning_crypto_m_1000a_1y.db"
        db-path "~/Desktop/tmp"
        hive-path "~/Desktop/tmp/hive"
        db (start-bardb-duck (str db-path"/"db-name))]
    (create-hive-partition-by-year (:conn db) "crypto_m" (str hive-path"/"db-name))
    )
  )

(comment
  (def base-path "~/Desktop/tmp")

  ; create databases
  (create-new-db base-path {:assets 1 :years 1} [:crypto :m])     ; 12 MB
  (create-new-db base-path {:assets 100 :years 10} [:crypto :m])  ; 11 GB

  (create-new-db base-path {:assets 1 :years 10} [:crypto :m])    ; 110 MB
  (create-new-db base-path {:assets 1 :years 100} [:crypto :m])   ; 1.1 GB

  (create-new-db base-path {:assets 10 :years 1} [:crypto :m])    ; 114 MB
  (create-new-db base-path {:assets 100 :years 1} [:crypto :m])   ; 1.1 GB
  (create-new-db base-path {:assets 1000 :years 1} [:crypto :m])  ; 11 GB

  ;(def duckdb (:duckdb modular.system/system))
  ;(def db (modular.system/system :duckdb))

  ; init db
  (require '[ta.db.bars.duckdb :refer [start-bardb-duck]])
  (def db-name "duckdb-partitioning_crypto_m_1a_100y.db")
  (def db-path "~/Desktop/tmp")
  (def hive-path "~/Desktop/tmp/hive")
  (def db (start-bardb-duck (str db-path"/"db-name)))

  ; partitioning
  ;(create-partition-table (:conn db) "crypto_m" "2023-01-01T00:00:00" "2023-12-31T23:59:00")
  ;(create-hive-partition-by-year (:conn db) "crypto_m" (str hive-path"/"db-name))

  ; get window
  ;(require '[ta.db.bars.protocol :as b])
  ;(b/get-bars db {:asset "ASSET_1" :calendar [:crypto :m]} {:start (t/instant "2023-01-01T00:00:00Z") :end (t/instant "2023-01-01T00:10:00Z")})

  ;; Backtest
  ;(backtest-algo db (assoc spec :trailing-n 500000) (t/in (t/date-time "2023-02-01T12:00:00") "America/New_York"))
  (time (backtest-algo-date db (assoc spec :trailing-n 500000) (t/in (t/date-time "2023-12-31T23:59:00") "America/New_York")))
  ;(time (trailing-window-test db
  ;                            (t/in (t/date-time "2023-01-01T00:00:00") "America/New_York")
  ;                            (t/in (t/date-time "2023-12-29T23:59:00") "America/New_York") ; last trading day in 2023
  ;                            ))

  ;; roundtrip
  ; TODO
  ;(require '[ta.trade.signal :refer [trade-signal]])
  ;(require '[ta.trade.roundtrip-backtest :refer [calc-roundtrips]])
  ;(trade-signal ds-demo)
  ;
  ;(-> (trade-signal ds-demo)
  ;    (calc-roundtrips {})
  ;    )
  ;
  ;(trade-signal ds-bars)


  ;; results
  ; 500k bars x 1 asset 1y = 1.628 | 2.597 sec
  ; 500k bars x 10 asset 1y =  1.5 | 1.7 sec
  ; 500k bars x 1000 asset 1y = 2.0 | 2.3 | 3.2 sec

  ; 5mio  bars x 1 asset 10y = 16.71 sec
  ; 50mio bars x 1 asset 100y= 450.145 sec
  )