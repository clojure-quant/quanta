(ns notebook.playground.bardb.duck
  (:require
   [tick.core :as t]
   [ta.db.bars.protocol :refer [bardb] :as b]
   [modular.system]))

;(def db (modular.system/system :duckdb))
(def db (modular.system/system :bardb-dynamic))


(def window {:start (t/instant "2024-02-01T20:00:00Z")
             :end (t/instant "2024-03-01T20:00:00Z")})


(b/get-bars db
           {:asset "EUR/USD"
            :calendar [:us :m]}
            window)

(b/get-bars db {:asset "USD/JPY"
                   :calendar [:us :m]}
               window)

(b/get-bars db {:asset "BTCUSDT"
                :import :bybit
                :calendar [:crypto :m]}
               window)







