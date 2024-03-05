(ns ta.db.bars.protocol)

(defprotocol barsource
  (get-bars [this opts window]))

(defprotocol bardb
  ;(get-bars [this opts window])
  (append-bars [this opts ds-bars]))
