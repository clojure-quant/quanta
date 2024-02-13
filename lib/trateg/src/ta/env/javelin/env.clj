(ns ta.env.javelin.env
  (:require
   [ta.env.javelin.calendar :as cal]
   [ta.env.tools.series-static :as series]
   [modular.system]
   ))

(def ^:dynamic *algo-env*)

(defn create-env [duckdb]
      (-> {}
          (cal/init)
          (series/set-env-series-static-duckdb duckdb)
          ))

(defn create-env-duckdb []
  (let [duckdb (:duckdb modular.system/system)]
    (create-env duckdb)))






