(ns ta.gann.db
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [clojure.edn :as edn]
   [modular.config :refer [get-in-config]]
   [ta.gann.box :as box]))

;; data

(defn gann-symbols []
  (let [filename (get-in-config [:demo :gann-data-file])
        ganns (-> filename slurp edn/read-string)]
    (map :symbol ganns)))

(defn load-ganns []
  (let [filename (get-in-config [:demo :gann-data-file])
        ganns (-> filename slurp edn/read-string)
        tuple (juxt :symbol identity)]
    (->> ganns
         (map box/convert-gann-dates)
         (map box/make-root-box)
         (map tuple)
         (into {}))))

(defn get-root-box [symbol]
  (get (load-ganns) symbol))

; (def gld-box (move-down (move-down (make-root-box gld))))

;                "QQQ" (zoom-in  (make-root-box qqq))
;                "GLD" (move-down (move-down (make-root-box gld)))
;                "SLV" (zoom-in (zoom-in (make-root-box slv)))
;                "EURUSD" (zoom-in (zoom-in (zoom-in (make-root-box eurusd))))})

;; printing

(defn exponentialize-prices [{:keys [ap bp] :as box}]
  (assoc box
         :apl (Math/pow 10 ap)
         :apr (Math/pow 10 bp)))

(defn print-boxes [boxes]
  (->> boxes
       (map exponentialize-prices)
       (clojure.pprint/print-table)))

(comment
  (get-in-config [:demo :gann-data-file])
  (gann-symbols)

  (-> (load-ganns)
      vals
      (print-boxes))

  (get-root-box "BTCUSD")
  (get-root-box "GLD")
  (get-root-box "BAD")

  ;
  )
