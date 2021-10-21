(ns demo.env.algos
  (:require
   [tablecloth.api :as tc]
   [ta.helper.ds :refer [ds->map]]
   [ta.backtest.date :refer [ds-convert-col-instant->localdatetime ensure-roundtrip-date-localdatetime]]
   [ta.backtest.study :refer [run-study]]
   [ta.backtest.roundtrip-backtest :refer [run-backtest]]
   [ta.backtest.roundtrip-stats :refer [roundtrip-performance-metrics]]
   [ta.backtest.nav :refer [nav-metrics nav]]
   ; algos
   [ta.algo.buy-hold :refer [buy-hold-signal]]
   [ta.series.gann :refer [algo-gann algo-gann-signal]]
   [demo.algo.moon :refer [moon-signal]]
   [demo.algo.supertrend :refer [supertrend-signal]]
   [demo.algo.sma :refer [sma-signal]]
   ; viz
   [ta.viz.study-highchart :refer [study-highchart]]))

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
    :axes-spec [{;:sma200 "line"
                 ;:sma30 "line"
                ; :open "line"
                 :sr-up-0 "line"
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
                ]}])

(defn algo-names []
  (map :name algos))

(defn algo-run [n]
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

(defn algo-chart [n]
  (if-let [algo-options (first (filter #(= n (:name %)) algos))]
    (let [_  (println "running algo:  " n)
          algo (:algo algo-options)
          comment (:comment algo-options)
          axes-spec (:axes-spec algo-options)
          algo-options (dissoc algo-options :algo :name :comment :axes-spec)
          b (run-study algo algo-options)
          ds-study (->  (:ds-study b)
                        ; (tc/select-rows (range 1000))
                        )
          axes-spec (if axes-spec axes-spec
                        [{;:sma200 "line"
                         ;:sma30 "line"
                          }
                         {:open "line"}
                         {:volume "column"}])
          ;ds-rts (-> (:ds-roundtrips b) ensure-roundtrip-date-localdatetime)
          ]
      (println "axes spec: " axes-spec)
      (println "run algo result: " (keys b))
      {:name n
       :options algo-options
       :comment comment
       :highchart (-> (study-highchart ds-study axes-spec)
                      second)}) ;ds
    (do (println "algo not found" n)
        nil)))

;; table brings this error:
;; java.lang.Exception: Not supported: class java.time.Instant

(defn algo-table [n]
  (if-let [algo-options (first (filter #(= n (:name %)) algos))]
    (let [_  (println "running algo:  " n)
          algo (:algo algo-options)
          comment (:comment algo-options)
          algo-options (dissoc algo-options :algo :name :comment :axes-spec)
          b (run-study algo algo-options)
          ds-study (->  (:ds-study b)
                        ds-convert-col-instant->localdatetime
                        (tc/select-rows (range 1000)))]
      (println "algo-table result: " (keys b))
      {:name n
       :options algo-options
       :comment comment
       :table (ds->map ds-study)})
    (do (println "algo not found" n)
        nil)))

(comment
  (algo-names)

  (algo-run "sma trendfollow BTC")

  (algo-chart "sma trendfollow BTC")

  (-> (algo-chart "gann BTC")
      ;keys
      )

  (-> (algo-table "gann BTC")
      ;keys
      )
;  
  )



