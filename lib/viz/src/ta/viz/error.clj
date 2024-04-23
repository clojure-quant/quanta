(ns ta.viz.error
  (:require
   [de.otto.nom.core :as nom]))

(defn error-render-spec
  "returns a render specification {:render-fn :spec :data}. 
   spec must follow chart-pane format.
   The ui shows a barchart with extra specified columns 
   plotted with a specified style/position, 
   created from the bar-algo-ds"
  [nom-anomaly]
  ^{:render-fn 'ta.viz.renderfn/render-spec} ; needed for notebooks
  {:render-fn 'ta.viz.renderfn.error/nom-error
   :data nom-anomaly
   :spec :whatever})