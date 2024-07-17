(ns quanta.market.broker.bybit.quote
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [missionary.core :as m]
   [quanta.market.broker.bybit.message :as parser]
   [quanta.market.broker.bybit.connection :refer [connection3 send-msg!]]))

; https://bybit-exchange.github.io/docs/v5/websocket/public/trade

(def topics
  {:order/execution "ticketInfo"
   :order/update "order"
    ; market
   :asset/orderbook "orderbook.%s.%s" ; depth asset OK
   :asset/orderbook-top  "bookticker.%s" ; best bid ask every 100ms  NO
   :asset/bars "kline.%s.%s" ; interval asset ; OK
   :asset/stats "tickers.%s" ; OK
   :asset/trade "publicTrade.%s"  ; symbol realtime  OK
   :asset/liquidation "liquidation.%s" ; BAD
   })

(defn topic [type & args]
  (if-let [s (get topics type)]
    (apply format s args)
    (throw (Exception. (ex-info "topic not found" {:type type
                                                   :args args})))))

(def test-msg-asset-trade
  {:type "snapshot",
   :topic "publicTrade.BTCUSDT",
   :ts 1721142198594,
   :data [{:i 2290000000256236200, :T 1721142198592, :p 63839.81, :v 0.003031, :S "Buy", :s "BTCUSDT", :BT false}
          {:i 2290000000256236201, :T 1721142198592, :p 63839.81, :v 0.004738, :S "Buy", :s "BTCUSDT", :BT false}
          {:i 2290000000256236202, :T 1721142198592, :p 63839.81, :v 0.004739, :S "Buy", :s "BTCUSDT", :BT false}
          {:i 2290000000256236203, :T 1721142198592, :p 63839.81, :v 0.004739, :S "Buy", :s "BTCUSDT", :BT false}
          {:i 2290000000256236204, :T 1721142198592, :p 63839.87, :v 0.000469, :S "Buy", :s "BTCUSDT", :BT false}]})

(def test-msg-asset-stats
  {:type "snapshot"
   :topic "tickers.BTCUSDT"
   :ts 1721227632068
   :cs 34573306044,
   :data {:symbol "BTCUSDT"
          :price24hPcnt 0.0250
          :prevPrice24h 63638.89
          :volume24h 18049.117915
          :turnover24h 1173560587.96609125
          :usdIndexPrice 65244.815694
          :lastPrice 65232.7
          :lowPrice24h 63592.23
          :highPrice24h 66129.54}})

(def test-msg-asset-bars
  {:type "snapshot",
   :topic "kline.M.BTCUSDT",
   :ts 1721227909998,
   :data [{:confirm false,
           :open 62772.83,
           :turnover 16187728730.4876258,
           :start 1719792000000,
           :close 65080.85,
           :volume 271788.26813,
           :high 66129.54,
           :low 53345.94,
           :interval "M",
           :end 1722470399999,
           :timestamp 1721227909998}]})

(def test-msg-asset-orderbook-snapshot
  {:topic "orderbook.1.BTCUSDT"
   :type "snapshot",
   :cts 1721229270063,
   :ts 1721229270067,
   :data {:s "BTCUSDT", :b [["65067.81" "0.32033"]], :seq 34575330925, :a [["65067.82" "0.156943"]], :u 33575893}})

(def test-msg-asset-orderbook-delta
  {:topic "orderbook.1.BTCUSDT"
   :type "delta"
   :cts 1721229275364
   :ts 1721229275366,
   :data {:s "BTCUSDT", :b [], :seq 34575334606, :a [["65066.62" "0.133354"]], :u 33576050}})

(defn subscription-msg [topic]
  {"op" "subscribe"
   "args" [topic]})

(defn topic-snapshot? [target-topic]
  (fn [{:keys [type topic]}]
    (and (= type "snapshot")
         (= topic target-topic))))

(defn subscribe
  [connection topic]
  (m/ap
   (info "subscribe: " topic " waiting for stream..")
   (if-let [c (m/?> connection)]
     (do
       (info "subscribing: " topic)
       (send-msg! c (subscription-msg topic))
       (let [my-topic? (topic-snapshot? topic)
             msg (m/?> (:msg-flow c))]
         (when (my-topic? msg)
           msg)))
     (do
       (error "subscribe error - cannot connect.")
       nil))))

(defn quotes->quote [quotes]
  (m/ap
   (loop [quotes quotes]
     (m/amb
      (first quotes)
      (when (seq quotes)
        (recur (rest quotes)))))))

(defn process-trade [trades])

 ;   quotes (parser/parse-snapshot msg)
(defn parse-snapshot [{:keys [topic ts type data]}]
  (map parse-bybit-trade data)
  [])

(comment
  (def print-data (fn [r q] (println q)))
  (def account
    (connection3 {:mode :main
                  :segment :spot}))

  (def t
     ;"publicTrade.BTCUSDT"
     ;"tickers.BTCUSDT"
    ;(topic :asset/bars "M" "BTCUSDT")
    ;(topic :asset/stats "BTCUSDT")
    ;(topic :asset/orderbook-top "BTCUSDT")
    ;(topic :asset/trade "BTCUSDT")
    (topic :asset/liquidation "BTCUSDT")
    ;(topic :asset/orderbook "1" "BTCUSDT")
    ;(topic :order/update)
    )

  t

  ; msg rcvd: {:ret_code "400", :type "error", :ret_msg "Invalid JSON!"}
  ;msg rcvd:  
  {:op "subscribe"
   :success false
   :conn_id "18f54745-ef6c-46b6-be18-3c8f1c59ee49"
   :ret_msg "Invalid topic :[bookticker.BTCUSDT]"}

  {:op "subscribe", :success false, :conn_id "26787c6d-a71f-493c-a9e3-364bd1fe46b3",
   :ret_msg "Invalid topic :[trade.BTCUSDT]"}

  {:op "subscribe"
   :success true
   :conn_id "cf18ebd9-9506-4b84-81ee-57b0027fd2ea"
   :ret_msg "subscribe"}

  (m/?
   (m/reduce print-data nil
             (subscribe account t)))

  
  
  (defn cont [flow]
    (->> flow
         (m/reductions (fn [r v]
                         (if v v r)) nil)
         (m/relieve {})))

  (let [assets ["BTCUSDT" "ETHUSDT"]
        get-quote (fn [asset]
                    (get-quote :bybit quote-account asset))
        quotes (map get-quote assets)
        quotes-cont (map cont quotes)
        last-quotes (apply m/latest vector quotes-cont)]
    (m/?
     (m/reduce println nil last-quotes))
     ;(m/reduce print-quote nil (first quotes))
    )
; 
  )