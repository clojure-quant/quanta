(ns demo.env.cli
  (:require
   [webly.log]
   [demo.warehouse.import-alphavantage :as av]
   [demo.warehouse.import-bybit :as bybit]
   [demo.warehouse.create-random :as rr]
   [demo.playground.symbollist :refer [print-symbol-list]])
  (:gen-class))

(defn log-config! []
  (webly.log/timbre-config!
   {:timbre-loglevel
    [[#{"pinkgorilla.nrepl.client.connection"} :info]
     [#{"org.eclipse.jetty.*"} :info]
     [#{"webly.*"} :info]
     [#{"*"} :info]]}))

;; tasks (for cli use)

(defn run-alphavantage-import-initial [& _]
  (log-config!)
  (av/get-alphavantage-daily av/alphavantage-test-symbols)
  (av/get-alphavantage-daily av/fidelity-symbols))

(defn run-bybit-import-initial [& _]
  (log-config!)
  (bybit/init-bybit-daily)
  (bybit/init-bybit-15))

(defn run-bybit-import-append [& _]
  (log-config!)
  (bybit/append-bybit-daily)
  (bybit/append-bybit-15))

(defn run-create-random [& _]
  (log-config!)
  (rr/create-crypto-shuffled))

(defn -main
  ([]
   (println "printing default list: currency")
   (print-symbol-list "currency"))
  ([list-name]
   (println "printing user defined list: " list-name)
   (print-symbol-list list-name)))

;  (-main "currency")

;  (-main "fidelity-select")