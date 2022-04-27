(ns ta.gann.db
  (:require
   [clojure.edn :as edn]
   [modular.config :refer [get-in-config]]
   [ta.gann.box :as box]
   [ta.gann.parse :refer [parse-double]]
   ))

;; config

(defn edn-filename []
  (get-in-config [:demo :gann-data-file]))

;; helpers

(defn boxes->dict [box-data-list]
  (->> box-data-list
       (map (juxt :symbol identity))
       (into {})))

(defn prepare-box [box-data]
  (-> box-data
      box/convert-gann-dates
      box/make-root-box))

;; data

(defn edn-load []
  (let [filename (edn-filename)]
    (-> filename slurp edn/read-string)))

(defn boxes-dict []
  (-> (edn-load)
      (boxes->dict)))

(defn gann-symbols []
  (map :symbol (edn-load)))

(defn load-gann
  "used in web frontend to edit gann-box definition"
  [symbol]
  (println "gann-db/load: " symbol)
  (-> (boxes-dict)
      (get symbol)))

(defn coerce-col-to-double [k m]
  (if (string? (k m))
    (assoc m k (parse-double (k m)) )
    m))    

(defn sanitize-root-box [root-box]
  (->> root-box
       (coerce-col-to-double :ap)
       (coerce-col-to-double :bp)))


(defn save-gann [root-box]
  (->> (assoc (boxes-dict) (:symbol root-box) (sanitize-root-box root-box))
       (vals)
       (into [])
       (pr-str)
       (spit (edn-filename))))

(defn get-root-box
  "used by trading algos to do math on gann boxes."
  [symbol]
  (when-let [box-data (load-gann symbol)]
    (prepare-box box-data)))

(comment
  (edn-filename)
  (edn-load)
  (boxes-dict)

  ; data load
  (gann-symbols)
  (load-gann "BTCUSD")
  (load-gann "SPY")
  (load-gann "BAD")

  (require '[clojure.pprint :refer [print-table]])
  (-> (edn-load)
      (print-table))

  ; data save
  (save-gann {:symbol "XXX"
              :ap 77.24 :at "2001-08-18"
              :bp  153.48  :bt "2002-10-10"})
  (load-gann "XXX")

  ; work with the data
  (get-root-box "BTCUSD")
  (get-root-box "GLD")
  (get-root-box "BAD")

;
  )
