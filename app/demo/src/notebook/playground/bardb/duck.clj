(ns notebook.playground.bardb.duck
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.db.bars.protocol :as b]
   [modular.system]))

(def db (modular.system/system :duckdb))
;(def db (modular.system/system :bardb-dynamic))


(def window {:start (t/instant "2022-03-05T00:00:00Z")
             :end (t/instant "2024-03-06T20:00:00Z")})


(b/get-bars db
           {:asset "EUR/USD"
            :calendar [:forex :d]
            :import :kibot
            }
            window)

(b/get-bars db
            {:asset "UUP"
             :calendar [:us :d]
             :import :kibot}
            window)


(b/get-bars db {:asset "USD/JPY"
                :calendar [:us :m]}
               window)

(-> (b/get-bars db {:asset "BTCUSDT"
                    ;:import :bybit
                    :calendar [:crypto :d]}
                window)
    (tc/select-columns [:date :close :volume :epoch :ticks]))

(b/get-bars db
            {:asset "EUR/USD"
             :calendar [:us :d]
             :import :kibot}
            {:start (t/instant "2024-02-29T05:00:00Z")
             :end (t/instant "2024-03-01T05:00:00Z")})
{:type "forex", :symbol "EURUSD", :startdate "2024-02-29", :enddate "2024-03-01", 
 :interval "daily", :timezone "UTC", :splitadjusted 1}