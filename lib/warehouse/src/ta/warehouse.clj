(ns ta.warehouse
  (:require
   [clojure.edn :as edn]
   [tech.v3.dataset :as ds]
   [tech.v3.dataset.io :as ds-io]
   [tech.v3.dataset.base :as ds-base]
   [tech.v3.datatype.errors :as errors]
  ;;Support for tensor/array buffers in nippy
   [tech.v3.datatype.nippy]
   [tech.v3.io :as io]
   [taoensso.nippy :as nippy]))



(def dir (atom {:series "./"
                :list "./resources/etf/"}
                ))

(defn init-tswh [path]
  (reset! dir path))

(defn save-ts [ds name]
  (let [s (io/gzip-output-stream! (str (:series @dir) name ".nippy.gz"))]
    (io/put-nippy! s ds)))

(defn load-ts [ name]
  (let [s (io/gzip-input-stream (str (:series @dir) name ".nippy.gz"))]
    (io/get-nippy s )))

(defn load-list [name]
  (println "loading list: " name)
  (->> (str (:list @dir) name ".edn") 
      slurp
      edn/read-string
      (map :symbol)
      ))


(comment
  
  (load-list "fidelity-select")
  
  )
