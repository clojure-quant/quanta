(ns demo.studies.supertrend
  (:require
   ;[taoensso.timbre :refer [trace debug info error]]
   [tick.alpha.api :as tick]
   [tech.v3.dataset :as tds]
   [ta.backtest.roundtrip-backtest :refer [run-backtest run-backtest-parameter-range]]
   [ta.backtest.print :refer [print-overview-stats print-roundtrip-stats
                              print-roundtrips print-roundtrips-pl-desc]]
   [ta.backtest.roundtrip-stats :refer [calc-roundtrip-stats]]
   ;[ta.algo.buy-hold :refer [buy-hold-signal]]
   [demo.algo.supertrend :refer [supertrend-signal]]
   [demo.env.config :refer [w-crypto w-random w-shuffled]]))

(comment
  (-> (tds/->dataset {:date [(tick/now) (tick/now) (tick/now)]
                      :open [1 2 3]
                      :high [1 2 3]
                      :low [1 2 3]
                      :close [1 2 3]
                      :volume [0 0 0]})
      (supertrend-signal {:atr-length 10
                          :atr-mult 0.5}))

;  
  )
;; daily backtest

(def options-d
  {:w w-crypto
   :symbol "ETHUSD"
   :frequency "D"
   :atr-length 20
   :atr-mult 0.5})

(def r-d
  (run-backtest supertrend-signal options-d)
  ;(run-backtest supertrend-signal (assoc options-d :symbol "BTCUSD"))
  ;(run-backtest buy-hold-signal (assoc options-d :symbol "BTCUSD"))
  )

r-d
(:ds-roundtrips r-d)
(print-roundtrip-stats r-d)
(print-roundtrips r-d)
(print-roundtrips-pl-desc r-d)
(print-overview-stats r-d)

;; 15min backtest

(def options-15
  {:w w-crypto
   :symbol "ETHUSD"
   :frequency "15"
   :atr-length 20
   :atr-mult 0.75})

(def r-15
  (run-backtest supertrend-signal options-15)
  ;(run-backtest supertrend-signal (assoc options-15 :symbol "BTCUSD"))
  )

(print-roundtrip-stats r-15)
(print-roundtrips r-15)
(print-roundtrips-pl-desc r-15)

; test with random walk

(def r-15-rand
  ;(run-backtest supertrend-signal (assoc options-15 :w w-random))
  (run-backtest supertrend-signal (assoc options-15 :w w-shuffled)))

(print-roundtrip-stats r-15-rand)
(print-roundtrips r-15-rand)
(print-roundtrips-pl-desc r-15-rand)

; optimize ATR MULTIPLYER

(def options-change-atr-mult
  {:w w-crypto
   :symbol "ETHUSD"
   :frequency "15"
   :atr-length 20
   :atr-mult 0.5})

(run-backtest-parameter-range supertrend-signal options-change-atr-mult
                              :atr-mult [0.5 0.75 1.0 1.25 1.5 1.75 2.0 2.5 3.0]
                              print-overview-stats)

(defn profit-factor-long [rt-stats]
  (as-> rt-stats x
    (tds/mapseq-reader x)
    (map (juxt :$group-name identity) x)
    (into {} x)
    (:long x)
    (/ (:pl-prct-cum x) (:pl-prct-max-dd x))
    ;)
    ))

(defn print-profit-factor [backtest-result]
  ;(println "XXX")
  (let [rt-stats (calc-roundtrip-stats backtest-result :position)
        pf (profit-factor-long rt-stats)]
     ;(println "profit factor: " pf)  
    pf))

(defn run-range [w freq]
  (println "run range  wh: " w " freq:" freq)
  (->> (run-backtest-parameter-range supertrend-signal options-change-atr-mult
                                     :atr-mult [0.5 0.75 1.0 1.25 1.5 1.75 2.0 2.5 3.0]
                                     print-profit-factor)
       (println (map print-profit-factor)))
  nil)

(run-range w-crypto "D")

(do (run-range w-crypto "D")
    (run-range w-crypto "15")
    (run-range w-random "D")
    (run-range w-random "15"))

; optimize ATR LENGTH

(def options-change-atr-length
  {:w w-crypto
   :symbol "ETHUSD"
   :frequency "15"
   :atr-length 20
   :atr-mult 0.75})

(run-backtest-parameter-range supertrend-signal options-change-atr-length
                              :atr-mult [5 10 15 20 25 30 35 40 45 50]
                              print-overview-stats)
