(ns notebook.playground.algo.dummy
  (:require
   [ta.calendar.core :as cal]
   [ta.algo.create :as a]
   [ta.backtest.core :refer [backtest-algo run-backtest]]
   [ta.engine.protocol :as env]
   [ta.engine.javelin :refer [create-env]]))

;; 1. time-based algo spec

(defn secret [env spec time]
  (str "the spec is: " spec " (calculated: " time ")"))

(def spec {:type :time
           :calendar [:us :d]
           :data 42
           :algo 'notebook.playground.algo.dummy/secret})

;; 2. test algo calculation

(def algo (a/create-algo spec))

algo

(algo nil {:data :v42} :now)

;; 3. backtest with simple syntax

(def result
  (backtest-algo :duckdb spec))

result
;; => "the spec is: {:type :time, :calendar [:us :d], :data 42, :algo notebook.playground.algo.dummy/secret} (calculated: 2024-02-23T17:00-05:00[America/New_York])"


;; 4. backtest with compex syntax

(def env (create-env :duckdb))

(def window (cal/trailing-range [:us :d] 1))

window

(def strategy (env/add-algo env spec))

strategy

(run-backtest env window)

@strategy
;; => "the spec is: {:type :time, :calendar [:us :d], :data 42, :algo notebook.playground.algo.dummy/secret} (calculated: 2024-02-23T17:00-05:00[America/New_York])"
