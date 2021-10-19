(ns demo.env.algos
  (:require
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.backtest.date :refer [ensure-roundtrip-date-localdatetime]]
   [ta.backtest.roundtrip-backtest :refer [run-backtest]]
   [ta.backtest.roundtrip-stats :refer [roundtrip-performance-metrics]]
   [ta.backtest.nav :refer [nav-metrics nav]]
   [demo.studies.moon :refer [moon-signal]]
   [demo.algo.supertrend :refer [supertrend-signal]]
   [demo.algo.sma :refer [sma-signal]]))

(def algos
  [; moon
   {:name "moon s&p"
    :algo moon-signal
    :w :stocks
    :symbol "SPY"
    :frequency "D"}
   {:name "moon nasdaq"
    :algo moon-signal
    :w :stocks
    :symbol "QQQ"
    :frequency "D"}
   ; supertrend
   {:name "supertrend ETH"
    :algo supertrend-signal
    :w :crypto
    :symbol "ETHUSD"
    :frequency "15"
    :atr-length 20
    :atr-mult 0.7}
   {:name "supertrend BTC"
    :algo supertrend-signal
    :w :crypto
    :symbol "BTCUSD"
    :frequency "15"
    :atr-length 20
    :atr-mult 0.7}
   ; sma
   {:name "sma trendfollow ETH"
    :algo sma-signal
    :w :crypto
    :symbol "ETHUSD"
    :frequency "15"
    :sma-length-st 20
    :sma-length-lt 200}
   {:name "sma trendfollow BTC"
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
          algo-options (dissoc algo-options :algo :name)
          b (run-backtest algo algo-options)
          ds-rts (-> (:ds-roundtrips b) ensure-roundtrip-date-localdatetime)]
      (println "run algo result: " (keys b))
      (println "roundtrip cols: " (->> ds-rts
                                       tc/columns
                                       (map meta)))
      {:rt-metrics (-> (roundtrip-performance-metrics b) ds->map first) ; ds
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



