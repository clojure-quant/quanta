(ns ta.warehouse.symbol-db
  (:require
   [clojure.string :refer [includes? lower-case blank?]]
   [taoensso.timbre :refer [trace debug info warnf error]]
   [ta.warehouse.symbollist :refer [load-lists-from-config]]))

(defn symbollist->dict [l]
  (let [s-name (juxt :symbol identity)
        dict (into {} (map s-name l))]
    dict))

(comment
    (->  (ta.warehouse.symbollist/load-lists-full ["fidelity-select" "bonds"])
       (symbollist->dict))
;
)

(defn sanitize-name [{:keys [symbol] :as instrument}]
  (update instrument :name (fn [name] (if (or (nil? name)
                                              (blank? name))
                                          (str "Unknown: " symbol)
                                        name))))

(comment 
    (sanitize-name {:symbol "a"})
    (sanitize-name {:symbol "a" :name nil})
    (sanitize-name {:symbol "a" :name ""})
    (sanitize-name {:symbol "a" :name "test"}) 
  ;
  )

(defn sanitize-category [instrument]
  (update instrument :category (fn [category] (if (nil? category)
                                                 :equity
                                                category))))

(comment
  (sanitize-category {:symbol "a"})
  (sanitize-category {:symbol "a" :category :fx})
  (sanitize-category {:symbol "a" :category :crypto})
  ;
  )



(defn sanitize-exchange [{:keys [category] :as instrument}]
  (update instrument :exchange (fn [exchange] (if (or (nil? exchange)
                                              (blank? exchange))
                                           (if (= category :crypto) 
                                               "BB"
                                               "SG")
                                           exchange))))

(comment
  (sanitize-exchange {:symbol "a"})
  (sanitize-exchange {:symbol "a" :exchange "VI"})
  (sanitize-exchange {:symbol "a" :category :stocks})
  (sanitize-exchange {:symbol "a" :category :crypto})
  ;
  )

(defn load-lists []
  (->> (load-lists-from-config)
       (map sanitize-name)
       (map sanitize-category)
       (map sanitize-exchange)
       ))

(comment 
  (require '[clojure.pprint :refer [print-table]])
  (-> (load-lists)  
      print-table
   )
  ;
  )

(def get-instruments
  (memoize load-lists))

(defn symbols-available [category]
  (->> (get-instruments)
       (filter #(= category (:category %)))
       (map :symbol)
   ))

(comment 
   (get-instruments)
   (symbols-available :crypto)
   (symbols-available :etf)
   (symbols-available :equity)
   (symbols-available :fx)
  
  ;
  )

(defn search [q]
  (let [l (get-instruments)
        q (lower-case q)]
    (filter (fn [{:keys [name symbol]}]
              (or (includes? (lower-case name) q)
                  (includes? (lower-case symbol) q)))
            l)))

(comment 
  ;(search "P")
  (search "Bitc")
  (search "BT")

  (instrument-details "BTCUSD")
  (instrument-details "EURUSD")
;  
  )

(defn instrument-details [s]
  (let [l (get-instruments)
        d (symbollist->dict l)]
    (get d s)))

(defn determine-wh [s]
  (let [{:keys [symbol name category]} (instrument-details s)]
    (case category
      :crypto :crypto
      :equity :stocks
      :fx :stocks
      :stocks
      )))

(comment
 
  (determine-wh "BTCUSD")
  (determine-wh "ETHUSD")
  (determine-wh "SPY")
  (determine-wh "QQQ")
   ; not existing:
  (determine-wh "BAD")

;   
  )


