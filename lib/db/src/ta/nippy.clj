(ns ta.nippy
  (:require
   [clojure.java.io :as java-io]
   [taoensso.timbre :refer [debug info warnf error]]
   [tech.v3.io :as io]
   [tablecloth.api :as tc]))

(defn save-ds [ds file-name]
  (let [s (io/gzip-output-stream! file-name)]
    (debug "saving ds count: " (tc/row-count ds) " to " file-name)
    (io/put-nippy! s ds)))

(defn load-ds [file-name]
  (let [s (io/gzip-input-stream file-name)
        ds (io/get-nippy s)]
    (debug "loaded ds " name " count: " (tc/row-count ds))
    ds))
