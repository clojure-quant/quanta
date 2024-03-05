(ns ta.import.provider.alphavantage.ds
  (:require
   [taoensso.timbre :refer [info warn error]]
   [tick.core :as t]
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.db.asset.db :as db]
   [ta.db.bars.protocol :refer [barsource]]
   [ta.import.provider.alphavantage.raw :as av]))

(defn alphavantage-result->dataset [response]
  (-> response
      :series
      (tds/->dataset)))

(def category-fn
  {:equity av/get-daily
   :fx av/get-daily-fx
   :crypto av/get-daily-crypto})

(defn get-category-download-fn [category]
  (let [fun (get category-fn category)]
    (or fun av/get-daily)))

(defn symbol->provider [symbol]
  symbol)

(def interval-mapping
  {:d "daily"})

(defn range->parameter [{:keys [start mode] :as range}]
  (if (= range :full)
    "full"
    (if (= mode :append)
      "compact"
      "full")))

(defn filter-rows-after-date [ds-bars dt]
  ;(info "filtering after date: " dt)
  (if dt
    (tc/select-rows ds-bars  (fn [row]
                               (t/>= (:date row) dt)))
    ds-bars))

(defn get-bars [{:keys [asset calendar]} range]
  (let [{:keys [category] :as instrument} (db/instrument-details asset)
        symbol-alphavantage (symbol->provider asset)
        f (second calendar)
        period-alphavantage (get interval-mapping f)
        av-get-data (get-category-download-fn category)]
    (assert asset "get-series-alphavantage needs an asset!")
    (assert period-alphavantage (str "get-series-alphavantage does not support interval: " f))
    (-> (av-get-data (range->parameter range) symbol-alphavantage)
        (alphavantage-result->dataset)
        (filter-rows-after-date (:start range)))))


(defrecord import-alphavantage [api-key]
  barsource
  (get-bars [this opts window]
    (get-bars opts window)))

(defn create-import-alphavantage [api-key]
  (av/set-key! api-key)
  (import-alphavantage. api-key))




(comment
  (require '[ta.helper.date :refer [parse-date]])
  (def dt (parse-date "2024-02-01"))

  (-> (get-bars {:asset "EURUSD"
                 :calendar [:fx :d]}
                {:start (parse-date "2023-09-01")
                 :mode :append}
                {})
      (tc/info))

  (get-bars {:asset "FMCDX"
             :calendar [:us :d]}
            {:start dt
             :mode :append})

;
  )