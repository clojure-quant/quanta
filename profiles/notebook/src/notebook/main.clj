(ns notebook.main
  (:require
   ;[clojure.core.async :refer [thread]]
   ;[dataset-tools.core :as dt] crashes all. opened ticket
   ;[pinkgorilla.notebook-app.cli :refer [parse-opts]]
   ;[pinkgorilla.notebook-app.core :refer [run-gorilla-server]]
   [pinkgorilla.embedded :refer [run-notebook #_start-notebook]]
   [gigasquid.utils]) ; bring to jar
  (:gen-class))

;; the notebook secret management 
;; currently expects ./test/creds.edn file
;; for security, this file is excluded from git

(defn start []
  (run-notebook "./profiles/notebook/config.edn"))

(defn -main []
  (println "Running PinkGorilla Notebook")
  (start))

(comment
  (run-notebook)

  ;comment end
  )