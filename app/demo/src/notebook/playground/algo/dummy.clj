(ns notebook.playground.algo.dummy
  (:require
   [ta.calendar.core :as cal]
   [ta.engine.protocol :as eng]
   [ta.algo.env :as algo-env]
   [ta.algo.env.backtest :refer [backtest-algo run-backtest]]))

;; 1. time-based algo spec

(defn secret [env spec time]
  (str "the spec is: " spec " (calculated: " time ")"))

(def spec {:type :time
           :calendar [:us :d]
           :data 42
           :algo 'notebook.playground.algo.dummy/secret})

(def e (algo-env/create-env-javelin nil))

(def algo (algo-env/add-algo e spec))

algo

;; 2. test algo calculation

(def engine (algo-env/get-engine e))

engine

(eng/set-calendar! engine {:calendar [:us :d] :time :evening})

algo
@algo

;; 3. backtest with complex syntax

(def window (cal/trailing-range [:us :d] 1))

e
(run-backtest e window)

@algo
;; => "the spec is: {:type :time, :calendar [:us :d], :data 42, :algo notebook.playground.algo.dummy/secret} (calculated: 2024-02-26T17:00-05:00[America/New_York])"


;; 4. backtest with simple syntax

(def result
  (backtest-algo :duckdb spec))

result
;; => "the spec is: {:type :time, :calendar [:us :d], :data 42, :algo notebook.playground.algo.dummy/secret} (calculated: 2024-02-23T17:00-05:00[America/New_York])"

;; 5. backtest with formulas.

(defn combine [_env spec & args]
{:spec spec :args args})
  
(def combined-spec 
  [:a {:calendar [:us :h] :algo 'notebook.playground.algo.dummy/secret :type :time}
   :b {:calendar [:us :m] :algo 'notebook.playground.algo.dummy/combine :type :time}
   ;:c {:formula [:a :b] :algo 'notebook.playground.algo.dummy/combine :type :time}
   ])

(require '[ta.algo.spec :refer [spec->ops]])
(spec->ops e combined-spec)



(def combined-result
  (backtest-algo :duckdb combined-spec))

