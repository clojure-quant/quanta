(ns ta.data.api-ds.kibot-ftp
  (:require 
    [babashka.fs :as fs]
    [clojure.java.io :as io]
    [tech.v3.dataset :as tds]
    [tablecloth.api :as tc]
    [ta.data.api.kibot-ftp :as kibot]))

(defn csv-dir-day [category day]
  (str (kibot/local-csv-dir-category category) day))


(defn csv-assets-day [category day]
  (let [dir (csv-dir-day category day)]
    (->> (fs/list-dir dir "**{.txt}")
         (map fs/file-name)
         (remove #(= "adjusted_files.txt" %))
         (map #(subs % 0 (- (count %) 4)))
         )))

(defn csv-day-asset->dataset [category day asset]
  (let [; csv/stock/20230918/HUN.txt 
        ; 09/18/2023,25.68,26.03,25.24,25.25,2003743
        filename (str (csv-dir-day category day) "/" asset ".txt")]
  (-> (tds/->dataset (io/input-stream filename)
                     {:file-type :csv
                      :header-row? false
                      :dataset-name asset})
      (tc/rename-columns {"column-0" :date
                          "column-1" :open
                          "column-2" :high
                          "column-3" :low
                          "column-4" :close
                          "column-5" :volume})
      (tc/add-column :symbol asset)
      ;(tc/convert-types :date [[:local-date-time date->localdate]])
      )))


(defn create-asset-aggregator [category day]
  (fn [combined-ds asset] 
    (let [next-ds (csv-day-asset->dataset category day asset)]
      ;(println "next-ds: " next-ds)
      (if combined-ds
       (tc/concat combined-ds next-ds)
        next-ds))))
   

(defn create-ds [category day]
   (let [assets (csv-assets-day category day)
         ;assets (take 1500 assets)
         agg (create-asset-aggregator category day)]
     ;(println "assets: " assets)
     (reduce agg nil assets)))

(defn local-dir-ds [category]
  (let [dir (str (:local-dir kibot/config) "ds/" (name category) "/")]
    (fs/create-dirs dir)
    dir))

(defn existing-rar-days [category]
  (let [files (kibot/existing-rar-files category)]
    (map #(subs % 0 (- (count %) 4)) files)))

(comment 
  (csv-dir-day :stock "20230918")

  (->> (csv-assets-day :stock "20230918")
      (take 152)
      last
      )
  
  (->> (csv-day-asset->dataset :stock "20230918" "HUN")
       (tc/columns))
  
  (csv-day-asset->dataset :stock "20230918" "AEHL")

  (create-ds :stock "20230918")
  (local-dir-ds :stock)
  (existing-rar-days :stock)
 ; 
  )

