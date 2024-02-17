(ns notebook.playground.duck
  (:require
   [tick.core :as t]
   [ta.db.bars.duckdb :as duck]
   [modular.system]))

(def db (modular.system/system :duckdb))

(def window {:start (t/instant "2024-02-01T20:00:00Z")
             :end (t/instant "2024-03-01T20:00:00Z")})


(duck/get-bars db
               {:asset "EUR/USD"
                :calendar [:us :m]}
               window)

(duck/get-bars db {:asset "USD/JPY"
                   :calendar [:us :m]}
               window)

(def window-since (dissoc window :end))


(duck/get-bars db {:asset "USD/JPY"
                   :calendar [:us :m]}
                   window-since)



