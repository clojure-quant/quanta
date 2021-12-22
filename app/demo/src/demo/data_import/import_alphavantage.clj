(ns demo.data-import.import-alphavantage
  (:require
   [tech.v3.dataset :as tds]
   [ta.warehouse :as wh]
   [ta.warehouse.since-importer :as since-importer]
   [ta.data.alphavantage :as av]))

; stocks

(defn alphavantage-get-since-ds [_ #_frequency _ #_since symbol]
  (-> (av/get-daily "full" symbol) ; (av/get-daily-adjusted "full" symbol)
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

; ********************************************************************************************+
(comment

  (av/get-daily-adjusted "compact" "MSFT")
  (av/get-daily-adjusted "full" "MSFT")

  (def symbols (wh/load-list "test"))

  (get-alphavantage-daily symbols)
  (get-alphavantage-fx-daily symbols)
;
  )