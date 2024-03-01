(ns ta.db.asset.db
  (:require
   [clojure.string :refer [includes? lower-case blank?]]
   [taoensso.timbre :refer [trace debug info warnf error]]
   [ta.db.asset.futures :refer [is-future? future-symbol]]))

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

(defn instrument-details [s]
  (if-let [f (is-future? s)]
    (let [data (get @db (:symbol-root f))]
      (future-symbol f data))
    (get @db s)))

(defn add [{:keys [symbol] :as instrument}]
  (let [instrument (-> instrument
                       sanitize-name
                       sanitize-category
                       sanitize-exchange)]
    (swap! db assoc symbol instrument)))

(defn modify [{:keys [symbol] :as instrument}]
  (let [old (instrument-details symbol)
        merged (merge old instrument)]
    (swap! db assoc symbol merged)))

(comment
  (add {:symbol "MSFT" :name "Microsoft"})
  (add {:symbol "IBM" :name "IBM"})
 ; 
  )

(defn get-instruments []
  (-> @db vals))

(defn get-symbols []
  (->> @db vals (map :symbol)))

(defn symbols-available [category]
  (->> (get-instruments)
       (filter #(= category (:category %)))
       (map :symbol)))

(defn q? [q]
  (fn [{:keys [name symbol]}]
    (or (includes? (lower-case name) q)
        (includes? (lower-case symbol) q))))

(defn =exchange? [e]
  (fn [{:keys [exchange]}]
    (= exchange e)))

(defn =category? [c]
  (fn [{:keys [category]}]
    (= category c)))

(defn filter-eventually [make-pred target list]
  (if target
    (filter (make-pred target) list)
    list))

(defn search
  ([q]
   (search q nil nil))
  ([q category]
   (search q category nil))
  ([q category exchange]

   (let [list-full (get-instruments)
         q (if (or (nil? q) (blank? q)) nil (lower-case q))
         e (if (or (nil? exchange) (blank? exchange)) nil exchange)
         c (if (nil? category) nil category)]
     (info "search q: " q "category: " c " exchange: " e)
     (->> list-full
          (filter-eventually =exchange? e)
          (filter-eventually =category? c)
          (filter-eventually q? q)))))

(defn instrument-name [asset]
  (-> asset instrument-details :name))

(defn get-instrument-by-provider [provider s]
  (some (fn [instrument]
          (let [ps (provider instrument)]
            (when (= ps s)
              instrument)))
        (vals @db)))


