(ns notebook.playground.bardb.init
  (:require
   [ta.db.bars.duckdb :as duck]
   [modular.system]))

(def db (modular.system/system :duckdb))


(duck/create-table db [:forex :d])
(duck/create-table db [:forex :m])

(duck/create-table db [:crypto :d])
(duck/create-table db [:crypto :m])


(duck/create-table db [:forex :month])
(duck/create-table db [:us :month])
(duck/create-table db [:crypto :month])


