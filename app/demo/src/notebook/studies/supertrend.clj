(ns notebook.studies.supertrend
  (:require
   [tick.core :as tick]
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [ta.backtest.roundtrip-backtest :refer [run-backtest run-backtest-parameter-range]]
   [ta.backtest.roundtrip-stats :as s]
   [ta.backtest.print :as p]
   [demo.algo.supertrend :refer [supertrend-signal]]))

(-> (tds/->dataset {:date [(tick/date-time "2019-01-01T00:00:00")
                           (tick/date-time "2020-01-01T00:00:00")
                           (tick/date-time "2021-01-01T00:00:00")]
                    :open [1 2 3]
                    :high [1 2 3]
                    :low [1 2 3]
                    :close [1 2 3]
                    :volume [0 0 0]})
    (supertrend-signal {:atr-length 10
                        :atr-mult 0.5}))

;; daily backtest

(def options-d
  {:w :crypto
   :symbol "ETHUSD"
   :frequency "D"
   :atr-length 20
   :atr-mult 0.5})

(def r-d
  (run-backtest supertrend-signal options-d)
  ;(run-backtest supertrend-signal (assoc options-d :w w-shuffled))
  ;(run-backtest supertrend-signal (assoc options-d :symbol "BTCUSD"))
  ;(run-backtest buy-hold-signal (assoc options-d :symbol "BTCUSD"))
  )

r-d
(:ds-roundtrips r-d)
(p/print-roundtrip-stats r-d)
(p/print-roundtrips r-d)
(p/print-roundtrips-pl-desc r-d)
(p/print-overview-stats r-d)
(p/viz-roundtrips r-d)

;; 15min backtest

(def options-15
  {:w :crypto
   :symbol "ETHUSD"
   :frequency "15"
   :atr-length 20
   :atr-mult 0.7})

(def r-15
  (run-backtest supertrend-signal options-15)
  ;(run-backtest supertrend-signal (assoc options-15 :symbol "BTCUSD"))
  )

(p/print-roundtrip-stats r-15)
(p/print-roundtrips r-15)
(p/print-roundtrips-pl-desc r-15)
(s/roundtrip-performance-metrics r-15)
(p/viz-roundtrips r-15)
(p/print-nav r-15)

; BTC history
(-> (run-backtest supertrend-signal (assoc options-15 :symbol "BTCUSD" :atr-mult 1.0))
    (p/print-nav))

; test with random walk

(def r-15-rand
  ;(run-backtest supertrend-signal (assoc options-15 :w :random))
  (run-backtest supertrend-signal (assoc options-15 :w :shuffled)))

(p/print-roundtrip-stats r-15-rand)
(p/print-roundtrips r-15-rand)
(p/print-roundtrips-pl-desc r-15-rand)
(s/roundtrip-performance-metrics r-15-rand)
(p/viz-roundtrips r-15-rand)

;; check if statistics are correct

(-> (tc/select-rows (:ds-roundtrips r-15-rand) (range 15690 15691))
    (tc/select-columns [:index-open :index-close
                        :date-open :date-close
                        :price-open :price-close
                        :position :trade]))

(-> (tc/select-rows (:ds-study r-15-rand) (range 89361 89369))
    (tc/select-columns [:index :close :position :trade]))

; optimize ATR MULTIPLYER

(def options-change-atr-mult
  {:w :crypto
   :symbol "BTCUSD"
   :frequency "15"
   :atr-length 20
   :atr-mult 0.5})

(def backtests
  (run-backtest-parameter-range
   supertrend-signal options-change-atr-mult
   :atr-mult [0.5 0.7 0.8 0.9 1.0 1.1 1.2 1.3 1.4 1.5 1.75 2.0 2.5 3.0 3.5 4.0 4.5 5.0]))

(-> backtests
    s/backtests->performance-metrics)

(-> (run-backtest-parameter-range
     supertrend-signal (assoc options-change-atr-mult :symbol "BTCUSD")
     :atr-mult [0.5 0.7 0.8 0.9 1.0 1.1 1.2 1.3 1.4 1.5 1.75 2.0 2.5 3.0 3.5 4.0 4.5 5.0])
    s/backtests->performance-metrics)

; optimize ATR LENGTH

(def options-change-atr-length
  {:w :crypto
   :symbol "ETHUSD"
   :frequency "15"
   :atr-length 20
   :atr-mult 0.75})

(-> (run-backtest-parameter-range
     supertrend-signal options-change-atr-length
     :atr-length [5 10 15 20 25 30 35 40 45 50])
    s/backtests->performance-metrics)

(-> (run-backtest-parameter-range
     supertrend-signal (assoc options-change-atr-length :w :shuffled)
     :atr-length [5 10 15 20 25 30 35 40 45 50])
    s/backtests->performance-metrics)

(->> (run-backtest-parameter-range
      supertrend-signal options-15
      :w [:crypto :shuffled])
     s/backtests->performance-metrics)

