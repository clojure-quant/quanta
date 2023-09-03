(ns ta.data.api-ds.alphavantage
  (:require
   [tech.v3.dataset :as tds]
   [ta.data.api.alphavantage :as av]
   [ta.warehouse.symbol-db :as db]))

(defn alphavantage-result->dataset [response]
  (-> response
      :series
      (tds/->dataset)))

(def category-fn
  {:equity av/get-daily
   :fx av/get-daily-fx
   :crypto av/get-daily-crypto})


(defn symbol->provider [symbol]
   symbol)

(def interval-mapping
  {"D" "daily"})


(defn get-series [symbol interval range opts]
  (let [{:keys [category] :as instrument} (db/instrument-details symbol)
        symbol (symbol->provider symbol)
        period (get interval-mapping interval)
        av-get-data (get category-fn category)]
    (-> (av-get-data "full" symbol)
        (alphavantage-result->dataset))))
