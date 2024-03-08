(ns juan.notebook.study.download-data
  (:require
   [taoensso.timbre :refer [info warn error]]
   [tick.core :as t]
   [modular.system]
   [ta.db.asset.db :as db]
   ;[ta.import.core :refer [get-bars]]
   [ta.calendar.core :as cal]
   [ta.db.bars.protocol :as b]
   [juan.asset-pairs :refer [asset-pairs]]))

; intraday data is big, so we preload it.
; this ensures that our historical study runs faster.

(def db (modular.system/system :bardb-dynamic))

(cal/trailing-range [:forex :d] 5000)

(def window-daily
  (cal/trailing-range [:forex :d] 1000 (t/zoned-date-time "2024-03-07T17:30-05:00[America/New_York]")))

window-daily

(defn get-forex-daily [asset]
  (info "getting intraday forex for: " asset)
  (b/get-bars db {:asset asset
                  :calendar [:forex :d]
                  :import :kibot}
              window-daily))

(def window-intraday
  (cal/trailing-range [:forex :m] 90000 (t/zoned-date-time "2024-03-08T17:30-05:00[America/New_York]")))

window-intraday

(defn get-forex-intraday [asset]
  (info "getting intraday forex for: " asset)
  (b/get-bars db {:asset asset
                  :calendar [:forex :m]
                  :import :kibot-http}
          window-intraday))


;; import one forex
(db/instrument-details "EUR/USD")

(get-forex-daily "EUR/USD")
(get-forex-intraday "EUR/USD")

;; import all forex bars

asset-pairs


(doall 
  (for [pair asset-pairs]
    (get-forex-daily (:fx pair))))

(doall 
  (for [pair asset-pairs]
    (get-forex-intraday (:fx pair))))


