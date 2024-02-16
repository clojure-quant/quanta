(ns ta.import.provider.kibot-ftp.ds
  (:require 
    [babashka.fs :as fs]
    [clojure.java.io :as io]
    [tech.v3.dataset :as tds]
    [tablecloth.api :as tc]
    [ta.import.provider.kibot-ftp.raw :as kibot]))

;; csv file => dataset

(defn filename->day [file-name]
  (subs file-name 0 (- (count file-name) 4)))

(defn csv-dir-day [category interval day]
  (str (kibot/local-dir category interval :csv) day))

(defn csv-assets-day [category interval day]
  (let [dir (csv-dir-day category interval day)]
    (->> (fs/list-dir dir "**{.txt}")
         (map fs/file-name)
         (remove #(= "adjusted_files.txt" %))
         (map #(subs % 0 (- (count %) 4)))
         )))

(defn csv-day-asset->dataset [category interval day asset]
  (let [; csv/stock/20230918/HUN.txt 
        ; 09/18/2023,25.68,26.03,25.24,25.25,2003743
        filename (str (csv-dir-day category interval day) "/" asset ".txt")]
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


(defn create-asset-aggregator [category interval day]
  (fn [combined-ds asset] 
    (let [next-ds (csv-day-asset->dataset category interval day asset)]
      ;(println "next-ds: " next-ds)
      (if combined-ds
       (tc/concat combined-ds next-ds)
        next-ds))))
   

(defn create-ds [category interval day]
   (let [assets (csv-assets-day category interval day)
         ;assets (take 1500 assets)
         agg (create-asset-aggregator category interval day)]
     ;(println "assets: " assets)
     (reduce agg nil assets)))

(defn existing-rar-days [category interval]
  (let [files (kibot/existing-rar-files category interval)]
    (map #(subs % 0 (- (count %) 4)) files)))

(defn existing-ds-files [category interval]
  (->> (fs/list-dir (kibot/local-dir category interval :ds)  "**{.nippy.gz}")
       (map fs/file-name)
       (map #(subs % 0 (- (count %) 9)))
       ))



(comment 
  (->> (csv-assets-day :stock :daily "20230918")
      (take 152)
      last
       )
  
  (->> (csv-day-asset->dataset :stock :daily "20230918" "HUN")
       (tc/columns))
  (csv-day-asset->dataset :stock :daily "20230918" "AEHL")

  (create-ds :stock :daily "20230918")

  (existing-ds-files :stock :daily-unadjusted)


 ; 
  )

