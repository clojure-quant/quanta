(ns ta.db.bars.nippy
  (:require
   [clojure.string :as str]
   [de.otto.nom.core :as nom]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [tablecloth.api :as tc]
   [clojure.java.io :as java-io]
   [tech.v3.io :as io]
   [babashka.fs :refer [create-dirs]]
   [ta.db.bars.protocol :refer [bardb barsource]]))


(defn save-ds [filename ds]
  (let [s (io/gzip-output-stream! filename)]
    (debug "saving series " filename " count: " (tc/row-count ds))
    (io/put-nippy! s ds)))

(defn load-ds [filename]
  (let [s (io/gzip-input-stream filename)
        ds (io/get-nippy s)]
    (debug "loaded series " name " count: " (tc/row-count ds))
    ds))

(defn filename-asset [this {:keys [asset calendar]}]
  (let [[exchange interval] calendar
        asset (str/replace asset #"/" "_")
        ]
  (str (:base-path this) asset "-" (name exchange) "-" (name interval) ".nippy.gz")))


(defrecord bardb-nippy [base-path]
  barsource
  (get-bars [this opts window]
    (info "get-bars " opts window)
    (-> (load-ds (filename-asset this opts))
        (tc/add-column :asset (:asset opts))))
  bardb
  (append-bars [this opts ds-bars]
    ;(info "this: " this)
    (save-ds (filename-asset this opts) ds-bars)))

(defn start-bardb-nippy [base-path]
  (debug "creating dir: " base-path)
  (create-dirs base-path)
  (bardb-nippy. base-path))
