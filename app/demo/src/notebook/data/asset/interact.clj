(ns notebook.data.asset.interact
  (:require
   [ta.db.asset.db :as db]
   [clojure.pprint :refer [print-table]]))


(db/search "P")
(db/search "Bitc")
(db/search "BT")
(db/search "Aura")


(db/search "BT" :crypto)
(db/search "B" :equity)
(db/search "B" :equity "SG")
(db/search "B" nil nil)
(db/search "B" "" "")

(db/instrument-details "BTCUSD")
(db/instrument-details "EUR/USD")
(db/instrument-name "BTCUSD")

(-> (db/get-instruments)
    print-table)


(db/get-instruments)

(db/symbols-available :crypto)
(db/symbols-available :etf)
(db/symbols-available :equity)
(db/symbols-available :fx)
(db/symbols-available :future)



(db/instrument-details "NG0")
;; => {:symbol "NG0", :kibot "NG", :name "CONTINUOUS NATURAL GAS CONTRACT", :category :future, :exchange "SG"}

(db/instrument-details "NG1223")
 ;; => {:symbol "NG1223", :kibot "NGZ23", :name "CONTINUOUS NATURAL GAS CONTRACT", :category :future, :exchange "SG"}

(vals @db/db)
 
(db/get-instrument-by-provider :kibot "NG")
(db/get-instrument-by-provider :kibot "XXXXXX")


(db/modify {:symbol "MSFT" :super 3})

(db/instrument-details "MSFT")