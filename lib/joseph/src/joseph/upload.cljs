(ns joseph.upload
  (:require
     [reagent.core :as r]
     [ajax.core :as ajax :refer [POST]])
  )

; this.state.selectedFile,
; this.state.selectedFile.name

(defn upload-file [js-file-value]
  (let [form-data (doto
                    (js/FormData.)
                      (.append "id" "10")
                      (.append "file" js-file-value "filename.txt"))]
   (POST 
     "/api/joseph/upload" 
     {:body form-data
      :response-format (ajax/raw-response-format)
      :timeout 5000})))
 
 
(defn upload-file-ui []
  (let [file-atom (r/atom nil)
        on-file-select (fn [event]
                         (let [target (.-target event)
                               files (.-files target)
                               file-0 (aget files 0)]
                            (println "selected-file: " file-0)
                            (swap! file-atom file-0)))
        upload-file (fn [&args]
                      (println "uploading...")
                      (upload-file @file-atom)
                      (println "uploading finished!"))]
    [:div 
      [:input {:type "file"
               :onChange on-file-select}]
      [:button {:onClick upload-file}]]))

