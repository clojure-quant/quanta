(ns demo.viktor.notebook-supertrend
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [tablecloth.api :as tablecloth]
   [ta.dataset.backtest :as backtest]
   [ta.dataset.helper :as helper]
   [ta.dataset.date :refer [add-year-and-month]]
   [ta.dataset.sma :as sma]
   [ta.dataset.supertrend :as supertrend]
   [ta.dataset.backtest-stats :as stats]
   [demo.viktor.strategy-bollinger :as bs]
   [demo.env.warehouse :refer [w]]))

;; daily backtest

(def options-d
  {:atr-length 20
   :atr-mult 0.5})

(def r-d
  (backtest/run-study w "ETHUSD" "D"
                      supertrend/study-supertrend
                      options-d))

(stats/print r-d)
(stats/stats r-d)
(stats/trades r-d)
(stats/trade-details r-d)

(-> (stats/stats r-d)
    (tablecloth/group-by :$group-name :as-map))

;; 15min backtest

(def options-15
  {:atr-length 40
   :atr-mult 0.75})

(def r15
  (backtest/run-study w "ETHUSD" "15"
                      supertrend/study-supertrend
                      options-15))

(stats/stats r15)
(stats/trade-details r15)

(-> r15
    :date
    (datetime/long-temporal-field :years)
    ;add-year-and-month    
    )
;; run a couple different variations

(def options-change-atr-mult
  {:atr-length 20
   :atr-mult 0.5})

(for [m [0.5 0.75 1.0 1.25 1.5 1.75 2.0]]
  (let [options (assoc options-change-atr-mult
                       :atr-mult m)
        _ (println "options: " options)
        r (backtest/run-study w "ETHUSD" "15"
                              supertrend/study-supertrend
                              options)
        r (tablecloth/set-dataset-name r m)]
    (println r)
    (println "atr-mult: " m)
    (stats/stats r)))

(def options-change-atr-length
  {:atr-length 20
   :atr-mult 0.75})

(for [m [10 15 20 25 30 35 40 45 50]]
  (let [options (assoc options-change-atr-length
                       :atr-length m)
        _ (println "options: " options)
        r (backtest/run-study w "ETHUSD" "15"
                              supertrend/study-supertrend
                              options)
        r (tablecloth/set-dataset-name r m)]
    (println r)
    (println (stats/stats r))))
