(ns quanta.market.connection.bybit.messages)



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
  {"type" "snapshot"
   "topic" "publicTrade.ETHUSDT"
   "ts" 1706476799818
   "data" [{"i" "2280000000184479515"
            "T" 1706476799815
            "p" "2263.77"
            "v" "0.01056"
            "S" "Buy"
            "s" "ETHUSDT"
            "BT" false}]})

(def trade-msg-multiple-trades-demo
  {"type" "snapshot"
   "topic" "publicTrade.ETHUSDT"
   "ts" 1706476982156
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



;; SNAPSHOT message

 [{:i "2280000000501621332", :T 1720978811575, :p "3192.02", :v "0.00689", :S "Sell", :s "ETHUSDT", :BT false}
  {:i "2280000000501621333", :T 1720978811575, :p "3192.02", :v "0.015", :S "Sell", :s "ETHUSDT", :BT false}]
 
 [{:i "2280000000501621334", :T 1720978811586, :p "3191.99", :v "0.00689", :S "Sell", :s "ETHUSDT", :BT false}]
 
 [{:i "2290000000252374500", :T 1720978811603, :p "60013.98", :v "0.048608", :S "Sell", :s "BTCUSDT", :BT false}
  {:i "2290000000252374501", :T 1720978811603, :p "60013.77", :v "0.000499", :S "Sell", :s "BTCUSDT", :BT false}
  {:i "2290000000252374502", :T 1720978811603, :p "60013.17", :v "0.000499", :S "Sell", :s "BTCUSDT", :BT false}
  {:i "2290000000252374503", :T 1720978811603, :p "60013.14", :v "0.003119", :S "Sell", :s "BTCUSDT", :BT false}
  {:i "2290000000252374504", :T 1720978811603, :p "60012.69", :v "0.001837", :S "Sell", :s "BTCUSDT", :BT false}
  {:i "2290000000252374505", :T 1720978811603, :p "60012.57", :v "0.000499", :S "Sell", :s "BTCUSDT", :BT false}
  {:i "2290000000252374506", :T 1720978811603, :p "60012.38", :v "0.002586", :S "Sell", :s "BTCUSDT", :BT false}
  {:i "2290000000252374507", :T 1720978811603, :p "60011.97", :v "0.000499", :S "Sell", :s "BTCUSDT", :BT false}
  {:i "2290000000252374508", :T 1720978811603, :p "60011.86", :v "0.000271", :S "Sell", :s "BTCUSDT", :BT false}]
 
 [{:i "2280000000501621341", :T 1720978811614, :p "3191.98", :v "0.0068", :S "Sell", :s "ETHUSDT", :BT false}]

