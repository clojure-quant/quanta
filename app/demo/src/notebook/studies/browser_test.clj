(ns notebook.studies.browser-test
  (:require
   [ta.algo.manager :as am]
   [ta.trade.roundtrip-backtest :refer [run-backtest]]
   ))


(am/algo-run
 "buy-hold"
 {:symbol "TLT"})


(let [{:keys [algo options]} (am/get-algo "buy-hold")]
  (run-backtest algo (merge options {:symbol "TLT"})))



(am/algo-run-browser 
  "buy-hold" 
 {:symbol "TLT"})

