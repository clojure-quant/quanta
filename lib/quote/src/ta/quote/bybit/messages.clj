(ns ta.quote.bybit.messages)

(def ping-response-demo
  {"success" true
   "ret_msg" "pong"
   "conn_id" "85ea7772-1b16-4d76-ace2-4ac7e7b6d163"
   "req_id" "100001"
   "op" "ping"})

(def subscription-success-demo
  {"success" true
   "ret_msg" "subscribe"
   "conn_id" "cf71cb32-e914-40db-9710-ac45c8086cae"
   "req_id" "6"
   "op" "subscribe"})

(def trade-msg-demo
  {"topic" "publicTrade.ETHUSDT"
   "ts" 1706476799818
   "type" "snapshot"
   "data" [{"i" "2280000000184479515"
            "T" 1706476799815
            "p" "2263.77"
            "v" "0.01056"
            "S" "Buy"
            "s" "ETHUSDT"
            "BT" false}]})

(def trade-msg-multiple-trades-demo
  {"topic" "publicTrade.ETHUSDT"
   "ts" 1706476982156
   "type" "snapshot"
   "data" [{"i" "2280000000184480265",
            "T" 1706476982154,
            "p" "2262.6",
            "v" "0.19676",
            "S" "Sell",
            "s" "ETHUSDT",
            "BT" :false}
           {"i" "2280000000184480266"
            "T" 1706476982154
            "p" "2262.6"
            "v" "0.17735"
            "S" "Sell"
            "s" "ETHUSDT"
            "BT" false}
           {"i" "2280000000184480267"
            "T" 1706476982154
            "p" "2262.6"
            "v" "0.00512"
            "S" "Sell"
            "s" "ETHUSDT"
            "BT" false}]})
