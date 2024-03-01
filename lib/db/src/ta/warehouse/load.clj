(ns ta.warehouse.load
  (:require
   [ta.warehouse :as wh]
   [ta.helper.ds :refer [ds->map]]))

(defn load-series
  "algo has to create :position column
   creates roundtrips based on this column
   note: used to send ds to the browser."
  [options]
  (let [ds-bars (wh/load-series options)]
    (ds->map ds-bars)))


