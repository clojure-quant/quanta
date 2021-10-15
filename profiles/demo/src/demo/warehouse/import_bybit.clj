(ns demo.warehouse.import-bybit
  (:require
   [taoensso.timbre :refer [trace debug info infof warn error]]
   [tick.alpha.api :as t] ; tick uses cljc.java-time
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tablecloth]
   [ta.data.date :as d]
   [ta.warehouse :as wh]
   [ta.data.bybit :as bybit]
   [ta.warehouse.since-importer :as since-importer]
   [demo.env.config :refer [w-crypto log-config!]]))

(defn bybit-get-since-ds [frequency since symbol]
  (-> (bybit/get-history frequency since symbol)
      (tds/->dataset)))

;; BYBIT UNIVERSE

(def bybit-symbols ["BTCUSD" "ETHUSD"])

(def start-date-daily (t/instant "1999-12-31T00:00:00Z"))

(def start-date-15 (t/instant "1999-12-31T00:00:00Z"))

(defn init-bybit-daily []
  (since-importer/init-symbols w-crypto bybit-get-since-ds "D"
                               start-date-daily bybit-symbols))

(defn init-bybit-15 []
  (since-importer/init-symbols w-crypto bybit-get-since-ds "15"
                               start-date-15 bybit-symbols))

(defn append-bybit-daily []
  (since-importer/append-symbols w-crypto bybit-get-since-ds "D"
                                 bybit-symbols))

(defn append-bybit-15 []
  (since-importer/append-symbols w-crypto bybit-get-since-ds "15"
                                 bybit-symbols))

(defn task-bybit-import-initial [& _]
  (log-config!)
  (init-bybit-daily)
  (init-bybit-15))

(defn task-bybit-import-append [& _]
  (log-config!)
  (append-bybit-daily)
  (append-bybit-15))

; ********************************************************************************************+
(comment

  (init-bybit-daily)

; init 15 - all
  (init-bybit-15)

  (append-bybit-daily)
  (append-bybit-15)
;
  )

