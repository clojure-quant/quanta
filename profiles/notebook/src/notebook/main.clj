(ns notebook.main
  (:require
   [clojure.core.async :refer [thread]]
   ;[dataset-tools.core :as dt] crashes all. opened ticket
   [pinkgorilla.notebook-app.cli :refer [parse-opts]]
   [pinkgorilla.notebook-app.core :refer [run-gorilla-server]])
  (:gen-class))

(defn run-notebook []
  (let [args2 ["-c" "./profiles/notebook/config.edn"]
        {:keys [options]} (parse-opts args2)]
    (println "Options Are: " options)
    (run-gorilla-server options)
    nil))

(defn start []
  (thread
   (run-notebook)))

;; the notebook secret management 
;; currently expects ./test/creds.edn file
;; for security, this file is excluded from git

(defn -main [& args]
  (println "Running PinkGorilla Notebook")
  (run-notebook))

(comment
  (run-notebook)

  ;comment end
  )