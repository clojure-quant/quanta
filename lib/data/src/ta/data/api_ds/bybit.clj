(ns ta.data.api-ds.bybit
  (:require
   [tick.core :as tick] ; tick uses cljc.java-time
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.data.api.bybit :as bybit]))
   
(defn sort-ds [ds]
  (tc/order-by ds [:date] [:asc]))

(defn bybit-result->dataset [response]
  (-> response
      (tds/->dataset)
      (sort-ds) ; bybit returns last date in first row.
      ))


(defn symbol->provider [symbol]
  ; {:keys [category] :as instrument} (db/instrument-details symbol)
  symbol)

(def start-date-bybit (tick/date-time "2018-11-01T00:00:00"))

(defn range->parameter [range]
  (if (= range :full)
      {:start start-date-bybit}
      range))

(defn get-series [{:keys [symbol frequency]} range _opts]
  (let [symbol (symbol->provider symbol)
        range-bybit (range->parameter range)]
    (-> (bybit/get-history (merge 
                            {:symbol symbol
                             :interval frequency}
                              range-bybit))
        (bybit-result->dataset))))

