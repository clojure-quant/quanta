(ns quanta.notebook.algo-dummy
  (:require
   [ta.calendar.core :as cal]
   [quanta.model.protocol :as eng]
   [ta.algo.env :as algo-env]
   [ta.algo.backtest :refer [backtest-algo run-backtest]]))

;; 1. time-based algo spec

(defn secret [env spec time]
  (str "the spec is: " spec " (calculated: " time ")"))

(def spec {:type :time
           :calendar [:us :d]
           :data 42
           :algo 'quanta.notebook.algo-dummy/secret})

(def e (algo-env/create-env-javelin nil))

(def algo (algo-env/add-algo e spec))

algo

;; 2. test algo calculation

(def engine (algo-env/get-engine e))

engine

(eng/set-calendar! engine {:calendar [:us :d] :time :evening})

algo
@algo
;; => "the spec is: {:type :time, :calendar [:us :d], :data 42, :algo notebook.playground.algo.dummy/secret} (calculated: :evening)"

;; 3. backtest with complex syntax

(def window (cal/trailing-range [:us :d] 1))

e
(run-backtest e window)

@algo
;; => "the spec is: {:type :time, :calendar [:us :d], :data 42, :algo notebook.playground.algo.dummy/secret} (calculated: 2024-02-26T17:00-05:00[America/New_York])"

;; 4. backtest with simple syntax

(def result
  (backtest-algo :duckdb spec))

@result
;; => "the spec is: {:type :time, :calendar [:us :d], :data 42, :algo notebook.playground.algo.dummy/secret} (calculated: 2024-02-26T17:00-05:00[America/New_York])"

;; 5. backtest with formulas.

(defn combine [env spec & args]
  {:args args})

(defn sum [env spec & args]
  (apply + args))

(def combined-spec
  [:a {:calendar [:us :d] :algo 'quanta.notebook.algo-dummy/secret :type :time}
   :b {:type :time :calendar [:us :d] :data 42 :algo 'quanta.notebook.algo-dummy/secret}
   :c {:value 4444}
   :d {:formula [:a :b] :algo 'quanta.notebook.algo-dummy/combine :type :time}
   :e {:value 2222}
   :f {:formula [:c :e] :algo 'quanta.notebook.algo-dummy/sum :type :time}])

(require '[ta.algo.spec.ops :refer [spec->ops]])
(spec->ops e spec)
(spec->ops e combined-spec)

(def combined-result
  (backtest-algo :duckdb combined-spec))

@(:a combined-result)
@(:b combined-result)
@(:c combined-result)
@(:d combined-result)
@(:f combined-result)
combined-result
