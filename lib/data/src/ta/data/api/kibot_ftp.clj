(ns ta.data.api.kibot-ftp
  (:require 
   [clojure.set :as set]
   [babashka.fs :as fs]
   [miner.ftp :as ftp]
   [babashka.process :refer [shell process exec]]))

(def config
  {:local-dir "/home/florian/repo/clojure-quant/trateg/output/kibot-incremental/"
   :user "hoertlehner@gmail.com"
   :password "282m2fhgh"})

(def categories 
  {:etf "ETFs"
   :stock "Stocks"
   :future "Futures"
   :fx "Forex"})

(defn local-dir-category [category]
  (let [dir (str (:local-dir config) (name category) "/")]
    (fs/create-dirs dir)
    dir))

(defn rar-directory-category [category]
  (str (:local-dir config) "rar/" (name category))
  
  )

(defn existing-rar-files [category]
  (->> (fs/list-dir (rar-directory-category category)  "**{.exe}")
       (map fs/file-name)))

(defn ftp-path-category [category]
  (str "/Updates/All%20" (category categories) "/Daily"))


(defn download-overview [category]
  (ftp/with-ftp [client ;(str "ftp://ftp.kibot.com/Updates/All%20Stocks/Daily")
                 (str "ftp://ftp.kibot.com" (ftp-path-category category))
                 :username (:user config)
                 :password (:password config)
                 :local-data-connection-mode :active
                 :control-keep-alive-reply-timeout-ms 7000
                 ]
    ;(ftp/client-get client "20231208.exe" "20231207.exe")
    (let [file-names (ftp/client-file-names client)]
      (println "remote files for " category ": " file-names)
      file-names)))


(defn download-file [category file-remote]
  (ftp/with-ftp [client ;(str "ftp://ftp.kibot.com/Updates/All%20Stocks/Daily")
                        (str "ftp://ftp.kibot.com" (ftp-path-category category)) 
                 :username (:user config)
                 :password (:password config)
                 :local-data-connection-mode :active
                 :file-type :binary]
    (let [file-local (str (local-dir-category category) file-remote)]
      (println "downloading " file-remote " ==> " file-local)
       (ftp/client-get client file-remote file-local))))

(defn download-day [category day]
  (download-file category (str day ".exe")))

(defn files-missing-locally [category]
  (let [remote (->> category download-overview (into #{}))
        local (->> category existing-rar-files (into #{}))
        missing (set/difference remote local)]
    (into [] missing)))

;; rar extraction

(defn local-csv-dir-category [category]
  (let [dir (str (:local-dir config) "csv/" (name category) "/")]
    (fs/create-dirs dir)
    dir))

(defn extract-rar [category day]
  (let [rar-filename (str (local-dir-category category) day ".exe")
        path-csv (str (local-csv-dir-category category) day "/")]
    (println "extracting rar " rar-filename " to : " path-csv)
    (fs/create-dirs path-csv)
    (shell "unrar" "e" (str "-op" path-csv) rar-filename)))

;; task

(defn download-and-extract [category file-name]
  (download-file category file-name)
  (extract-rar category (subs file-name 0 (- (count file-name) 4)))
  )

(defn download-missing-files [category]
  (let [missing-files (files-missing-locally category)]
    ;(doall (map #(download-file category %) missing-files))
    (doall (map #(download-and-extract category %) missing-files))
    ))

;; 6 months of daily files
;; each day is 1MB - 26MB
;; circa 200 files * 5MB = 1 GIG compressed.

(comment 
  
  (local-dir-category :stock)

  (download-overview :stock)
  (rar-directory-category :stock)
  (existing-rar-files :stock)

  (download-day :stock "20231207")
  (download-day :stock "20231208")
  (download-day :stock "20231206")

  (ftp-path-category :stock)
  (existing-rar-files :stock)

  (local-csv-dir-category :stock)
  (extract-rar :stock "20231207")

  (files-missing-locally :stock)
  (download-missing-files :stock)

  
  ;
  )