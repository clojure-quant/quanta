(ns notebook.data.import.dynamic
  (:require
   [taoensso.timbre :refer [info warn error]]
   [tick.core :as t]
   [modular.system]
   [ta.calendar.core :as cal]
   [ta.db.bars.protocol :as b]))

(def db (modular.system/system :bardb-dynamic))


(def window-daily
  (cal/trailing-range [:us :d] 200 
                      (t/zoned-date-time "2024-03-07T17:30-05:00[America/New_York]")))

window-daily

(defn get-eodhd-daily [asset]
  (info "getting eodhd data for: " asset)
  (b/get-bars db {:asset asset
                  :calendar [:us :d]
                  :import :eodhd}
              window-daily))

(get-eodhd-daily "AEE.AU")
