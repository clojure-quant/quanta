(ns ta.data.api-ds.bybit
  (:require
   [tick.core :as tick] ; tick uses cljc.java-time
   [tech.v3.dataset :as tds]
   [ta.data.api.bybit :as bybit]))
   

(defn bybit-result->dataset [response]
  (-> response
      (tds/->dataset)))


(defn symbol->provider [symbol]
  ; {:keys [category] :as instrument} (db/instrument-details symbol)
  symbol)

(def start-date-bybit (tick/date-time "2018-11-01T00:00:00"))

(defn get-series [symbol interval _range _opts]
  (let [symbol (symbol->provider symbol)]
    (-> (bybit/get-history {:symbol symbol
                            :start start-date-bybit
                            :interval interval})
        (bybit-result->dataset))))

