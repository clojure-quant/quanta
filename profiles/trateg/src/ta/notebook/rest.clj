(ns ta.notebook.rest
  (:require
   [taoensso.timbre :refer [info]]
   [clojure.java.io :as io]
   ;[tech.v3.dataset :as dataset]
   [ring.util.response :as res]
   [ring.util.io :as ring-io]
   [webly.web.handler :refer [add-ring-handler]]
   [webly.web.middleware :refer [wrap-api-handler]]
   [ta.notebook.persist :as p]
   [ta.persist.tds :refer [filename->response-arrow]]))

(defn get-notebook-list
  [req]
  (let [nb-list (p/get-notebook-list)]
    (debug "notebook list user: " nb-list)
    (res/response {:data nb-list})))

(defn get-resource-list ; for notebook
  [req]
  (let [params (:params req)
        {:keys [nbns]} params
        res-list (p/get-resource-list nbns)]
    (debug "resources for notebook " nbns ": " res-list)
    (res/response {:data res-list})))

(add-ring-handler :nb/ns (wrap-api-handler get-notebook-list))
(add-ring-handler :nb/list (wrap-api-handler get-resource-list))

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

(comment

  (get-resource-list "ta.notebook.persist")

  ; (loadr "demo.studies.asset-allocation-dynamic" "2" :text)

  (resource-handler {:params {:nbns "ta.notebook.persist"
                              :name "ds1.edn"}})

  (resource-handler {:params {:nbns "demo.studies.asset-allocation-dynamic"
                              :name "2.txt"}})

;  
  )