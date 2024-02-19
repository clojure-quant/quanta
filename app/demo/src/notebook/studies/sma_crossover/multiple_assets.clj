(ns notebook.studies.sma-crossover.multiple-assets
  (:require
   [ta.env.javelin.backtest :refer [run-backtest]]
   [ta.env.javelin.env :refer [create-env]]
   [ta.env.javelin.algo :as dsl]
   [notebook.algo-config.simple-sma-crossover :refer [algos-fx]]
   [ta.db.bars.protocol :as b]
   [tick.core :as t]
   [ta.calendar.core :as cal]
   ))

(def env (create-env :bardb-dynamic))

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
