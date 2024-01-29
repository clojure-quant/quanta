(ns notebook.live.duckdb
  (:require
   [modular.system]
   [ta.warehouse.duckdb :refer [get-bars]]))

(def db (:duckdb modular.system/system))

(get-bars db [:us :m] "EUR/USD")
(get-bars db [:us :m] "USD/JPY")

