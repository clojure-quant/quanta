(ns ta.viz.ds.metrics
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [tech.v3.dataset :as tds]
   [de.otto.nom.core :as nom]
   [ta.trade.core :refer [trade-summary]]
   [ta.viz.error :refer [error-render-spec]]))

(defn ds->map [ds]
  ;(tc/rows :as-maps) ; this does not work, type of it is a reified dataset. 
  ; this works in repl, but when sending data to the browser it fails.
  (into []
        (tds/mapseq-reader ds)))

(defn trade-summary-safe [ds]
  (try
    (trade-summary ds)
    (catch Exception ex
      (error "exception in trade-summary calc: " ex)
      (nom/fail ::viz-calc {:message "algo viz-calc exception!"
                            :location :trade-summary}))))

(defn metrics-render-spec-impl [{:keys [roundtrip-ds nav-ds metrics]}]
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
  [spec bar-signal-ds]
  (let [summary (trade-summary-safe bar-signal-ds)]
    (if (nom/anomaly? summary)
      (error-render-spec summary)
      (metrics-render-spec-impl summary))))
