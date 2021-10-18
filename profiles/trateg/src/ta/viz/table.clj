(ns ta.viz.table
  (:require
   [tech.v3.dataset :as tds]))

(defn ds->table [ds]
  (let [;ds-safe (dissoc ds :date)
        ds-safe ds
        data (into [] (tds/mapseq-reader ds-safe))]
    data))

(defn print-table [ds]
  ^:R [:p/aggrid
       {:box :lg
        :data (ds->table ds)}])





