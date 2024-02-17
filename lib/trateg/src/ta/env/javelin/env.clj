(ns ta.env.javelin.env
  (:require
   [ta.env.javelin.calendar :as cal]
   [ta.env.core :as env]
   [modular.system]))

; (def ^:dynamic *algo-env*)

(defn- create-env-impl [bar-db]
  (-> {}
      (cal/init)
      (env/set-env-bardb bar-db)))

(defn create-env [bar-db-kw]
  (let [duckdb (bar-db-kw modular.system/system)]
    (create-env-impl duckdb)))






