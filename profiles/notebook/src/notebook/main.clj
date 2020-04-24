(ns notebook.main
  (:require
   [pinkgorilla.embedded :refer [run-notebook #_start-notebook]]
   [gigasquid.utils]) ; bring to jar
  (:gen-class))

(defn start []
  (run-notebook "./profiles/notebook/config.edn"))

(defn -main []
  (println "Running PinkGorilla Notebook")
  (start))

(comment
  (run-notebook)

  ;comment end
  )