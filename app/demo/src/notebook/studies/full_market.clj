(ns notebook.studies.full-market
  (:require
   [tablecloth.api :as tc]
   [ta.warehouse :as wh]
   [ta.data.api-ds.kibot-ftp :as kibot]
   [ta.nippy :as nippy]
   ))

;; store one daily-ds

(defn ds-name-day [category day]
  (str (kibot/local-dir-ds category) day ".nippy.gz"))

(defn store-ds-day [category day]
  (println "creating nippy file for category: " category " day: " day)
  (-> (kibot/create-ds category day)
      (nippy/save-ds (ds-name-day category day))))

;; convert all days

(defn store-ds-all [category]
  (let [days (kibot/existing-rar-days category)]
    (doall (map #(store-ds-day category %) days))))  

(comment 
  (ds-name-day :stock "20230918") 

  (store-ds-day :stock "20230918")
  ; 20230918.nippy.gz has 6 MB
  ; stock/20230918.exe has 5.1 MB.

  (store-ds-all :stock)
    
;  
  )

