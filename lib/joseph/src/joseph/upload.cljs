(ns joseph.upload
  (:require
     [reagent.core :as r])
  )

 const formData = new FormData();
 
        // Update the formData object
        formData.append(
            "myFile",
            this.state.selectedFile,
            this.state.selectedFile.name
        );
 
        // Details of the uploaded file
        console.log(this.state.selectedFile);
 
        // Request made to the backend api
        // Send formData object
        axios.post("api/uploadfile", formData);
 
 
(defn upload-file-dialog []
  (let [file-atom (r/atom nil)
        on-file-select (fn [event]
                         (let [target (.-target event)
                               files (.-files target)
                               file-0 (aget files 0)]
                            (println "selected-file: " file-0)
                            (swap! file-atom file-0)))

        upload-file (fn [&args]
                      
                      )        
        ]
  [:input {:type "file"
           :onChange upload-file
           }]
    [:button {:onClick upload-file}]
  )

