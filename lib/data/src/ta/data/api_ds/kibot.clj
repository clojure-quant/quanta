(ns ta.data.api-ds.kibot
  (:require
    [taoensso.timbre :refer [info warn error]]
    [clojure.java.io :as io]
    [tick.core :as t]
    [tech.v3.dataset :as tds]
    [tablecloth.api :as tc]
    [ta.data.api.kibot :as kibot]
    [ta.warehouse.symbol-db :as db]))

(defn string->stream [s]
  (io/input-stream (.getBytes s "UTF-8")))

(defn date->localdate [d]
   (t/at d (t/time "00:00:00")))

(defn kibot-result->dataset [csv]
  (-> (tds/->dataset (string->stream csv)
                     {:file-type :csv
                      :header-row? false
                      :dataset-name "kibot-bars"
                      })
       (tc/rename-columns {"column-0" :date
                           "column-1" :open
                           "column-2" :high
                           "column-3" :low
                           "column-4" :close
                           "column-5" :volume})
      (tc/convert-types :date [[:local-date-time date->localdate]])
   ))
   

(comment
  (def csv "09/01/2023,26.73,26.95,26.02,26.1,337713\r\n")
  (def csv
    (kibot/history {:symbol "SIL" ; SIL - ETF
                   :interval "daily"
                   :period 1
                   :type "ETF" ; Can be stocks, ETFs forex, futures.
                   :timezone "UTC"
                   :splitadjusted 1}))
 csv

 (-> (kibot-result->dataset csv)
     (tc/info :columns)
  )
 
 ;
)

(def category-mapping
  {:equity "stocks"
   :etf "ETF"
   :future "futures"
   :fx "forex"})

(defn symbol->provider [symbol]
  (let [{:keys [category kibot] :as instrument} (db/instrument-details symbol)
        type (get category-mapping category)
        symbol (if kibot kibot symbol)
        ]
    {:type type
     :symbol symbol}))

(def interval-mapping
   {"D" "daily"})


 (defn fmt-yyyymmdd [dt]
   (t/format (t/formatter "YYYY-MM-dd") dt))

(defn range->parameter [{:keys [start] :as range}]
  (if (= range :full)
    {:period 100000}
    {:startdate (fmt-yyyymmdd start)} ;  :startdate "2023-09-01"
    ))

(defn get-series [symbol interval range opts]
  (let [symbol-map (symbol->provider symbol)
        period (get interval-mapping interval)
        range-kibot (range->parameter range)
        result (kibot/history (merge symbol-map
                                     range-kibot
                                     {:interval period
                                      :timezone "UTC"
                                      :splitadjusted 1}))]
        (if-let [error? (:error result)]
          (do (error "kibot request error: " error?) 
              nil)
          (kibot-result->dataset result))))


(comment 
    (symbol->provider "MSFT")

    (require '[ta.helper.date :refer [parse-date]])
    (parse-date "2023-09-01")
  
    (get-series "MSFT"
                "D"
                {:start (parse-date "2023-09-06")}
                {})
  
    (symbol->provider "EURUSD")
    (get-series "EURUSD"
              "D"
              {:start (parse-date "2023-09-01")}
              {})
  
   (symbol->provider "IJH")
  (get-series "IJH"
              "D"
              {:start (parse-date "2023-09-01")}
              {})
  
  
  (get-series "IBM"
              "D"
              {:start (parse-date "2023-09-07")}
              {})
  
  )

