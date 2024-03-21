(ns ta.viz.ds.vega
  (:require
   [tablecloth.api :as tc]
   [tech.v3.dataset :as tds]))

(defn convert-data [bar-algo-ds columns]
  (->> (tc/select-columns bar-algo-ds columns)
       (tds/mapseq-reader)
       (into [])))

(defn vega-render-spec [{:keys [cols spec] :as vega-spec} bar-algo-ds]
  (when bar-algo-ds
    {:render-fn 'ta.viz.renderfn.vega/vega-lite
     :data {:values (convert-data bar-algo-ds cols)}
     :spec spec}))

