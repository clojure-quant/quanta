(ns ta.viz.trade.m2m.core
  (:require
   [tech.v3.dataset :as tds]))

(defn- ds->map [ds]
  ;(tc/rows :as-maps) ; this does not work, type of it is a reified dataset. 
  ; this works in repl, but when sending data to the browser it fails.
  (into []
        (tds/mapseq-reader ds)))

(defn m2m-chart [m2m-data]
  ; todo: add warnings (because they are relevant!)
  (with-meta
    (-> (update m2m-data :eff ds->map)
        :eff)
    {:render-fn 'ta.viz.trade.m2m.vega/m2m-chart}))