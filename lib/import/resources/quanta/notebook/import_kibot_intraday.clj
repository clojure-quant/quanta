(ns quanta.notebook.import-kibot-intraday
  (:require
   [tick.core :as t]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [tablecloth.api :as tc]
   [ta.db.bars.protocol :as b]
   [ta.db.bars.nippy :as nippy]
   [ta.import.helper.retries :refer [with-retries]]
   [modular.system]
   [de.otto.nom.core :as nom]))

(def im (modular.system/system :import-manager))

im

(def db (nippy/start-bardb-nippy "output/kibot-intraday/"))

db
(nippy/filename-asset db {:asset "EUR/USD"
                          :calendar [:forex :m]})

(defn import-asset [asset]
  (let [opts {:asset asset
              :calendar [:forex :m]
              :import :kibot-http}
        ds (with-retries 5 b/get-bars im opts
             {:start (t/instant "2019-12-01T00:00:00Z")
              :end (t/instant "2020-02-01T00:00:00Z")})]
    (if (nom/anomaly? ds)
      (do
        (error "could not get asset: " asset)
        {:asset asset :count 0})
      (let [c (tc/row-count ds)]
        (info "recevied from kibot asset:" asset " count: " c)
        (b/append-bars db opts ds)
        {:asset asset :count c}))))

(defn import-assets [assets]
  (let [r (doall (map import-asset assets))]
    (spit "output/kibot-intraday/summary.edn" (pr-str r))))

(import-asset "USD/JPY")

(import-assets ["EUR/USD" "USD/CHF" "GBP/USD" "USD/SEK"
                "USD/NOK" "USD/CAD" "USD/JPY" "AUD/USD"
                "NZD/USD" "USD/MXN" "USD/ZAR" "EUR/JPY"
                "EUR/CHF" "EUR/GBP" "GBP/JPY"])

(import-assets ["EU0" "SF0" "BP0" "SEK0" "NOK0"
                "CD0" "JY0" "AD0" "NE0" "PX0"
                "RA0" "RY0" "RF0" "RP0" "PJY0"])

(import-assets ["SPY" "QQQ"])

(import-assets  ["JY0"  "USD/JPY"])

(import-assets ["EU0" "SF0" "BP0"])

(import-assets ["INTC"])




