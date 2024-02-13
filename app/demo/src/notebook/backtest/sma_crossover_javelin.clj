(ns notebook.backtest.sma-crossover-javelin
  (:require
   [ta.calendar.window :as win]
   [ta.env.javelin.backtest :refer [run-backtest]]
   [ta.env.javelin.env :refer [create-env-duckdb]]
   [ta.env.javelin.algo :as dsl]
   [notebook.algo-config.simple-sma-crossover :refer [algos-fx]]))

(def env (create-env-duckdb))

env

; adding bar-strategies to environment, will automatically 
; start calculations with time=nil 
; returns seq of javelin cells, which can be used for further 
; processing
(def strategies (dsl/add-bar-strategies env algos-fx))

(def  w (win/recent-days-window 1))
w
(run-backtest env w)


strategies
@(first strategies)
@(last strategies)
