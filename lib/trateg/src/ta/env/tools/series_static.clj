(ns ta.env.tools.series-static
  "series-static adds a barsieries getter that 
   just gives back series that are currently saved in the storage db."
   (:require
   [ta.warehouse.duckdb :as duck]
   [ta.warehouse :as nippy]))

(defn set-env-series-static-duckdb 
  "creates environment to load series via duckdb"
  [env duckdb]
  (assoc env 
         :get-series (partial duck/get-bars-window duckdb)))

(defn create-historic-static-nippy
  "creates environment to load series via nippy
   this is the old timeseries manager, but with new api"
  [env nippy-directory]
  (assoc env 
         :get-series nippy/load-symbol))
   