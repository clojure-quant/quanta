(ns ta.viz.trade.core
  (:require
   [tech.v3.dataset :as tds]
   [de.otto.nom.core :as nom]
   [ta.viz.error :refer [error-render-spec]]
   [ta.viz.trade.nav-chart :refer [nav-chart]]
   [ta.viz.trade.roundtrip :refer [roundtrip-ui]]))

(defn ds->map [ds]
  ;(tc/rows :as-maps) ; this does not work, type of it is a reified dataset. 
  ; this works in repl, but when sending data to the browser it fails.
  (into []
        (tds/mapseq-reader ds)))

(defn- roundtrip-stats-ui-impl [spec {:keys [roundtrip-ds metrics]}]
  ^{:render-fn 'ta.viz.renderfn/render-spec} ; needed for notebooks
  {:render-fn 'ta.viz.trade.core/roundtrip-stats-ui
   :data {:metrics metrics
          :chart (nav-chart roundtrip-ds)
          :rt (roundtrip-ui {} roundtrip-ds)}
   :spec spec})

(defn roundtrip-stats-ui
  "returns a render specification {:render-fn :spec :data}. 
   spec must follow chart-pane format.
   The ui shows a barchart with extra specified columns 
   plotted with a specified style/position, 
   created from the bar-algo-ds"
  ([metrics]
   (roundtrip-stats-ui {} metrics))
  ([spec metrics]
   (if (nom/anomaly? metrics)
     (error-render-spec metrics)
     (roundtrip-stats-ui-impl spec metrics))))
