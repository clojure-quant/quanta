(ns joseph.upload
  (:require
   [clojure.java.io :as io]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.multipart-params :refer [wrap-multipart-params]]))

(defn save-file [req]
  (let [tmpfilepath (:path (bean (get-in req [:params "file" :tempfile])))
        custom-path "../resources/trades-upload.edn"]
    (io/copy (io/file tmpfilepath) (io/file custom-path))
    {:status 200
     :headers  {"Content-Type" "text/html"}
     :body (str "File uploaded to: " custom-path)}))

(def wrapped-save-file
  (-> save-file
      wrap-params
      wrap-multipart-params))

(comment


  (def demo-ring-req
    {:params {"file"
              {:filename     "words.txt"}
              :content-type "text/plain"
              :tempfile     nil ; java file
              :size         51}})

  (save-file demo-ring-req)
;  
  )