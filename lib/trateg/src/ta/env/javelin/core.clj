(ns ta.env.javelin.core 
  (:require 
    [ta.calendar.core :as cal]
    [ta.env.javelin.backtest :refer [run-backtest]]
    [ta.env.javelin.env :refer [create-env]]
    [ta.env.javelin.algo :as dsl]))

(defn backtest-single-bar-strategy 
  "run a single bar-strategy with data powered by bar-db-kw.
   returns the result of the strategy."
  [bar-db-kw algo-spec]
  (let [env (create-env bar-db-kw)
        calendar (:calendar algo-spec)
        strategy (dsl/add-bar-strategy env algo-spec)
        window (cal/trailing-range calendar 1)]
    (run-backtest env window)
    @strategy))