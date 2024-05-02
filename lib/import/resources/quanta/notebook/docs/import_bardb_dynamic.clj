(ns quanta.notebook.docs.import-bardb-dynamic
  (:require
   [tick.core :as t]
   [modular.system]
   [ta.calendar.core :as cal]
   [ta.db.bars.protocol :as b]))

(def db (modular.system/system :bardb-dynamic))

(def window-daily
  (cal/trailing-range [:us :d] 200
                      (t/zoned-date-time "2024-05-02T17:30-05:00[America/New_York]")))

window-daily


(b/get-bars db {:asset "AEE.AU"
                :calendar [:us :d]
                :import :eodhd}
            window-daily)

(b/get-bars db {:asset "BTCUSDT"
                :calendar [:us :d]
                :import :bybit}
            window-daily)