(ns ta.tradingview.interface)

(defprotocol tradingview
  "TradingView Templates"
  (config [this])

  (search [this query type exchange limit])
  (symbol-info [this symbol])

  (load-series [this symbol resolution from to])

  (chart-list [this client user])
  (load-chart [this client user chart-id])
  (save-chart [this client-id user-id data])
  (modify-chart [this client-id user-id chart-id data])
  (delete-chart [this client-id user-id chart-id])

  (load-template [this client-id user-id template-id])
  (save-template [this client-id user-id data])
  (delete-template [this client-id user-id template]))