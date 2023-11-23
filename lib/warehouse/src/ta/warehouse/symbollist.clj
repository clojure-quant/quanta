(ns ta.warehouse.symbollist
  (:require
   [clojure.string :refer [includes? lower-case]]
   [clojure.java.io :as java-io]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [debug info warnf error]]
   [modular.config :refer [get-in-config] :as config]
   [ta.warehouse.symbol-db :as db]
   ))

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


(defn add-lists-to-db []
  ; used in the services config. builds instrument db on start.
  (let [symbols (load-lists-full (get-in-config [:ta :warehouse :lists]))]
    (doall (map db/add symbols))
    
    ))
  



(comment

  (get-lists)

  (def directory (clojure.java.io/file "/path/to/directory"))
  (def files (file-seq directory))
  (take 10 files)

  (load-list "bonds")

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


; 
  )