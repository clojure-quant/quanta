(ns ta.data.api-ds.kibot
  (:require
    [clojure.string :as str]
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
  (cond 
    (= range :full)
    {:period 100000}

    (= range 1)
    {:period 1}

    :else
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

(defn symbols->str [symbols]
  (->> (interpose "," symbols)
       (apply str)))

(defn provider->symbol [provider-symbol]
  (if-let [inst (db/get-instrument-by-provider :kibot provider-symbol)]
    (:symbol inst)
    provider-symbol))

(comment
  (symbol->provider "MES0")
  ;; => {:type "futures", :symbol "ES"}
  (provider->symbol "ES")

  (symbols->str ["MSFT" "ORCL"])
  (symbols->str ["ES"]))


; 

(defn symbol-conversion [col-symbol]
  (map provider->symbol col-symbol)
  )

(defn kibot-snapshot-result->dataset [csv]
  (-> (tds/->dataset (string->stream csv)
                     {:file-type :csv
                      :header-row? true
                      :key-fn (comp keyword str/lower-case )
                      :dataset-name "kibot-snapshot"})
      (tc/update-columns {:symbol symbol-conversion})
      (tc/rename-columns {(keyword ":404 symbol not foundsymbol")
                          :symbol
                          })
      ;(tc/convert-types :date [[:local-date-time date->localdate]])
      ))

(defn get-snapshot [symbol]
  (let [symbols-kibot (->> symbol 
                          (map symbol->provider)
                          (map :symbol))
        result (kibot/snapshot {:symbol (symbols->str symbols-kibot)})]
    (if-let [error? (:error result)]
      (do (error "kibot request error: " error?)
          nil)
      (kibot-snapshot-result->dataset result))))



(comment 
  
    (get-snapshot ["AAPL"])
    (get-snapshot ["NG0"])
    (get-snapshot ["CL0"])
    (get-snapshot ["MES0"])
    (get-snapshot ["RIVN" "AAPL" "MYM0"])
  

"RIVN" "MYM0" "RB0" "GOOGL" "FCEL"
"NKLA" "M2K0" "INTC" "MES0" "RIG"
"ZC0" "FRC" "AMZN" "HDRO" "MNQ0"
"BZ0" "WFC" "DAX0" "PLTR" "NG0"
  

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

