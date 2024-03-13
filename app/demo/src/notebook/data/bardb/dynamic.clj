(ns notebook.data.bardb.dynamic
  (:require
   [tick.core :as t]
   [modular.system]
   [ta.calendar.core :as cal]
   [ta.db.bars.protocol :as b]))

(def db (modular.system/system :bardb-dynamic))

(def window (cal/trailing-range [:us :d] 10))

window

(b/get-bars db
            {:asset "QQQ"
             :calendar [:us :d]
             :import :kibot}
            window)

(b/get-bars db
            {:asset "BTCUSDT"
             :calendar [:crypto :d]
             :import :bybit}
            {:start (t/instant "2021-01-01T00:00:00Z")
             :end  (t/instant "2021-12-31T00:00:00Z")})
