(ns demo.warehouse.import-alphavantage
  (:require
   [tech.v3.dataset :as tds]
   [ta.warehouse :as wh]
   [ta.warehouse.since-importer :as since-importer]
   [ta.data.alphavantage :as av]
   [demo.env.config :refer [log-config!]]))

(defn alphavantage-get-since-ds [_ #_frequency _ #_since symbol]
  (-> (av/get-daily "full" symbol)
      (tds/->dataset)))

(def alphavantage-test-symbols
  ["IAU"
   "GLD"
   "SPY"
   "QQQ"
   "EURUSD"
   "MSFT"
   "ORCL"])

(def fidelity-symbols
  (wh/load-list "fidelity-select"))

(defn get-alphavantage-daily [symbols]
  (let  [start-date-dummy nil]
    (since-importer/init-symbols
     :stocks alphavantage-get-since-ds "D"
     start-date-dummy symbols)))

(defn task-alphavantage-import-initial [& _]
  (log-config!)
  (get-alphavantage-daily alphavantage-test-symbols)
  (get-alphavantage-daily fidelity-symbols))

; ********************************************************************************************+
(comment

  (get-alphavantage-daily alphavantage-test-symbols)
  (get-alphavantage-daily fidelity-symbols)

;
  )