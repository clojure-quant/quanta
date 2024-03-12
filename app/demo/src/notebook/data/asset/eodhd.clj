(ns notebook.data.asset.eodhd
  (:require
   [ta.import.provider.eodhd.raw :refer [get-exchanges get-exchange-tickers]]))


(def api-token "65f0ad82c56400.56029279")

(def e (get-exchanges api-token))

(spit "../resources/eodhd-exchanges.edn" (pr-str e))


(def t (get-exchange-tickers api-token "AU"))

t

(defn convert-one [{:keys [Code Name Country Exchange Isin Type Currency] :as eodhd-one}]
  {:symbol (str Code "." Exchange)
   :name Name
   :category :stock
   :region :australia
   :isin Isin})

(defn filter-stocks [l]
  (filter #(= (:Type %) "Common Stock") l)
  )

(defn save-list [exchange assets]
  (->> assets
       (filter-stocks)
       (map convert-one)
       (pr-str)
       (spit (str "../resources/symbollist/eodhd-" exchange ".edn"))))

(save-list "AU" t)











