(ns ta.data.settings
  (:require
    [ta.warehouse.symbol-db :as db]
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