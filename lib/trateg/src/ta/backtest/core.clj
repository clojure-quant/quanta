(ns ta.backtest.core
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [ta.calendar.core :as cal]
   [ta.calendar.combined :refer [combined-event-seq]]
   [ta.env.protocol :as env]
   [ta.env.javelin :refer [create-env]]))

(defn run-backtest [e w]
  (let [cals (env/active-calendars e)
        event-seq (combined-event-seq w cals)]
    (info "backtesting window: " w " ..")
    (doall (map #(env/set-calendar! e %) event-seq))
    (info "backtesting window: " w "finished!")
    :backtest-finished))


(defn backtest-algo
  "run a single bar-strategy with data powered by bar-db-kw.
   returns the result of the strategy."
  [bar-db-kw algo-spec]
  (let [env (create-env bar-db-kw)
        calendar (:calendar algo-spec)
        strategy (env/add-algo env algo-spec)
        window (cal/trailing-range calendar 1)]
    (run-backtest env window)
    @strategy))


(comment

  (require '[ta.env.javelin :as e])
  (require '[javelin.core-clj :refer [cell cell=]])
  (require '[ta.env.tools.window :as tw])

  (require '[modular.system])
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
