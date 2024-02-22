(ns juan.study.intraday-data
  (:require
   [ta.db.asset.db :as db]
   [modular.system]
   ;[ta.import.core :refer [get-bars]]
   [ta.calendar.core :as cal]
   [ta.db.bars.protocol :as b]
   [juan.asset-pairs :refer [asset-pairs]]))

; intraday data is big, so we preload it.
; this ensures that our historical study runs faster.

(def db (modular.system/system :bardb-dynamic))

(def window-intraday
  (cal/trailing-range-current [:forex :m] 100000))


(defn get-forex-intraday [asset]
  (b/get-bars db {:asset asset
                  :calendar [:forex :m]
                  :import :kibot-http}
          window-intraday))


(def window-daily
  (cal/trailing-range-current [:forex :d] 1000))


(defn get-forex-daily [asset]
  (b/get-bars db {:asset asset
                  :calendar [:forex :d]
                  :import :kibot}
              window-daily))


;; import one forex
(db/instrument-details "EURUSD")

(get-forex-daily "EURUSD")
(get-forex-intraday "EURUSD")

;; import all forex bars

asset-pairs

(for [pair asset-pairs]
  (get-forex-daily (:fx pair)))

(for [pair asset-pairs]
  (get-forex-intraday (:fx pair)))

