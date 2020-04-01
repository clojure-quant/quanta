(ns ta.series.compress-speed
  (:require
   [clj-time.core :as t]
   [taoensso.tufte :as tufte :refer (defnp p profiled profile)]
   [ta.series.compress :refer [compress group-month]]))


  ; (require '[db.csv])
(profile
 {}
 (p :all
    (->> ;"AAPL US Equity"
            ;"MCD UN Equity"
     "BP/ LN Equity"
            ;(series.csv/load-series)
     (mongo.series/load-series)
     (series.compress/compress series.compress/group-month)
            ;(take 500)
     (doall)
            ;(last)
     )))