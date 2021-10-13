(ns ta.warehouse
  (:require
   [clojure.java.io :as java-io]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [trace debug info warnf  error]]
   [tech.v3.io :as io]
   [taoensso.nippy :as nippy]
   [tablecloth.api :as tablecloth]
   [ta.dataset.helper :as h]))

(defn init [settings]
  (info "wh init: " settings)
  settings)

(defn load-list [w name]
  (println "loading list: " name)
  (->> (str (:list w) name ".edn")
       slurp
       edn/read-string
       (map :symbol)))

; on name

(defn save-ts [w ds name]
  (let [s (io/gzip-output-stream! (str (:series w) name ".nippy.gz"))]
    (info "saving series " name " count: " (tablecloth/row-count ds))
    (io/put-nippy! s ds)))

(defn load-ts [w name]
  (let [s (io/gzip-input-stream (str (:series w) name ".nippy.gz"))
        ds (io/get-nippy s)
        ds (tablecloth/set-dataset-name ds name)]
    (info "loaded series " name " count: " (tablecloth/row-count ds))
     ;(tablecloth/add-column ds :symbol symbol)
    ds))

(defn make-filename [frequency symbol]
  (str symbol "-" frequency))

(defn load-symbol [w frequency symbol]
  (load-ts w (make-filename frequency symbol)))

(defn save-symbol [w ds frequency symbol]
  (let [n (make-filename frequency symbol)]
    (info "saving: " n)
    (save-ts w ds n)))

; series in warehouse

(defn- filename->info [filename]
  (let [m (re-matches #"(.*)-(.*)\.nippy\.gz" filename)
        [_ symbol frequency] m]
    ;(errorf "regex name: %s cljs?: [%s]" name cljs?)
    {:symbol symbol
     :frequency frequency}))

(comment
  (filename->info "BTCUSD-15.nippy.gz")
 ; 
  )
(defn- dir? [filename]
  (-> (java-io/file filename) .isDirectory))

(defn symbols-available [w frequency]
  (let [dir (java-io/file (:series w))
        files (if (.exists dir)
                (into [] (->> (.list dir)
                              (remove dir?)
                              doall))
                (do
                  (warnf "path for: %s not found: %s"  dir)
                  []))]
    (debug "explore-dir: " files)
    ;(warn "type file:" (type (first files)) "dir?: " (dir? (first files)))
    (->> (map filename->info files)
         (remove #(nil? (:symbol %)))
         (filter #(= frequency (:frequency %)))
         (map :symbol))))

(comment
  (symbols-available {:series "../db/crypto"} "D")

 ; 
  )