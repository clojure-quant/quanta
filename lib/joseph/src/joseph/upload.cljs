(ns joseph.upload
  (:require
     [reagent.core :as r]
     [promesa.core :as p]
     [ajax.core :as ajax :refer [POST]]
     [ui.webly :refer [notify]]
   ))

; this.state.selectedFile,
; this.state.selectedFile.name

(defn upload-file [js-file-value]
  (println "uploading file: " js-file-value)
  (let [form-data (doto
                    (js/FormData.)
                      ;(.append "id" "10")
                      (.append "file" js-file-value "filename.txt"))
        rp (POST
             "/api/joseph/upload"
             {:body form-data
              ;:response-format (ajax/raw-response-format)
              :timeout 5000})]
    (-> rp
        (p/then (fn [result]
                  (println "upload success: " result)
                  (notify :info [:div (str result)] 5000)))
        (p/catch (fn [result]
                   (println "upload error: " result)
                  (notify :error "ERROR: " [:div (str result)])))
        )
    nil))
 
 
(defn upload-file-ui []
  (let [file-atom (r/atom nil)
        on-file-select (fn [event]
                         (let [target (.-target event)
                               files (.-files target)
                               file-0 (aget files 0)]
                            (println "selected-file: " file-0)
                            (reset! file-atom file-0)))
        upload-file (fn [&args]
                      (println "uploading...")
                      (upload-file @file-atom)
                      (println "uploading finished!"))]
    (fn []
    [:div 
      [:input {:type "file"
               :onChange on-file-select
               }]
      [:button {:onClick upload-file} "upload-trades"]
     ])))

