(ns quanta.notebook.bardb-duck-delete
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.db.bars.protocol :as b]
   [ta.db.bars.duckdb :as duck]
   [ta.db.bars.dynamic.overview-db :refer [remove-asset]]
   [modular.system]))

(def ddb (modular.system/system :bardb-dynamic))

(def db (modular.system/system :duckdb))


(duck/delete-bars db [:crypto :d] "ETHUSDT")

(remove-asset (:overview-db ddb) {:asset "ETHUSDT" :calendar [:crypto :d]})