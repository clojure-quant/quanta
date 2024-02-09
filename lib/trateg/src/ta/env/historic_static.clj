(ns ta.env.historic-static
  "historic-static is an environment to which algos can be added.
   
   It just gives back series that are currently saved in the storage db.
   ")

(defn create-historic-static-duckdb 
  "creates a historic-static environment
   bar-series will be loaded via duckdb"
  [duckdb]
  {:duckdb duckdb
   :algos (atom {})
   :env {:get-series (partial duck/get-bars-window duckdb)}})

(defn create-historic-static-nippy
  "creates a historic-static environment
   bar-series will be loaded via nippy
   
   das ist der alte timeseries manager. Aber neues algo interface."
  [duckdb]
  {:duckdb duckdb
   :algos (atom {})
   :env {:get-series (partial timeseries-manager/get-bars-window duckdb)}})


(defn add 
  "adds an algo to the historic-static environment"
  [state algo]
  (println "TODO: implement add!"))

 (defn playback 
   "raises time-events for the specified interval; added algos will
    calculate accoridingly, similar to the live environment"
   [state date-from date-to]
   (println "TODO: implement playback!"))