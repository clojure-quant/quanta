(ns notebook.data.duckdb
  (:require
   [modular.system]
   [ta.warehouse.duckdb :refer [get-bars]]))

(def db (:duckdb modular.system/system))

(get-bars db "EUR/USD")
(get-bars db "USD/JPY")