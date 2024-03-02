(ns notebook.playground.bardb.duck
  (:require
   [tick.core :as t]
   [ta.db.bars.protocol :refer [bardb] :as b]
   [modular.system]))

;(def db (modular.system/system :duckdb))
(def db (modular.system/system :bardb-dynamic))


(def window {:start (t/instant "2023-02-01T20:00:00Z")
             :end (t/instant "2024-03-01T20:00:00Z")})


(b/get-bars db
           {:asset "EUR/USD"
            :calendar [:us :d]
            :import :kibot
            }
            window)


(b/get-bars db {:asset "USD/JPY"
                :calendar [:us :m]}
               window)

(b/get-bars db {:asset "BTCUSDT"
                :import :bybit
                :calendar [:crypto :m]}
               window)


(b/get-bars db
            {:asset "EUR/USD"
             :calendar [:us :d]
             :import :kibot}
            {:start (t/instant "2024-02-29T05:00:00Z")
             :end (t/instant "2024-03-01T05:00:00Z")})
{:type "forex", :symbol "EURUSD", :startdate "2024-02-29", :enddate "2024-03-01", 
 :interval "daily", :timezone "UTC", :splitadjusted 1}






2024-03-02T23:00:55.021Z nuc12 INFO [ta.db.bars.dynamic:11] - dynamic get-bars 
{:asset "EUR/USD", :calendar [:us :d], :import :kibot} {:start #inst "2023-02-01T20:00:00.000000000-00:00",
                                                        :end #inst "2024-03-01T20:00:00.000000000-00:00"}
2024-03-02T23:00:55.022Z nuc12 INFO [ta.db.bars.dynamic.import:74] - import-on-demand  
{:asset "EUR/USD", :calendar [:us :d], :import :kibot} {:start #inst "2023-02-01T20:00:00.000000000-00:00", 
                                                        :end #inst "2024-03-01T20:00:00.000000000-00:00"}
2024-03-02T23:00:55.028Z nuc12 INFO [ta.db.bars.dynamic.import:76] - tasks: 

({:type :missing-prior, :start #inst "2023-02-01T20:00:00.000000000-00:00", 
  :end #inst "2023-11-10T22:00:00.000-00:00", 
  :db {:start #inst "2023-02-01T20:00:00.000000000-00:00"}}
 {:type :missing-after, :start #inst "2024-02-29T22:00:00.000-00:00", 
  :end #inst "2024-03-01T20:00:00.000000000-00:00", 
  :db {:end #inst "2024-03-01T20:00:00.000000000-00:00"}})
2024-03-02T23:00:55.028Z nuc12 INFO [ta.import.provider.kibot.ds:90] - get-bars kibot  EUR/USD 
[:us :d]   {:type :missing-prior, :start #inst "2023-02-01T20:00:00.000000000-00:00", 
            :end #inst "2023-11-10T22:00:00.000-00:00", :db {:start #inst "2023-02-01T20:00:00.000000000-00:00"}}  ..
2024-03-02T23:00:55.028Z nuc12 INFO [ta.import.provider.kibot.ds:97] - kibot make request interval:  
daily  range:  {:startdate "2023-02-01", :enddate "2023-11-10"} asset-kibot:  {:type "forex", :symbol "EURUSD"}
2024-03-02T23:00:55.029Z nuc12 INFO [ta.import.provider.kibot.raw:71] - kibot history:  {:type "forex", :symbol "EURUSD", :startdate "2023-02-01", :enddate "2023-11-10", :interval "daily", :timezone "UTC", :splitadjusted 1}
2024-03-02T23:00:56.394Z nuc12 INFO [ta.import.provider.kibot.raw:50] - kibot response status:  200
2024-03-02T23:00:56.395Z nuc12 INFO [ta.import.provider.kibot.ds:103] - kibot request finished!
2024-03-02T23:00:56.395Z nuc12 INFO [ta.import.provider.kibot.ds:90] - get-bars kibot  EUR/USD   [:us :d]   {:type :missing-after, :start #inst "2024-02-29T22:00:00.000-00:00", :end #inst "2024-03-01T20:00:00.000000000-00:00", :db {:end #inst "2024-03-01T20:00:00.000000000-00:00"}}  ..
2024-03-02T23:00:56.395Z nuc12 INFO [ta.import.provider.kibot.ds:97] - kibot make request interval:  daily  range:  {:startdate "2024-02-29", :enddate "2024-03-01"} asset-kibot:  {:type "forex", :symbol "EURUSD"}
2024-03-02T23:00:56.395Z nuc12 INFO [ta.import.provider.kibot.raw:71] - kibot history: 
