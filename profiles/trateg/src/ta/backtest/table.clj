(ns ta.backtest.table
  (:require
   [tech.v3.dataset :as tds]))

(defn table-spec [ds]
  (let [ds-safe (dissoc ds :date)
        data (into [] (tds/mapseq-reader ds-safe))]
    {:box :lg
     :data data}))

(defn ds-table [ds]
  ^:R [:p/aggrid
       (table-spec ds)])



