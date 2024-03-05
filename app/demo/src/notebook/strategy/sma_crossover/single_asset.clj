(ns notebook.strategy.sma-crossover.single-asset
  (:require
   [tick.core :as t]
   [ta.calendar.core :as cal]
   [ta.db.bars.protocol :as b]
   [ta.engine.protocol :as p]
   [ta.algo.backtest :refer [run-backtest]]
   [modular.system]))


(-> (cal/trailing-range [:us :d] 10)
    :end
    t/instant)


(defn window-as-instant [window]
  {:start (t/instant (:start window))
   :end (t/instant (:end window))})

(def window (-> (cal/trailing-range [:us :d] 10)
                (window-as-instant)))

window

;; quick test, dynamic bar-db (duckdb + import) is working
(def db (modular.system/system :duckdb))
(def db-dyn (modular.system/system :bardb-dynamic))
db
db-dyn
  
(b/get-bars db {:asset "AAPL"
                :calendar [:us :d]
                :import :kibot} window)

(b/get-bars db {:asset "BTCUSDT"
                :calendar [:crypto :m]
                :import :bybit}
            (window-as-instant
             (cal/trailing-range [:crypto :m] 10)))


(def algo-spec {:type :trailing-bar
                :algo 'notebook.strategy.sma-crossover.algo/bar-strategy
                :calendar [:us :d]
                :asset "AAPL"
                :import :kibot
           ; algo parameter:
                :trailing-n 1000
                :sma-length-st 10
                :sma-length-lt 59})

(def strategy (p/add-algo env algo-spec))
strategy

(def window-4y (-> (cal/trailing-range [:us :d] 1)
                   ;(window-as-date-time)
                   ))

window-4y


(run-backtest env window-4y)


@strategy

