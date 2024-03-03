(ns notebook.strategy.sma-crossover.multiple-assets
  (:require
   [ta.engine.javelin :refer [create-env]]
   [ta.engine.javelin.algo :as dsl]
   [ta.env.backtest :refer [run-backtest]]
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
(def strategies (dsl/add-algos env algos-fx))

(def window (cal/trailing-range [:us :d] 10))

window


(run-backtest env window)


strategies
@(first strategies)
@(last strategies)
