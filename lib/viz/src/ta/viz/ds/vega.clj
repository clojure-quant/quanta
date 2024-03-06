(ns ta.viz.ds.vega
  (:require
   [tablecloth.api :as tc]
   [tech.v3.dataset :as tds]))


(defn convert-data [bar-algo-ds columns]
  (->> (tc/select-columns bar-algo-ds columns)
       (tds/mapseq-reader)
       (into [])))
  
  