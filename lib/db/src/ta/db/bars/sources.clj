(ns ta.db.bars.sources
  (:require
   [tech.v3.dataset :as tds]
   [ta.db.bars.protocol :as b]))

(defonce sources (atom {}))

(defn start-bardb-sources [m]
  (reset! sources m)
  m)

(defn ds->map [ds]
  ;(tc/rows :as-maps) ; this does not work, type of it is a reified dataset. 
  ; this works in repl, but when sending data to the browser it fails.
  (into []
        (tds/mapseq-reader ds)))

(defn get-bars-source [source opts window]
  (-> (b/get-bars (get @sources source)
                  opts
                  window)
      ds->map))
