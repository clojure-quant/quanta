(ns notebook.studies.full-market
  (:require
   [ta.data.api.kibot-ftp :as kibot-raw]
   [ta.data.api-ds.kibot-ftp :as kibot-ds]
   [ta.db.bars.nippy :as nippy]))

;; store one daily-ds

(defn ds-name-day [category interval day]
  (str (kibot-raw/local-dir category interval :ds) day ".nippy.gz"))

(defn store-ds-day [category interval day]
  (println "creating nippy file for category: " category " day: " day)
  (-> (kibot-ds/create-ds category interval day)
      (nippy/save-ds (ds-name-day category interval day))))

(defn load-ds-day [category interval day]
 (nippy/load-ds (ds-name-day category interval day)))


; task

(defn download-and-extract [category interval file-name]
  (let [day (kibot-ds/filename->day file-name)]
    (println "download&extract " category " " interval "date: " day)
    (kibot-raw/download-file category interval file-name)
    (kibot-raw/extract-rar category interval day)
    (store-ds-day category interval day)))

(defn download-missing-files [category interval]
  (let [missing-files (kibot-raw/files-missing-locally category interval)]
    (doall (map #(download-and-extract category interval %) missing-files))
    ))

;; Backload 6 months: process by date ascending. Reason: split data changes prior values.
;; Add daily bar to storage. Try duckdb.
;; Eliminate symbols with splits (entire history).


;; 6 months of daily files
;; each day is 1MB - 26MB
;; circa 200 files * 5MB = 1 GIG compressed.

;; convert all days

(defn store-ds-all [category interval]
  (let [days (kibot-ds/existing-rar-days category interval)]
    (doall (map #(store-ds-day category interval %) days))))  

(comment 
  (kibot-raw/local-dir :stock :daily :ds)
  (kibot-raw/local-dir :stock :daily-unadjusted :ds)
  (ds-name-day :stock :daily-unadjusted "20230918")

  (store-ds-day :stock :daily "20230918")
  ; 20230918.nippy.gz has 6 MB
  ; stock/20230918.exe has 5.1 MB.
  
  (store-ds-all :stock :daily)

   ;; task
  (download-missing-files :stock :daily-unadjusted)
  (download-missing-files :etf :daily-unadjusted)

  ; etf rar problems
  ; 20230626.exe
  

  
;  
  )

