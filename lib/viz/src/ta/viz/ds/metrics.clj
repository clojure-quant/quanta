(ns ta.viz.ds.metrics
  (:require
   [tech.v3.dataset :as tds]
   [ta.trade.core :refer [trade-summary]]))

(defn ds->map [ds]
  ;(tc/rows :as-maps) ; this does not work, type of it is a reified dataset. 
  ; this works in repl, but when sending data to the browser it fails.
  (into []
        (tds/mapseq-reader ds)))

(defn metrics-render-spec
  "returns a render specification {:render-fn :spec :data}. 
   spec must follow chart-pane format.
   The ui shows a barchart with extra specified columns 
   plotted with a specified style/position, 
   created from the bar-algo-ds"
  [spec bar-signal-ds]
  (let [{:keys [trade-ds roundtrip-ds nav-ds metrics]} (trade-summary bar-signal-ds) 
        ]
    ;(assert (chart-pane-spec? pane-spec) "please comply with chart-pane-spec")
    {:render-fn 'ta.viz.renderfn.metrics/metrics
     :data {:trades (ds->map trade-ds)
            :roundtrips (ds->map roundtrip-ds)
            :nav (ds->map nav-ds)
            :metrics metrics}
     :spec {}}))

  