(ns ta.warehouse.symbollist
  (:require
   [clojure.string :refer [includes? lower-case]]
   [clojure.java.io :as java-io]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [debug info warnf error]]
   [modular.config :refer [get-in-config] :as config]))


 (defn get-lists []
   (get-in-config [:ta :warehouse :lists]))
 

(defn load-list-raw [name]
  (try
    (->> (str (get-in-config [:ta :warehouse :list]) name ".edn")
         slurp
         edn/read-string)
    (catch Exception _
      (error "Error loading List: " name)
      [])))

(defn process-item [symbols {:keys [list] :as item}]
  (if list
    (concat symbols (load-list-raw list))
    (conj symbols item)))

;; lists
(defn load-list-full [name]
  (let [items (load-list-raw name)]
    (reduce process-item [] items)
    ;items
    ))

(defn load-lists-full [names]
  (->> (map load-list-full names)
       (apply concat)
       (into [])))

(defn load-list [name]
  (->> (load-list-full name)
       (map :symbol)))

(defn load-lists [names]
  (->> (map load-list names)
       (apply concat)))

(defn symbollist->dict [l]
  (let [s-name (juxt :symbol identity)
        dict (into {} (map s-name l))]
    dict))

(defn get-dict [names]
  (let [l (load-lists-full names)
        d (symbollist->dict l)]
    d))

(defn search [q]
  (let [l (load-lists-full (get-in-config [:ta :warehouse :lists]))
        q (lower-case q)]
    (filter (fn [{:keys [name symbol]}]
              (or (includes? (lower-case name) q)
                  (includes? (lower-case symbol) q)))

            l)))

(defn instrument-details [s]
  (let [d (get-dict (get-in-config [:ta :warehouse :lists]))]
    (get d s)))


(comment

  (get-lists)
  

  (def directory (clojure.java.io/file "/path/to/directory"))
(def files (file-seq directory))
(take 10 files)


   ;(search "P")
  (search "Bitc")
  (search "BT")

  (instrument-details "BTCUSD")
  (instrument-details "EURUSD")

  (load-list-full "fidelity-select")
  (load-lists-full ["crypto"
                    "fidelity-select"
                    "bonds"
                    "commodity-industry"
                    "commodity-sector"
                    "currency-spot"
                    "equity-region"
                    "equity-region-country"
                    "equity-sector-industry"
                    "equity-style"
                    "test"])
  (->  (load-lists-full ["fidelity-select" "bonds"])
       (symbollist->dict))

; 
  )