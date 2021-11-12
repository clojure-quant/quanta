(ns demo.env.cli
  (:require
   [webly.log]
   [demo.env.config] ; side-effects
   [demo.data-import.import-alphavantage :as av]
   [demo.data-import.import-bybit :as bybit]
   [demo.data-import.create-random :as rr])
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
  ;(av/get-alphavantage-daily av/alphavantage-test-symbols)
  ;(av/get-alphavantage-daily av/fidelity-symbols)
  (av/get-alphavantage-daily av/tradingview-symbols))

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
   (println "printing default list: currency")))

;  (-main "currency")

;  (-main "fidelity-select")