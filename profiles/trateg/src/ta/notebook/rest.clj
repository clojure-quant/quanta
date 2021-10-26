(ns ta.notebook.rest
  (:require
   [taoensso.timbre :refer [info]]
   [clojure.java.io :as io]
   ;[tech.v3.dataset :as dataset]

   [ring.util.io :as ring-io]

   [ta.notebook.persist :as p]
   [ta.persist.tds :refer [filename->response-arrow]]))

(defn send-response [fmt file-name]
  (case fmt
    ;"gz" (res/response {:data (p/loadr file-name :nippy)})
    :arrow (filename->response-arrow file-name)
    :edn  (res/response {:data (slurp file-name)})
    :text  (res/response {:data (slurp file-name)})))

(defn file-exists [file-name]
  (let [res-file (io/file file-name)]
    (and (.exists res-file)
         (.isFile res-file))))

(defn resource-handler [req]
  (let [params (:params req)
        {:keys [nbns name]} params]
    (info "resource handler running params:  nbns" nbns "name:" name)
    (if-let [fmt (p/filename->format name)]
      (let [file-name (p/get-filename-ns nbns name)]
        (if (file-exists file-name)
          (send-response fmt file-name)
          (res/response {:error (str "File not found:" name " ns: " nbns)})))
      (res/response {:error (str "Resource could not be determined:" name " ns: " nbns)}))))

(add-ring-handler :nb/get resource-handler)
; problem with headers in get request

(defn resource-handler-edn [req]
  (let [params (:params req)
        {:keys [nbns name]} params]
    (info "resource handler running params:  nbns" nbns "name:" name)
    (if-let [fmt (p/filename->format name)]
      (let [file-name (p/get-filename-ns nbns name)]
        (if (file-exists file-name)
          (send-response fmt file-name)
          (res/response {:error (str "File not found:" name " ns: " nbns)})))
      (res/response {:error (str "Resource could not be determined:" name " ns: " nbns)}))))

(add-ring-handler :nb/get (wrap-api-handler resource-handler-edn))

