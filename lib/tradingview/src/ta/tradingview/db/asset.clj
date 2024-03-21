(ns ta.tradingview.db.asset
  (:require
   [clojure.string :as str]
   [ta.db.asset.db :refer [search instrument-details]]))

(def category-names-1
  {:crypto "Crypto"
   :etf "ETF"
   :equity "Equity"
   :fx "FX"
   :future "Future"
   :mutualfund "MutualFund"})

(defn reverse-lookup []
  (->> category-names-1
       (map (fn [[k v]]
              [v k]))
       (into {})))

(def category-names
  (reverse-lookup))

(defn inst-type [i]
  ; this dict has to match above the server-config list of supported categories
  (let [c (:category i)]
    (or (get category-names c) "Equity")))

(defn category-name->category [c]
  (or (get category-names c) :equity))

(defn get-asset-exchange [asset]
  (-> asset instrument-details :exchange))

(defn symbol-info
  "Converts instrument [from db] to tradingview symbol-information
   Used in symbol and search"
  [s]
  (let [i (instrument-details s)]
    {:ticker s  ; OUR SYMBOL FORMAT. TV uses exchange:symbol
     :name  s ; for tv this is only the symbol
     :description (:name i)
     :exchange (:exchange i)
     :exchange-listed (:exchange i)
     :type (inst-type i)
     :supported_resolutions [;"15"
                             "D"]
     :has_no_volume false
     ; FORMATTING OF DIGITS
     :minmov 1  ; is the amount of price precision steps for 1 tick. For example, since the tick size for U.S. equities is 0.01, minmov is 1. But the price of the E-mini S&P futures contract moves upward or downward by 0.25 increments, so the minmov is 25.
     :pricescale 100 ;  If a price is displayed as 1.01, pricescale is 100; If it is displayed as 1.005, pricescale is 1000.
     :minmov2 0  ;  for common prices is 0 or it can be skipped.
     :fractional 0  ; for common prices is false or it can be skipped.   ; Fractional prices are displayed 2 different forms: 1) xx'yy (for example, 133'21) 2) xx'yy'zz (for example, 133'21'5).
     :volume_precision 0 ;Integer showing typical volume value decimal places for a particular symbol. 0 means volume is always an integer. 1 means that there might be 1 numeric character after the comma.
     :pointvalue 1
     ; session
     :data_status "endofday"  ; streaming endofday pulsed delayed_streaming delayed  delayed_streaming
     :has_intraday true
     :timezone "Etc/UTC" ; "America/New_York"
     :session "0900-1600"  ;"0900-1630|0900-1400:2",
                           ;:session-regular "0900-1600"

     ; :expired true ; whether this symbol is an expired futures contract or not.
     ; :expiration_date  (to-epoch-no-ms- (-> 1 t/hours t/ago))
     }))
(defn instrument->tradingview [{:keys [symbol name] :as i}]
  {:ticker symbol
   :symbol symbol ; OUR SYMBOL FORMAT. TV uses exchange:symbol
   :full_name name
   :description  (:name i)
   :exchange (:exchange i)
   :type (inst-type i)})

(defn symbol-search [query category exchange limit]
  (let [category (when (and type (string? category)
                            (not (str/blank? category)))
                   (keyword category))
        exchange (when (and type (string? exchange)
                            (not (str/blank? exchange)))
                   (keyword exchange))
        result (search query category exchange)
        result (take limit result)
        result-tv (map instrument->tradingview result)]
    result-tv))

(comment
  (inst-type {:category :etf})
  (inst-type nil)

  (category-name->category "Equity")
  (category-name->category nil)
  (category-name->category "Crypto")
  (category-name->category "Future")

  (symbol-search "B" "" "" 10)
  (symbol-search "" "crypto" "" 10)
  (symbol-search "" "" "crypto" 10)
  (keyword "crypto")
 ;
  )



