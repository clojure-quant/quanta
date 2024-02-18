(ns notebook.backtest.sma-crossover.single-asset
  (:require
   [tick.core :as t]
   [ta.calendar.core :as cal]
   [ta.db.bars.protocol :as b]
   [ta.env.javelin.backtest :refer [run-backtest]]
   [ta.env.javelin.env :refer [create-env]]
   [ta.env.javelin.algo :as dsl]))

;; an environment needs to be created for each backtest;
;; this is because the excel-style calculation tree should only
;; calculate something relevant to the backtest
(def env (create-env :bardb-dynamic))

env

(defn window-as-date-time [window]
  {:start (t/date-time (:start window))
   :end (t/date-time (:end window))})

(def window (-> (cal/trailing-range-current [:us :d] 10)
                (window-as-date-time)))

window

;; quick test, dynamic bar-db (duckdb + import) is working
(def bdb (:bar-db env))
bdb
(b/get-bars bdb {:asset "AAPL"
                 :calendar [:us :d]
                 :import :kibot} window)

(def algo {:algo 'notebook.algo.sma3/bar-strategy
           :calendar [:us :d]
           :asset "AAPL"
           :import :kibot
           ; algo parameter:
           :trailing-n 1000
           :sma-length-st 10
           :sma-length-lt 59})

(def strategy (dsl/add-bar-strategy env algo))
strategy

(def window-4y (-> (cal/trailing-range-current [:us :d] 1000)
                   ;(window-as-date-time)
                   ))

window-4y


(run-backtest env window-4y)


@strategy

