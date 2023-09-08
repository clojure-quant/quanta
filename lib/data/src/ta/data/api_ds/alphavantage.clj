(ns ta.data.api-ds.alphavantage
  (:require
    [taoensso.timbre :refer [info warn error]]
    [tick.core :as t]
    [tech.v3.dataset :as tds]
    [tablecloth.api :as tc]
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

(defn get-category-download-fn [category]
  (let [fun (get category-fn category)]
    (or fun av/get-daily)))


(defn symbol->provider [symbol]
   symbol)

(def interval-mapping
  {"D" "daily"})

(defn range->parameter [{:keys [start mode] :as range}]
  (if (= range :full) 
    "full"
    (if (= mode :append)
      "compact"
      "full"
      )))

(defn filter-rows-after-date [ds-bars dt]
  ;(info "filtering after date: " dt)
  (if dt
    (tc/select-rows ds-bars  (fn [row]
                               (t/>= (:date row) dt)))
    ds-bars))

(defn get-series [symbol interval range opts]
  (let [{:keys [category] :as instrument} (db/instrument-details symbol)
        symbol (symbol->provider symbol)
        period (get interval-mapping interval)
        av-get-data (get-category-download-fn category)]
    (-> (av-get-data (range->parameter range) symbol)
        (alphavantage-result->dataset)
        (filter-rows-after-date (:start range))
        )))


(comment 
  (require '[ta.helper.date :refer [parse-date]])
  (parse-date "2023-09-01")
  
  (-> (get-series "EURUSD" "D" 
              {:start (parse-date "2023-09-01")
               :mode :append} 
              {})
      (tc/info))
  
  (get-series "FSDCX" "D"
             {:start (parse-date "2023-01-01")
             :mode :append}
            {})
  

  
  ;
  )