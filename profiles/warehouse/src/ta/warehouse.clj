(ns ta.warehouse
  (:require
   [clojure.edn :as edn]
   [taoensso.timbre :refer [trace debug info error]]
  ; [tech.v3.dataset :as ds]
  ; [tech.v3.dataset.io :as ds-io]
   ;[tech.v3.dataset.base :as ds-base]
  ; [tech.v3.datatype.errors :as errors]
  ;;Support for tensor/array buffers in nippy
  ; [tech.v3.datatype.nippy]
   [tech.v3.io :as io]
   [taoensso.nippy :as nippy]
   [tablecloth.api :as tablecloth]
   [ta.dataset.helper :as h]))

(defn init [settings]
  (info "wh init: " settings)
  settings)

(defn save-ts [wh ds name]
  (let [s (io/gzip-output-stream! (str (:series wh) name ".nippy.gz"))]
    (info "saving series " name " count: " (h/ds-rows ds))
    (io/put-nippy! s ds)))

(defn load-ts [wh name]
  (let [s (io/gzip-input-stream (str (:series wh) name ".nippy.gz"))
        ds (io/get-nippy s)
        ds (tablecloth/set-dataset-name ds name)]
    (info "loaded series " name " count: " (-> (tablecloth/shape ds) first))
     ;(tablecloth/add-column ds :symbol symbol)
    ds))

(defn load-list [wh name]
  (println "loading list: " name)
  (->> (str (:list wh) name ".edn")
       slurp
       edn/read-string
       (map :symbol)))


