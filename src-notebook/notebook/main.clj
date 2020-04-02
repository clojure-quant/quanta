(ns notebook.main
  (:require
   [pinkgorilla.notebook-app.cli :refer [parse-opts]]
   [pinkgorilla.notebook-app.core :refer [run-gorilla-server]])
  (:gen-class))

(defn start-notebook []
  (let [args2 ["-c" "./src-notebook/config.edn"]
        {:keys [options]} (parse-opts args2)]
    (println "Options Are: " options)
    (run-gorilla-server options)
    nil))

;; the notebook secret management 
;; currently expects ./test/creds.edn file
;; for security, this file is excluded from git

(defn -main [& args]
  (println "Running PinkGorilla Notebook")
  (start-notebook))

(comment
  (start-notebook)
  
  ;comment end
  )