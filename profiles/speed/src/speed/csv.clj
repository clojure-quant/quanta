(ns speed.csv
  (:require
   [clj-time.core :as t]
   [taoensso.tufte :as tufte :refer (defnp p profiled profile)]
   [ta.data.csv :refer [load-bars-file save-bars-file load-csv-bars-trateg]]
   ;[ta.series.compress :refer [compress group-month]]
   ))


(defn- test-load []
  (p :load-csv
     (->>
      "ta/spx.csv"
      (load-csv-bars-trateg)
     ;(series.compress/compress series.compress/group-month)
            ;(take 500)
      (doall)
            ;(last)
      )))


(defn speed-csv-load []
  (profile
   {}
   (p :test
      (doall (repeatedly 100 test-load)))))