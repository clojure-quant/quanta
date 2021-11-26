(ns ta.tradingview.db-instrument
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [modular.config :refer [get-in-config]]
   [ta.warehouse :refer [symbols-available load-symbol search]]))

(comment
  (symbols-available :crypto "D")
  (symbols-available :stocks "D")

  (search "GOLD")
  (search "BT")
  (search "Bit")

;
  )

(def categories
  {:crypto "Crypto"
   :etf "ETF"
   :mutualfund "MutualFund"
   :equity "Equity"})

(def category-names
  {"Crypto" :crypto
   "ETF" :etf
   "MutualFund" :mutualfund
   "Equity" :equity})

(defn inst-type [i]
  ; this dict has to match above the server-config list of supported categories
  (let [c (:category i)]
    (or (get categories c) "Equity")))

(defn inst-crypto? [i]
  (= (:category i) :crypto))

(defn inst-exchange [i]
  (if (inst-crypto? i) "BB" "SG"))

(defn inst-name [s i]
  (or (:name i) (str "Unknown:" s)))

(defn category-name->category [c]
  (or (get category-names c) :equity))

(comment
  (inst-type {:category :etf})
  (inst-type nil)
  (inst-crypto? {:category :etf})
  (inst-crypto? {:category :crypto})
  (inst-exchange {:category :etf})
  (inst-exchange {:category :crypto})
  (category-name->category "Equity")
  (category-name->category nil)

 ;
  )


