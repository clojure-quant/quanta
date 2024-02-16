(ns ta.import.provider.bybit.ds
  (:require
   [tick.core :as t] ; tick uses cljc.java-time
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.import.provider.bybit.raw :as bybit]))
   
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

(def start-date-bybit (t/date-time "2018-11-01T00:00:00"))

(def bybit-frequencies
  ; Kline interval. 1,3,5,15,30,60,120,240,360,720,D,M,W
  {:m "1"
   :h "60"
   :d "D"})

(defn bybit-frequency [frequency]
  (get bybit-frequencies frequency)
  )

(defn range->parameter [range]
  (if (= range :full)
      {:start start-date-bybit}
      range))

(defn get-series [{:keys [asset calendar]} range]
  (let [f (last calendar)
        frequency-bybit (bybit-frequency f)
        symbol-bybit (symbol->provider asset)
        range-bybit (range->parameter range)]
    (assert frequency-bybit (str "bybit does not support frequency: " f))
    (-> (bybit/get-history (merge 
                            {:symbol symbol-bybit
                             :interval frequency-bybit}
                              range-bybit))
        (bybit-result->dataset))))

(comment 
  (bybit-frequency :d)
  (bybit-frequency :h)
  (bybit-frequency :m)
  (bybit-frequency :s)

  

  (get-series {:asset "BTCUSD"
               :calendar [:crypto :d]}
               {:start (t/date-time "2024-02-01T00:00:00")})
  
  
 ; 
  )