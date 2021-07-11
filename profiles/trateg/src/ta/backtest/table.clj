(ns ta.backtest.table
  (:require
   [tech.v3.dataset :as tds]))

(defn ds->table [ds]
  (let [ds-safe (dissoc ds :date)
        data (into [] (tds/mapseq-reader ds-safe))]
    data))

(defn ds-table [ds]
  ^:R [:p/aggrid
       {:box :lg
        :data (ds->table ds)}
       ])



