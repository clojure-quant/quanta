(ns demo.data-import.import-quandl
  (:require
   [tablecloth.api :as tc]
   [ta.warehouse :as wh]
   [ta.warehouse.since-importer :as since-importer]
   [ta.data.quandl :as quandl]
   [ta.helper.date :refer [parse-date]]
   ))

(defn quandl-result->ds [{:keys [data columns]}]
  (-> (tc/dataset data {:column-names columns})
      (tc/rename-columns {"Date" :date
                          "Open" :open
                          "High" :high
                          "Low" :low
                          "Settle" :close
                          "Volume" :volume
                          "Prev. Day Open Interest" :open-interest-1
                          })
      (tc/convert-types :date [[:local-date-time parse-date]])
      ))


(defn quandl-get-since-ds [_frequency _since symbol]
  (let [quandl-result (quandl/cfuture-request symbol {})]
    (quandl-result->ds quandl-result)
      ))

(def dax-full (quandl/cfuture-request "FDAX" {}))
(def dax-2023 (quandl/cfuture-request "FDAX" {:start_date "2021-06-25" }))

(def gold-2023 (quandl/cfuture-request "GC" {:start_date "2021-01-01"}))

(quandl/quandl-metadata "CHRIS/EUREX_FDAX1" {})

(quandl/quandl-metadata "CHRIS/CME_GC1" {})


(quandl/quandl-metadata "SCF/CBOE_VX2N" {})


; WTI spot
(quandl/quandl-metadata "EIA/PET_RWTC_D" {})





gold-2023

quandl-result
quandl-result-2023

(quandl-result->ds quandl-result)



;(quandl-get-since-ds "D" "2021-01-01" "FDAX")
 


#_(defn get-alphavantage-daily [symbols]
  (let  [start-date-dummy nil]
    (since-importer/init-symbols
     :stocks alphavantage-get-since-ds "D"
     start-date-dummy symbols)))


; ********************************************************************************************+
(comment

  (def symbols (wh/load-list "test"))

 ; (get-alphavantage-daily ["QQQ" "SPY" "TLT"])
  
;
  )