(ns notebook.playground.duck 
  (:require 
    [tick.core :as t]
    [ta.db.bars.duckdb :as duck]
    [modular.system]))
  
(def duckdb (modular.system/system :duckdb))

(duck/get-bars-window duckdb [:us :m] "EUR/USD"
                      "2024-01-26T19:35:00Z"
                      "2024-02-26T19:45:00Z")

(def time (t/instant "2024-01-26T20:00:00Z"))
(duck/get-bars-since duckdb [:us :m] "EUR/USD" time)


(duck/get-bars db {:asset "EUR/USD"
              :calendar [:us :m]}
             {:start time}
          )
(duck/get-bars db {:asset "USD/JPY"
                   :calendar [:us :m]}
                  {:start time})


