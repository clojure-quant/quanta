(ns ta.tradingview.db-instrument)

(def categories
  {:crypto "Crypto"
   :etf "ETF"
   :mutualfund "MutualFund"
   :equity "Equity"})


(defn reverse-lookup []
  (->> categories
       (map (fn [[k v]]
               [v k]))
       (into {})))

(def category-names 
  (reverse-lookup))

(defn inst-type [i]
  ; this dict has to match above the server-config list of supported categories
  (let [c (:category i)]
    (or (get categories c) "Equity")))


(defn category-name->category [c]
  (or (get category-names c) :equity))

(comment
  (inst-type {:category :etf})
  (inst-type nil)

  (category-name->category "Equity")
  (category-name->category nil)
  (category-name->category "Crypto")

 ;
  )


