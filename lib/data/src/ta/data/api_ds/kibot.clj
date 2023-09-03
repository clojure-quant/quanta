(ns ta.data.api-ds.kibot
  (:require
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


(defn get-series [symbol interval range opts]
  (let [symbol-map (symbol->provider symbol)
        period (get interval-mapping interval)
        ]
    (-> (merge symbol-map
               {:interval period
                :period 1
                :timezone "UTC"
                :splitadjusted 1})
        (kibot/history) 
        (kibot-result->dataset))))


(comment 
    (symbol->provider "MSFT")

    (get-series "MSFT"
                "D"
                {:from "2000"}
                {})
  
  
  (symbol->provider "IBM")
  
  (get-series "IBM"
              "D"
              {:from "2000"}
              {})
  
  )

