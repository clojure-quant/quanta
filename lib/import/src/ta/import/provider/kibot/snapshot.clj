(ns ta.import.provider.kibot.snapshot
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer [info warn error]]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.argops :as argops]
   [tablecloth.api :as tc]
   [ta.import.provider.kibot.raw :as kibot]
   [ta.import.provider.kibot.ds :refer [string->stream provider->symbol symbol->provider symbols->str]]))

(defn symbol-conversion [col-symbol]
  (map provider->symbol col-symbol))

(defn kibot-snapshot-result->dataset [csv]
  (-> (tds/->dataset (string->stream csv)
                     {:file-type :csv
                      :header-row? true
                      :key-fn (comp keyword str/lower-case)
                      :dataset-name "kibot-snapshot"})
      (tc/update-columns {:symbol symbol-conversion})
      (tc/rename-columns {(keyword ":404 symbol not foundsymbol")
                          :symbol})
      ;(tc/convert-types :date [[:local-date-time date->localdate]])
      ))

(defn get-snapshot [symbol]
  (let [symbols-kibot (->> symbol
                           (map symbol->provider)
                           (map :symbol))
        result (kibot/snapshot {:symbol (symbols->str symbols-kibot)})]
    (if-let [error? (:error result)]
      (do (error "kibot request error: " error?)
          nil)
      (kibot-snapshot-result->dataset result))))

(comment

  (get-snapshot ["AAPL"])
  (get-snapshot ["NG0"])
  (get-snapshot ["CL0"])
  (get-snapshot ["MES0"])
  (get-snapshot ["RIVN" "AAPL" "MYM0"])

  "RIVN" "MYM0" "RB0" "GOOGL" "FCEL"
  "NKLA" "M2K0" "INTC" "MES0" "RIG"
  "ZC0" "FRC" "AMZN" "HDRO" "MNQ0"
  "BZ0" "WFC" "DAX0" "PLTR" "NG0"

  (symbol->provider "MSFT")
  (symbol->provider "EURUSD")
  (symbol->provider "IJH")




;
  )
