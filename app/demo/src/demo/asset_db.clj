(ns demo.asset-db
  (:require
   [ta.db.asset.db :as db]
   [ta.db.asset.symbollist :refer [load-lists-full]]
   [ta.import.provider.kibot-http.assets :as kibot-http]))

(def asset-lists
  ["crypto"
   "fidelity-select"
   "bonds"
   "commodity-industry"
   "commodity-sector"
   "currency-spot"
   "equity-region"
   "equity-region-country"
   "equity-sector-industry"
   "equity-style"
   "test"
   "futures-kibot"])

(def asset-list-directory "../resources/symbollist/")

(def asset-lists-filenames
  (map #(str asset-list-directory % ".edn") asset-lists))

(defn add-lists-to-db [filenames]
  (let [asset-detail-seq (load-lists-full filenames)]
    (doall (map db/add asset-detail-seq))))

(defn add-assets []
  (add-lists-to-db asset-lists-filenames)
  (kibot-http/import-kibot-links "forex")
  :assets-added-to-db)

(comment
  asset-lists-filenames
  (add-assets)


 ; 
  )
