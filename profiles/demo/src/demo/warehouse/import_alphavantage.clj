(ns demo.warehouse.import-alphavantage
  (:require
   [tech.v3.dataset :as tds]
   [ta.data.alphavantage :as av]
   [ta.warehouse.since-importer :as since-importer]
   [demo.env.config :refer [log-config!]] ; side effects
   ))

(defn alphavantage-get-since-ds [_ #_frequency _ #_since symbol]
  (-> (av/get-daily "full" symbol)
      (tds/->dataset)))

(def alphavantage-symbols
  ["SPY" "EURUSD" "MSFT" "ORCL"]
  ;(wh/load-list w-stocks "fidelity-select")  
  )

(defn init-alphavantage-daily []
  (let  [start-date-dummy nil]
    (since-importer/init-symbols
     :stocks alphavantage-get-since-ds "D"
     start-date-dummy alphavantage-symbols)))

(defn task-alphavantage-import-initial [& _]
  (log-config!)
  (init-alphavantage-daily))

; ********************************************************************************************+
(comment

  (init-alphavantage-daily)

;
  )