(ns quanta.notebook.bardb-compress
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [modular.system]
   [ta.db.bars.protocol :as b]))

(def db (modular.system/system :bardb-dynamic-compressing))

db

(def window {:start (-> "1999-02-01T20:00:00Z" t/instant)
             :end (-> "2001-03-01T20:00:00Z" t/instant)})

window

; get all data available
(b/get-bars db
            {:asset "MSFT"
             :calendar [:us :month]
             :import :kibot}
            window)



