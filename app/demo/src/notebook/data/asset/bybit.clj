(ns notebook.data.asset.bybit
  (:require
   [ta.import.provider.bybit.raw :refer [get-assets-spot]]))


(defn save-list [assets]
  (->>  (map (fn [s]
               {:name s
                :symbol s
                :category :crypto}) assets)
        (pr-str)
        (spit "../resources/symbollist/bybit.edn")))

(-> (get-assets-spot)
    (save-list))



