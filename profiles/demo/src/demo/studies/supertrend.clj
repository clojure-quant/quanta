(ns demo.studies.supertrend
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [tick.alpha.api :as tick]
   [tablecloth.api :as tablecloth]
   [tech.v3.dataset :as tds]
   [ta.dataset.helper :as helper]
   [ta.indicator.supertrend :as supertrend]
   [ta.trade.signal :refer [trade-signal]]
   [ta.dataset.backtest :as backtest]
   [ta.trade.backtest-stats :as stats :refer [calc-roundtrips]]
   [demo.env.config :refer [w-crypto w-random]]))



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
  (backtest/run-study w-crypto "ETHUSD" "D"
                      study-supertrend
                      options-d))

r-d
(:ds-roundtrips r-d)

(stats/print-roundtrips r-d)
(stats/roundtrip-stats r-d)
(stats/print-roundtrips-pl-desc r-d)




(-> (stats/stats r-d)
    (tablecloth/group-by :$group-name :as-map))

;; 15min backtest

(def options-15
  {:atr-length 40
   :atr-mult 0.75})

(def r15
  (backtest/run-study w-crypto "ETHUSD" "15"
                      supertrend/study-supertrend
                      options-15))

(def r15-rand
  (backtest/run-study w-random "ETHUSD" "15"
                      supertrend/study-supertrend
                      options-15))

(stats/stats r15-rand)
(stats/trade-details r15)


;; run a couple different variations

(def options-change-atr-mult
  {:atr-length 20
   :atr-mult 0.5})

(for [m [0.5 0.75 1.0 1.25 1.5 1.75 2.0]]
  (let [options (assoc options-change-atr-mult
                       :atr-mult m)
        _ (println "options: " options)
        r (backtest/run-study w-crypto "ETHUSD" "15"
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
