(ns ta.warehouse.symbol-db
  (:require
   [clojure.string :refer [includes? lower-case blank?]]
   [taoensso.timbre :refer [trace debug info warnf error]]))

(defonce db (atom {}))

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


(defn add [{:keys [symbol] :as instrument}]
  (let [instrument (-> instrument 
                       sanitize-name
                       sanitize-category
                       sanitize-exchange)]
   (swap! db assoc symbol instrument)))


(comment 
   (add {:symbol "MSFT" :name "Microsoft"})
   (add {:symbol "IBM" :name "IBM"})  
 ; 
  )

(defn get-instruments []
  (-> @db vals))


(comment 
  (require '[clojure.pprint :refer [print-table]])
  (-> (get-instruments)  
      print-table
   )
  ;
  )



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
  (get @db s))





