(ns ta.viz.arrow
  (:require
   ;[tech.v3.dataset :as dataset]
   [ring.util.response :as res]
   [ring.util.io :as ring-io]
   [webly.web.handler :refer [add-ring-handler]]
   [tech.v3.libs.arrow :as arrow])
  ;(:import (java.io ByteArrayOutputStream ByteArrayInputStream))
  )

; https://github.com/apache/arrow

(defn save-ds-as-arrow-file [ds filename]
  (arrow/write-dataset-to-stream! ds filename {}))

(defn load-ds-from-arrow-file [filename]
  (arrow/read-stream-dataset-copying filename))

;ByteArrayOutputStream
; (let [output (ByteArrayOutputStream.)]

(defonce ds-atom (atom nil))

(defn publish-ds! [ds _ #_id]
  (reset! ds-atom ds))

(defn ds-arrow-handler [_ #_req]
  (res/response
   (ring-io/piped-input-stream
    (fn [ostream]
      (arrow/write-dataset-to-stream! @ds-atom ostream {})))))

(add-ring-handler :dataset/arrow
                  ;(wrap-api-handler 
                  ds-arrow-handler)