(ns demo.data-import.import-alphavantage
  (:require
   [tech.v3.dataset :as tds]
   [ta.warehouse :as wh]
   [ta.warehouse.since-importer :as since-importer]
   [ta.data.alphavantage :as av]))

; stocks

(defn alphavantage-get-since-ds [_ #_frequency _ #_since symbol]
  (-> (av/get-daily-adjusted "full" symbol)
      :series
      (tds/->dataset)))

(defn get-alphavantage-daily [symbols]
  (let  [start-date-dummy nil]
    (since-importer/init-symbols
     :stocks alphavantage-get-since-ds "D"
     start-date-dummy symbols)))

; fx

(defn alphavantage-get-fx-since-ds [_ #_frequency _ #_since symbol]
  (-> (av/get-daily-fx "full" symbol)
      :series
      (tds/->dataset)))

(defn get-alphavantage-fx-daily [symbols]
  (let  [start-date-dummy nil]
    (since-importer/init-symbols
     :stocks alphavantage-get-fx-since-ds "D"
     start-date-dummy symbols)))

;; symbol lists

(def alphavantage-test-symbols
  ["IAU"
   "SPY"
   "QQQ"
   "GLD"
   "SLV"
   "MSFT"
   "ORCL"])

(def alphavantage-fx-symbols
  ["EURUSD"
   "USDJPY"])

(def fidelity-symbols
  (wh/load-list "fidelity-select"))

(def tradingview-symbols

  (->> (wh/load-lists-full ["fidelity-select"
                            "bonds"
                            "commodity-industry"
                            "commodity-sector"
                            "currency"
                            "equity-region"
                            "equity-region-country"
                            "equity-sector-industry"
                            "equity-style"
                            "test"])
       (map :symbol)))

; ********************************************************************************************+
(comment

  (av/get-daily-adjusted "compact" "MSFT")
  (av/get-daily-adjusted "full" "MSFT")

  (get-alphavantage-daily alphavantage-test-symbols)
  (get-alphavantage-daily fidelity-symbols)

  (get-alphavantage-fx-daily alphavantage-fx-symbols)
;
  )