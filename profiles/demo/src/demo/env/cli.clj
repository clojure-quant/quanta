(ns demo.env.cli
  (:require
   [webly.log]
   [demo.warehouse.import-alphavantage :as av]
   [demo.warehouse.import-bybit :as bybit]
   [demo.warehouse.create-random :as rand])
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
  (get-alphavantage-daily alphavantage-test-symbols)
  (get-alphavantage-daily fidelity-symbols))

(defn run-bybit-import-initial [& _]
  (log-config!)
  (init-bybit-daily)
  (init-bybit-15))

(defn run-bybit-import-append [& _]
  (log-config!)
  (append-bybit-daily)
  (append-bybit-15))

(defn run-create-random [& _]
  (log-config!)
  (create-crypto-random))


(defn -main
  ([]
   (println "printing default list: currency")
   (print-symbol-list "currency"))
  ([list-name]
   (println "printing user defined list: " list-name)
   (print-symbol-list list-name)))

;  (-main "currency")

;  (-main "fidelity-select")