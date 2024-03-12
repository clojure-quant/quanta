(ns notebook.data.import.kibot-intraday
  (:require
   [tick.core :as t]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [tablecloth.api :as tc]
   [ta.db.bars.protocol :as b]
   [ta.db.bars.nippy :as nippy]
   [modular.system]))

(def im (modular.system/system :import-manager))

im

(def db (nippy/start-bardb-nippy "../../output/kibot-intraday/"))

db
(nippy/filename-asset db {:asset "EUR/USD" 
                          :calendar [:forex :m]})

(defn import-asset [asset]
  (let [opts {:asset asset
              :calendar [:forex :m]
              :import :kibot-http}
        ds (b/get-bars im opts
                      {:start (t/instant "2019-12-01T00:00:00Z")
                       :end (t/instant "2020-02-01T00:00:00Z")})
        c (tc/row-count ds)]
    (info "recevied from kibot asset:" asset " count: " c)
    (b/append-bars db opts ds)
    {:asset asset :count c}))

(defn import-assets [assets]
  (doall (map import-asset assets)))

(import-asset "USD/JPY")

(import-assets ["EUR/USD" "USD/CHF" "GBP/USD" "USD/SEK"
                "USD/NOK" "USD/CAD" "USD/JPY" "AUD/USD"
                "NZD/USD" "USD/MXN" "USD/ZAR" "EUR/JPY"
                "EUR/CHF" "EUR/GBP" "GBP/JPY"])







