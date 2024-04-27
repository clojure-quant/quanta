(ns ta.viz.ds.metrics
  (:require
   [tech.v3.dataset :as tds]
   [de.otto.nom.core :as nom]
   [ta.viz.error :refer [error-render-spec]]))

(defn ds->map [ds]
  ;(tc/rows :as-maps) ; this does not work, type of it is a reified dataset. 
  ; this works in repl, but when sending data to the browser it fails.
  (into []
        (tds/mapseq-reader ds)))

(defn metrics-render-spec-impl [{:keys [roundtrip-ds nav-ds metrics]}]
  ^{:render-fn 'ta.viz.renderfn/render-spec} ; needed for notebooks
  {:render-fn 'ta.viz.renderfn.metrics/metrics
   :data {:roundtrips (ds->map roundtrip-ds)
          :nav (ds->map nav-ds)
          :metrics metrics}
   :spec {}})

(defn metrics-render-spec
  "returns a render specification {:render-fn :spec :data}. 
   spec must follow chart-pane format.
   The ui shows a barchart with extra specified columns 
   plotted with a specified style/position, 
   created from the bar-algo-ds"
  [spec metrics]
  (if (nom/anomaly? metrics)
    (error-render-spec metrics)
    (metrics-render-spec-impl metrics)))
