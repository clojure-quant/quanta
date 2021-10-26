(ns ta.notebook.handler
  (:require
   [taoensso.timbre  :refer [debug info warn error]]
   [ring.util.response :as res :refer [not-found file-response resource-response response]]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.not-modified :refer [wrap-not-modified]]
   [webly.web.middleware :refer [wrap-api-handler]]
   [webly.web.handler :refer [add-ring-handler]]
   [ta.notebook.persist :as p]))

;; file 

(defn notebook-resource-file-handler [req]
  (let [params (:params req)
        {:keys [nbns name]} params]
    (info "nb resource file handler: nbns:" nbns "name:" name)
    (if-let [fmt (p/filename->format name)]
      (if-let [file-name (p/get-filename-ns nbns name)]
        (file-response file-name)
        (do (error "viewer filename cannot be created: " nbns name)
            (not-found {:body (str "filename cannot be created: " nbns name)})))
      (do (error (str "viewer file resource - format could not be determined for name: [" name "]"))
          (not-found {:error (str "format could not be determined: " name)})))))

(def wrapped-notebook-resource-handler
  (-> notebook-resource-file-handler
      (wrap-content-type) ; options
      (wrap-not-modified)))

(add-ring-handler :viewer/file  wrapped-notebook-resource-handler)

;; rest

(defn get-notebook-list
  [req]
  (let [nb-list (p/get-notebook-list)]
    (debug "notebook list user: " nb-list)
    (response {:data nb-list})))

(defn get-resource-list ; for notebook
  [req]
  (let [params (:params req)
        {:keys [nbns]} params
        res-list (p/get-resource-list nbns)]
    (debug "resources for notebook " nbns ": " res-list)
    (response {:data res-list})))

(add-ring-handler :viewer/ns (wrap-api-handler get-notebook-list))
(add-ring-handler :viewer/list (wrap-api-handler get-resource-list))

(comment

  (get-resource-list "ta.notebook.persist")

  ; (loadr "demo.studies.asset-allocation-dynamic" "2" :text)

  (notebook-resource-file-handler
   {:params {:nbns "demo.studies.asset-allocation-dynamic"
             :name "1.edn"}})

  (notebook-resource-file-handler
   {:params {:nbns "demo.studies.asset-allocation-dynamic"
             :name "2.txt"}})

;  
  )