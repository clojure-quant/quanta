(ns demo.data-import.import-bybit
  (:require
   [tick.core :as tick] ; tick uses cljc.java-time
   [tech.v3.dataset :as tds]
   [ta.data.bybit :as bybit]
   [ta.warehouse.since-importer :as since-importer]))

(defn bybit-get-since-ds [frequency since symbol]
  (-> (bybit/get-history {:symbol symbol
                          :start since
                          :interval frequency})
      (tds/->dataset)))




;; BYBIT UNIVERSE

(def start-date-daily (tick/date-time "2018-11-01T00:00:00"))

(def start-date-15 (tick/date-time "2018-11-01T00:00:00"))


(defn init-bybit-daily [symbols]
  (since-importer/init-symbols 
   :crypto bybit-get-since-ds "D"
   start-date-daily symbols))

(defn init-bybit-15 [symbols]
  (since-importer/init-symbols :crypto bybit-get-since-ds "15"
                               start-date-15 symbols))

(defn append-bybit-daily [symbols]
  (since-importer/append-symbols :crypto bybit-get-since-ds "D"
                                 symbols))

(defn append-bybit-15 [symbols]
  (since-importer/append-symbols :crypto bybit-get-since-ds "15"
                                 symbols))

; ********************************************************************************************+
(comment

  (def symbols ["BTCUSD" "ETHUSD"])

  (init-bybit-daily symbols)
  (init-bybit-15 symbols)

  (append-bybit-daily symbols)
  (append-bybit-15 symbols)
;
  )

