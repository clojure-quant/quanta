(ns ta.data.load
  (:require
   [taoensso.timbre :refer [info warn error]]
   [ta.warehouse :as wh]
   [ta.data.settings :refer [determine-wh]]
   [ta.helper.ds :refer [ds->map]]
   ))

(defn load-series
  "algo has to create :position column
   creates roundtrips based on this column"
  [{:keys [symbol frequency] :as options}]
  (let [w (determine-wh symbol)
        ds-bars (wh/load-symbol w frequency symbol)]
    (ds->map ds-bars)))