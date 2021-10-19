(ns demo.env.algos
  (:require
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.backtest.date :refer [ensure-roundtrip-date-localdatetime]]
   [ta.backtest.roundtrip-backtest :refer [run-backtest]]
   [ta.backtest.roundtrip-stats :refer [roundtrip-performance-metrics]]
   [ta.backtest.nav :refer [nav-metrics nav]]
   ; algos
   [ta.algo.buy-hold :refer [buy-hold-signal]]
   [demo.studies.moon :refer [moon-signal]]
   [demo.algo.supertrend :refer [supertrend-signal]]
   [demo.algo.sma :refer [sma-signal]]))

(def algos
  [; buyhold
   {:name "buy-hold s&p"
    :comment "much better than b/h nasdaq"
    :algo buy-hold-signal
    :w :stocks
    :symbol "SPY"
    :frequency "D"}
   {:name "buy-hold nasdaq"
    :comment "huge drawdown kills returns"
    :algo buy-hold-signal
    :w :stocks
    :symbol "QQQ"
    :frequency "D"}
   ; moon
   {:name "moon s&p"
    :comment "very good - 2:1"
    :algo moon-signal
    :w :stocks
    :symbol "SPY"
    :frequency "D"}
   {:name "moon nasdaq"
    :comment "not as good as moon/s&p but big improvement"
    :algo moon-signal
    :w :stocks
    :symbol "QQQ"
    :frequency "D"}
   ; supertrend
   {:name "supertrend ETH"
    :comment "a 15min strategy should be better than daily moon"
    :algo supertrend-signal
    :w :crypto
    :symbol "ETHUSD"
    :frequency "15"
    :atr-length 20
    :atr-mult 0.7}
   {:name "supertrend BTC"
    :comment "REALLY BAD! do not trade."
    :algo supertrend-signal
    :w :crypto
    :symbol "BTCUSD"
    :frequency "15"
    :atr-length 20
    :atr-mult 0.7}
   ; sma
   {:name "sma trendfollow ETH"
    :comment "best strategy so far!"
    :algo sma-signal
    :w :crypto
    :symbol "ETHUSD"
    :frequency "15"
    :sma-length-st 20
    :sma-length-lt 200}
   {:name "sma trendfollow BTC"
    :comment "by far not so good as sma trendfollow ETH"
    :algo sma-signal
    :w :crypto
    :symbol "BTCUSD"
    :frequency "15"
    :sma-length-st 20
    :sma-length-lt 200}])

(defn algo-names []
  (map :name algos))

(defn ds->map [ds]
  (into [] (tds/mapseq-reader ds)))

(defn run-algo [n]
  (if-let [algo-options (first (filter #(= n (:name %)) algos))]
    (let [algo (:algo algo-options)
          comment (:comment algo-options)
          algo-options (dissoc algo-options :algo :name :comment)
          b (run-backtest algo algo-options)
          ds-rts (-> (:ds-roundtrips b) ensure-roundtrip-date-localdatetime)]
      (println "run algo result: " (keys b))
      (println "roundtrip cols: " (->> ds-rts
                                       tc/columns
                                       (map meta)))
      {:options algo-options
       :comment comment
       :rt-metrics (-> (roundtrip-performance-metrics b) ds->map first) ; ds
       :nav-metrics (nav-metrics b)
       :roundtrips (-> ds-rts ds->map)
       :nav (-> (nav b) ds->map)}) ;ds
    (do (println "algo not found" n)
        nil)))

(comment
  (algo-names)

  (run-algo "sma trendfollow BTC")
;  
  )



