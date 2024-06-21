(ns quanta.notebook.algo-backtest
    (:require 
     [modular.system]
     [javelin.core-clj :refer [cell cell=]]
     [ta.calendar.core :as cal]
     [ta.engine.javelin :as e]
     [ta.env.tools.window :as tw]
     [ta.algo.backtest :as backtest]
     ))
  

 (def duckdb (:duckdb modular.system/system))

  (defn demo-backtest []
    (let [calendar [:us :h]
          env (e/create-env duckdb)
          c (cal/get-calendar env calendar) ; force creation of one calendar
          printer (cell= (println "now: " c)) ; track calendar time and print it.
          count-a (atom 0)
          inc-counter (fn [cell]
                        (swap! count-a inc))
          counter (cell= (inc-counter c))
          w (tw/trailing-years-window 1)
          trailing-n 100
          w (cal/trailing-window calendar trailing-n time)
          ]
      (backtest/run-backtest env w)
      @count-a))

  ;; this will print "now: DATE" for all hours in the last year
  ;; will return the number of hourly bars in the last year.
  (demo-backtest)
