(ns demo.warehouse.import-bybit
  (:require
   [tick.core :as tick] ; tick uses cljc.java-time
   [tech.v3.dataset :as tds]
   [ta.data.bybit :as bybit]
   [ta.warehouse.since-importer :as since-importer]))

(defn bybit-get-since-ds [frequency since symbol]
  (-> (bybit/get-history frequency since symbol)
      (tds/->dataset)))

;; BYBIT UNIVERSE

(def bybit-symbols ["BTCUSD" "ETHUSD"])

(def start-date-daily (tick/date-time "1999-12-31T00:00:00"))

(def start-date-15 (tick/date-time "1999-12-31T00:00:00"))

(defn init-bybit-daily []
  (since-importer/init-symbols :crypto bybit-get-since-ds "D"
                               start-date-daily bybit-symbols))

(defn init-bybit-15 []
  (since-importer/init-symbols :crypto bybit-get-since-ds "15"
                               start-date-15 bybit-symbols))

(defn append-bybit-daily []
  (since-importer/append-symbols :crypto bybit-get-since-ds "D"
                                 bybit-symbols))

(defn append-bybit-15 []
  (since-importer/append-symbols :crypto bybit-get-since-ds "15"
                                 bybit-symbols))

; ********************************************************************************************+
(comment

  (init-bybit-daily)
  (init-bybit-15)

  (append-bybit-daily)
  (append-bybit-15)
;
  )

