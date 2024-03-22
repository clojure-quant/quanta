(ns ta.algo.backtest ;   ta.env.backtest
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [modular.system]
   [tick.core :as t]
   [ta.calendar.core :as cal]
   [ta.calendar.combined :refer [combined-event-seq]]
   [ta.engine.protocol :as eng]
   [ta.algo.env.protocol :as algo-env]
   [ta.algo.env :as algo-env-impl]))

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
  [bar-db-kw algo-spec]
  (let [bar-db  (modular.system/system bar-db-kw)
        env (algo-env-impl/create-env-javelin bar-db)
        strategy (algo-env/add-algo env algo-spec)
        calendar [:us :d]
        window (cal/trailing-range calendar 1)]
    (run-backtest env window)
    strategy))

(defn backtest-algo-date
  "run a single bar-strategy with data powered by bar-db-kw
   as of date dt. returns the result of the strategy."
  [bar-db-kw algo-spec dt]
  (let [;dt (-> (t/now)
        ;       (t/in "UTC"))
        bar-db  (modular.system/system bar-db-kw)
        env (algo-env-impl/create-env-javelin bar-db)
        strategy (algo-env/add-algo env algo-spec)
        engine (algo-env/get-engine env)
        calendars (eng/active-calendars engine)
        prior-dates (map (fn [[calendar-kw interval-kw]]
                           (cal/prior-close calendar-kw interval-kw dt))
                         calendars)
        ;prior-dates-sorted (sort prior-dates)
        event-seq (map (fn [cal dt]
                         {:calendar cal :time dt}) calendars prior-dates)]
    (info "event-seq: " event-seq)
    (doall (map #(eng/set-calendar! engine %) event-seq))
    strategy))

(comment

  (require '[ta.engine.javelin :as e])
  (require '[javelin.core-clj :refer [cell cell=]])
  (require '[ta.env.tools.window :as tw])

  ;(require '[modular.system])
  (def duckdb (:duckdb modular.system/system))

  (defn demo-backtest []
    (let [env (e/create-env duckdb)
          c (cal/get-calendar env [:us :h]) ; force creation of one calendar
          printer (cell= (println "now: " c)) ; track calendar time and print it.
          count-a (atom 0)
          inc-counter (fn [cell]
                        (swap! count-a inc))
          counter (cell= (inc-counter c))
          w (tw/trailing-years-window 1)]
      (run-backtest env w)
      @count-a))

  ;; this will print "now: DATE" for all hours in the last year
  ;; will return the number of hourly bars in the last year.
  (demo-backtest)

 ; 
  )
