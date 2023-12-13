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

(def intervals
  {:daily "Daily"
   :daily-unadjusted "Daily%20-%20Unadjusted"
   :weekly "Weekly"
   :monthly "Monthly"
   })


(defn local-dir [category interval type]
  (let [dir (str (:local-dir config) (name category) "/" (name interval) "/" (name type) "/")]
    (fs/create-dirs dir)
    dir))


(defn existing-rar-files [category interval]
  (->> (fs/list-dir (local-dir category interval :rar)  "**{.exe}")
       (map fs/file-name)))

(defn ftp-path-category [category interval]
  (str "/Updates/All%20" (category categories) "/" (interval intervals)))


(defn download-overview [category interval]
  (ftp/with-ftp [client ;(str "ftp://ftp.kibot.com/Updates/All%20Stocks/Daily")
                 (str "ftp://ftp.kibot.com" (ftp-path-category category interval))
                 :username (:user config)
                 :password (:password config)
                 :local-data-connection-mode :active
                 :control-keep-alive-reply-timeout-ms 7000
                 ]
    ;(ftp/client-get client "20231208.exe" "20231207.exe")
    (let [file-names (ftp/client-file-names client)]
      (println "remote files for " category ": " file-names)
      file-names)))


(defn download-file [category interval file-remote]
  (ftp/with-ftp [client ;(str "ftp://ftp.kibot.com/Updates/All%20Stocks/Daily")
                        (str "ftp://ftp.kibot.com" (ftp-path-category category interval)) 
                 :username (:user config)
                 :password (:password config)
                 :local-data-connection-mode :active
                 :file-type :binary]
    (let [file-local (str (local-dir category interval :rar) file-remote)]
      (println "downloading " file-remote " ==> " file-local)
       (ftp/client-get client file-remote file-local))))

(defn download-day [category interval day]
  (download-file category interval (str day ".exe")))

(defn files-missing-locally [category interval]
  (let [remote (->> (download-overview category interval)  (into #{}))
        local (->> (existing-rar-files category interval) (into #{}))
        missing (set/difference remote local)
        missing (into [] missing)]
    (sort missing)
    
    ))

;; rar extraction

(defn extract-rar [category interval day]
  (let [rar-filename (str (local-dir category interval :rar) day ".exe")
        path-csv (str (local-dir category interval :csv) day "/")]
    (println "extracting rar " rar-filename " to : " path-csv)
    (fs/create-dirs path-csv)
    (shell "unrar" "e" (str "-op" path-csv) rar-filename)))

;

(comment 
  
  (local-dir :stock :daily :rar)
  (local-dir :stock :daily-unadjusted :csv)
  (existing-rar-files :stock :daily)
  
  (ftp-path-category :stock :daily)
  (ftp-path-category :stock :daily-unadjusted)
  (download-overview :stock :daily)
  

  (download-day :stock :daily "20231207")
  (download-day :stock :daily "20231208")
  (download-day :stock :daily "20231206")
 

  (extract-rar :stock :daily "20231207")

  (count (files-missing-locally :stock :daily))
  (count (files-missing-locally :stock :daily-unadjusted))

  

  
  ;
  )