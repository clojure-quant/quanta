(ns demo.main.performance
  (:require
   [taoensso.tufte :as tufte :refer (defnp p profiled profile)]
   [speed.csv :refer [speed-csv-load]])
  (:gen-class))

(defn -main [& args]
  (println "Running Performance Tests:")

  (tufte/add-basic-println-handler! {})

  (speed-csv-load)

  (Thread/sleep 5000) ;sleep needed for tufte performance logging, otherwise app stops to fast
  (System/exit 0)    ; otherwise log4js will not stop
  )