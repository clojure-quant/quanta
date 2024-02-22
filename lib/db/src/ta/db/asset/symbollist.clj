(ns ta.db.asset.symbollist
  (:require
   [clojure.string :refer [includes? lower-case]]
   [clojure.java.io :as java-io]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [debug info warnf error]]))

(defn load-list-raw [file-name]
  (try
    (->> file-name
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

(defn load-lists [file-names]
  (->> (map load-list file-names)
       (apply concat)))

(comment
  (def directory (clojure.java.io/file "/path/to/directory"))
  (def files (file-seq directory))
  (take 10 files)

  (load-list "../resources/symbollist/bonds.edn")

  (load-list-full "../resources/symbollist/fidelity-select.edn")



; 
  )