(ns notebook.playground.live.result-printer
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [manifold.stream :as s]
   [ta.env.live-bargenerator :as env]))

(defn print-result [result]
  (info ">>> result: " result))

(defn start-print-results [live]
  (s/consume print-result (env/get-result-stream live)))

(comment
  (require '[modular.system])
  (def live (:live modular.system/system))
  live

  (start-print-results live)

  {:time "#inst 2024-02-09T20:39:00.000000000-00:00"
   :category [:us :m]
   :algo "#function[ta.env.dsl.barstrategy/trailing-window-algo-run]",
   :algo-opts {:feed :fx, :sma-length-st 2,
               :label :sma-crossover-1m,
               :algo-calc #'notebook.algo.sma3/bar-strategy,
               :algo-ns 'notebook.algo.sma3,
               :trailing-n 5,
               :id "9iDsMl",
               :sma-length-lt 3,
               :bar-category [:us :m],
               :asset "EUR/USD"}
   :result :whatever}

;  
  )




