(ns demo.env.algos
  (:require
   [tablecloth.api :as tc]
   [ta.helper.ds :refer [ds->map]]
   [ta.series.signal :refer [select-signal-has]]
   ; backtest
   [ta.backtest.date :refer [ds-convert-col-instant->localdatetime ensure-roundtrip-date-localdatetime]]
   [ta.backtest.roundtrip-backtest :refer [run-backtest]]
   [ta.backtest.roundtrip-stats :refer [roundtrip-performance-metrics]]
   [ta.backtest.nav :refer [nav-metrics nav]]
   ; viz
   [ta.viz.study-highchart :refer [study-highchart]]
   ; algos
   [ta.algo.buy-hold :refer [buy-hold-signal]]
   [ta.series.gann :refer [algo-gann algo-gann-signal]]
   [demo.algo.moon :refer [moon-signal]]
   [demo.algo.supertrend :refer [supertrend-signal]]
   [demo.algo.sma :refer [sma-signal]]
   [demo.algo.sma-diff :refer [sma-diff-indicator]]))

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
   {:name "moon ETH"
    :comment ""
    :algo moon-signal
    :w :crypto
    :symbol "ETHUSD"
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
    :sma-length-lt 200}
   {:name "gann BTC"
    :comment ""
    :algo algo-gann-signal
    :w :crypto
    :symbol "BTCUSD"
    :frequency "D"
    :box {:ap 8000.0
          :at 180
          :bp 12000.0
          :bt 225}
    :axes-spec [{:sr-up-0 "line"
                 :sr-up-1 "line"
                 :sr-up-2 "line"
                 :sr-down-0 {:type "line" :color "red"}
                 :sr-down-1 {:type "line" :color "red"}
                 :sr-down-2 {:type "line" :color "red"}}
                {:cross-up-close "column"
                 :cross-down-close "column"}
                {:qp "column"
                 ;:qt "column"
                 }
                ;{:index "column"}
               ; {:qt-jump-close "column"}
                ]}
   {:name "sma-diff BTC"
    :comment "experiment"
    :algo sma-diff-indicator
    :w :crypto
    :symbol "BTCUSD"
    :frequency "D"
    :st-mult 2.0
    :sma-length-st 5
    :sma-length-lt 20
    :axes-spec [{:sma-st "line"
                 :sma-lt "line"
                 :sma-diff {:type "line" :color "red"}}]}])

(defn algo-names []
  (map :name algos))

(defn algo-backtest [n]
  (when-let [options (first (filter #(= n (:name %)) algos))]
    (let [algo (:algo options)
          algo-options (dissoc options :algo :name :comment :axes-spec)
          _  (println "running algo:  " n)
          b (run-backtest algo algo-options)]
      (println "run algo result: " (keys b))
      {:name n
       :comment  (:comment options)
       :axes-spec (:axes-spec options)
       :options algo-options
       :backtest b})))

(defn algo-metrics [n]
  (if-let [b (algo-backtest n)]
    (let [backtest (:backtest b)
          ds-rts (-> (:ds-roundtrips backtest) ensure-roundtrip-date-localdatetime)]
      (println "roundtrip cols: " (->> ds-rts
                                       tc/columns
                                       (map meta)))
      {:options (:options b)
       :comment (:comment b)
       :rt-metrics (-> (roundtrip-performance-metrics backtest) ds->map first) ; ds
       :nav-metrics (nav-metrics backtest)
       :roundtrips (-> ds-rts ds->map)
       :nav (-> (nav backtest) ds->map)}) ;ds
    (do (println "algo not found" n)
        nil)))

(defn algo-chart [n]
  (if-let [b (algo-backtest n)]
    (let [ds-study (->  (:ds-study (:backtest b))
                        ; (tc/select-rows (range 1000))
                        )
          axes-spec (:axes-spec b)
          axes-spec (if axes-spec axes-spec
                        [{:trade "flags"}
                         {:volume "column"}])]
      (println "axes spec: " axes-spec)
      (println "run algo result: " (keys b))
      {:name n
       :options (:options b)
       :comment (:comment b)
       :highchart (-> (study-highchart ds-study axes-spec)
                      second)}) ;ds
    (do (println "algo not found" n)
        nil)))

(defn algo-table
  ([n]
   (algo-table n false))
  ([n filter-signal]
   (if-let [b (algo-backtest n)]
     (let [ds-study (->  (:ds-study (:backtest b))
                         ds-convert-col-instant->localdatetime
                         ;(tc/select-rows (range 1000))
                         )
           ds-study (if filter-signal
                      (select-signal-has ds-study :trade)
                      ds-study)]
       {:name n
        :options (:options b)
        :comment (:comment b)
        :table (ds->map ds-study)})
     (do (println "algo not found" n)
         nil))))

(comment
  (algo-names)

  ; test the 4 functions on a strategy that generates a signal
  (require '[ta.viz.study-highchart :refer [ds-epoch series-flags]])
  (-> (algo-backtest "buy-hold s&p")
      ;keys
      :backtest
      :ds-study
      ds-epoch
      (series-flags :trade))

  (-> (algo-metrics "buy-hold s&p")
      keys)

  (-> (algo-chart "buy-hold s&p")
      keys)

  (-> (algo-table "buy-hold s&p")
      keys)

; no signal strategy
  (algo-backtest "gann BTC")
  (algo-chart "gann BTC")

  (algo-backtest "sma-diff BTC")

;  
  )



