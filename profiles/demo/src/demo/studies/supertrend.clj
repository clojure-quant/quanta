(ns demo.studies.supertrend
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [tick.alpha.api :as tick]
   [tablecloth.api :as tablecloth]
   [tech.v3.dataset :as tds]
   [ta.helper.print :as helper]
   [ta.backtest.signal :refer [trade-signal]]
   [ta.backtest.backtester :as backtester]
   [ta.backtest.stats :as stats :refer [calc-roundtrips]]
   [demo.strategy.supertrend :as supertrend]
   [demo.env.config :refer [w-crypto w-random w-shuffled]]))

(defn study-supertrend [ds {:keys [atr-length atr-mult] :as options}]
  (let [ds-study (-> ds
                     (supertrend/add-supertrend-signal options)
                     trade-signal)
        ds-roundtrips (calc-roundtrips ds-study)]
    {:ds-study ds-study
     :ds-roundtrips ds-roundtrips}))

(comment
  (-> (tds/->dataset {:date [(tick/now) (tick/now) (tick/now)]
                      :open [1 2 3]
                      :high [1 2 3]
                      :low [1 2 3]
                      :close [1 2 3]
                      :volume [0 0 0]})
      (study-supertrend {:atr-length 10
                         :atr-mult 0.5}))

;  
  )
;; daily backtest

(def options-d
  {:atr-length 20
   :atr-mult 0.5})

(def r-d
  (backtester/run-study
   w-crypto "ETHUSD" "D"
   study-supertrend
   options-d))

(def r-d
  (backtester/run-study
   w-crypto "BTCUSD" "D"
   study-supertrend
   options-d))


r-d
(:ds-roundtrips r-d)
(stats/print-roundtrip-stats r-d)
(stats/print-roundtrips r-d)
(stats/print-roundtrips-pl-desc r-d)

;; 15min backtest

(def options-15
  {:atr-length 20
   :atr-mult 0.75})

(def r-15
  (backtester/run-study w-crypto "ETHUSD" "15"
                        study-supertrend
                        options-15))

(def r-15
  (backtester/run-study w-crypto "BTCUSD" "15"
                        study-supertrend
                        options-15))

(stats/print-roundtrip-stats r-15)
(stats/print-roundtrips r-15)
(stats/print-roundtrips-pl-desc r-15)



; test with random walk

(def r-15-rand
  (backtester/run-study w-random "ETHUSD" "15"
                        study-supertrend
                        options-15))

(def r-15-rand
  (backtester/run-study w-shuffled "ETHUSD" "15"
                        study-supertrend
                        options-15))

(stats/print-roundtrip-stats r-15-rand)
(stats/print-roundtrips r-15-rand)
(stats/print-roundtrips-pl-desc r-15-rand)


; optimize ATR MULTIPLYER

(def options-change-atr-mult
  {:atr-length 20
   :atr-mult 0.5})

(backtester/run-study-parameter-range
 w-crypto "ETHUSD" "15"
 study-supertrend options-change-atr-mult
 :atr-mult [0.5 0.75 1.0 1.25 1.5 1.75 2.0 2.5 3.0]
 stats/print-overview-stats)

(defn profit-factor-long [rt-stats]
  (as-> backtest-result x
    (tds/mapseq-reader x)
    (map (juxt :$group-name identity) x)
    (into {} x)
    (:long x)
    (/ (:pl-prct-cum x) (:pl-prct-max-dd x))
    ;)
    ))

(defn print-profit-factor [backtest-result]
  ;(println "XXX")
  (let [rt-stats (stats/calc-roundtrip-stats backtest-result :position)
        pf (profit-factor-long rt-stats)]
     ;(println "profit factor: " pf)  
    pf))


(defn run-range [w freq]
  (println "run range  wh: " w " freq:" freq)
  (->> (backtester/run-study-parameter-range
        w-crypto "ETHUSD" "15"
        study-supertrend options-change-atr-mult
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
  {:atr-length 20
   :atr-mult 0.75})

(backtester/run-study-parameter-range
 w-crypto "ETHUSD" "15"
 study-supertrend options-change-atr-mult
 :atr-length  [10 15 20 25 30 35 40 45 50]
 stats/print-overview-stats)
