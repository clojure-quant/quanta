(ns ta.algo.backtest
  (:require
   [quanta.model.backtest :refer [fire-backtest-events]]
   [ta.algo.env.protocol :as algo-env]
   [ta.algo.env :refer [create-env-javelin]]))

(defn backtest-algo
  "runs an algo with data powered by bar-db.
   returns the result of the strategy."
  [bar-db algo-spec window-or-dt]
  (let [env (create-env-javelin bar-db)
        strategy (algo-env/add-algo env algo-spec)
        model (algo-env/get-model env)]
    (fire-backtest-events model window-or-dt)
    strategy))