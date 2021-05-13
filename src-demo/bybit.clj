(ns demo.bybit)

 (c/to-long (-> 2 t/hours t/ago))

  (-> 2 t/hours t/ago)
  (-> (history "15" (-> 2 t/hours t/ago) 5 "ETHUSD")
      (clojure.pprint/print-table))

  (-> (history-recent "BTCUSD" 10)
      (clojure.pprint/print-table))

  (requests-needed 950)
  (clojure.pprint/print-table (history-recent-extended "BTCUSD" 500))
 