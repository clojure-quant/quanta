(ns ta.data.settings
  (:require
    [ta.db.asset.symbol-db :as db]
   )
  )

(defn determine-wh [s]
  (let [{:keys [symbol name category]} (db/instrument-details s)]
    (case category
      :crypto :crypto
      :equity :stocks
      :fx :fx
      :future :futures
      :stocks)))

;{:value "Corp" :name "Bonds"}
;{:value "Index" :name "Indices"}
;{:value "Curncy" :name "Currencies"}

(def category-names 
  {:crypto "Crypto"
   :etf "ETF"
   :equity "Equity"
   :fx "FX"
   :future "Future"
   :mutualfund "MutualFund"
   })


(comment
  (db/instrument-details "NG0")

  (determine-wh "BTCUSD")
  (determine-wh "ETHUSD")
  (determine-wh "SPY")
  (determine-wh "QQQ")

  (determine-wh "EURUSD")
  (determine-wh  "NG0")

   ; not existing:
  (determine-wh "BAD")

;   
  )