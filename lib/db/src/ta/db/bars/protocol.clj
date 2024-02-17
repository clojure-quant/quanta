(ns ta.db.bars.protocol)

(defprotocol bardb
  (get-bars [this opts window])
  (append-bars [this opts ds-bars]))
