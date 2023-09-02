(ns ta.data-ds.kibot
  (:require
   [clojure.java.io :as io]
   [tick.core :as t]
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.data.kibot :as kibot]
   [ta.warehouse :as wh]
   [ta.warehouse.since-importer :as since-importer]))

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