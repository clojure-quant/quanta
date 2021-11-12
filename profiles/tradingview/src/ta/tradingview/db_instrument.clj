(ns ta.tradingview.db-instrument
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [ta.warehouse :refer [symbols-available init-lookup load-symbol]]
   [ta.tradingview.config :refer [tv-config]]))


(comment
  (symbols-available :crypto "D")

  (symbols-available :stocks "D")

  (let [{:keys [lookup search]} (init-lookup ["fidelity-select" "bonds" "test"])]
    [(lookup "MSFT")
     (search "PH")])
;
  )

(let [{:keys [instrument name search]} (init-lookup (:lists tv-config))]
  (def search search)
  (def instrument instrument))

(comment
  (instrument "SPY")
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


