(ns ta.import.provider.kibot-http.assets
  (:require
   [clojure.string :as str]
   [hickory.core :as hc]
   [hickory.select :as s]
   [ta.db.asset.db :as db]))

(defn select-tables [htree]
  (-> (s/select (s/child
                 (s/tag :table)
                 ;(s/class "ms-classic4-main")
                 )
                htree)))

(defn select-table-rows [htree]
  (-> (s/select (s/child
                 ;(s/tag :table)
                 (s/tag :tr))
                htree)))

(defn select-td [htree]
  (s/select  (s/child
              (s/tag :td)) htree))

(defn tds->vec [tds]
  (->> (map :content tds)
       (map first)
       (into [])))

(defn remove-unicode [s]
  (str/replace s "\u00A0" ""))

(defn extract-row [row]
  (-> row select-td tds->vec second remove-unicode str/trim))

(defn extract-table [table]
  (let [rows (select-table-rows table)
        vec (map extract-row rows)]
    vec))

(defn parse [data]
  (->> data ; (slurp html-path)
       hc/parse
       hc/as-hickory
       select-tables
       ;(map extract-table)
       last
       extract-table
       ;(remove nil?)
       ))

;

(defn assets-for [path category]
  (let [assets (-> (str path  category ".html")
                   slurp
                   parse
                   rest)
        links (-> (str path category ".txt")
                  slurp
                  (str/split #"\r\n"))]
    (if (= (count assets) (count links))

      (map (fn [asset link]
             {:asset asset :link link}) assets links)
      {:asset (count assets) :link (count links)})))

(defn update-kibot-asset-impl [asset link]
  (db/modify {:symbol asset
              :kibot-http link}))

(defn update-kibot-asset [{:keys [asset link]}]
  (if-let [old (db/get-instrument-by-provider :kibot asset)]
    (update-kibot-asset-impl (:symbol old) link)
    (when-let [old (db/instrument-details asset)]
      (update-kibot-asset-impl (:symbol old) link))))

(defn import-kibot-links [path category]
  (let [assets (assets-for path category)]
    (doall (map update-kibot-asset assets))
    :kibot-link-import-finished))

(comment
  (assets-for  "../resources/kibot-http/" "forex")

  (assets-for  "../resources/kibot-http/"  "futures")

  (assets-for   "../resources/kibot-http/" "etf")

  ; stocks does not work yet.
  ;(assets-for "stocks")

  (import-kibot-links   "../resources/kibot-http/" "forex")

  (db/instrument-details "EURUSD")

 ; 
  )


