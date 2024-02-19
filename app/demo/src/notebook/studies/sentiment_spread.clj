(ns notebook.studies.sentiment-spread
  "Sentiment Spreads
  Backtest of a strategy described in 
  https://cssanalytics.wordpress.com/2010/09/19/creating-an-ensemble-intermarket-spy-model-with-etf-rewinds-sentiment-spreads/
  The indicator according to Jeff Pietsch (who is the creator of ETF Rewind) is most valuable for intraday-trading as an indicator that captures the market’s sentiment towards risk assets. A positive spread or positive differential return implies that the market is willing to take risk and thus likely to go higher. By extension, the more spreads that are positive, 
  or the greater the sum of the spreads, the more likely the market will go up and vice versa"
  (:require
   [tablecloth.api :as tc]
   [ta.calendar.core :as cal]
   [ta.env.javelin.backtest :refer [run-backtest]]
   [ta.env.javelin.env :refer [create-env]]
   [ta.env.javelin.algo :as dsl]
   [notebook.algo.sentiment-spread :refer [sentiment-spread]]))


(def algo-spec {:calendar [:us :d]
                :import :kibot
                :trailing-n 100
                :spreads [[:consumer-sentiment "XLY" "XLP"]
                          [:smallcap-speculative-demand "IWM" "SPY"]
                          [:em-speculative-demand "EEM" "EFA"]
                          [:innovation-vs-safehaven "XLK" "GLD"]
                          [:stocks-vs-bonds "SPY" "AGG"]
                          [:quality-yield-spreads "HYG" "AGG"]
                          [:yen-eur-currency "FXE" "FXY"]
                          ; 8th spread- VXX-VXZ – due to insufficient historical data.
                          ]})


(def window (-> (cal/trailing-range-current [:us :d] 1)
                   ;(window-as-date-time)
                ))

window

(def env (create-env :bardb-dynamic))
(def strategy (dsl/add-time-strategy env algo-spec sentiment-spread))
(run-backtest env window)


@strategy

; correlation between factors and spx
; (stats/cor 'm spy :method "pearson" :use "pairwise.complete.obs")


(defn distribution [ds-sentiment]
  (-> ds-sentiment
      (tc/group-by :sentiment)
      (tc/aggregate
       {:count (fn [ds] (tc/row-count ds))})
      (tc/order-by :$group-name)))

(distribution @strategy)

; | :$group-name | :count |
; |-------------:|-------:|
; |          6.0 |      7 |
; |          0.0 |     29 |
; |          2.0 |     24 |
; |         -2.0 |     23 |
; |          4.0 |     11 |
; |         -4.0 |      5 |
; |         -6.0 |      1 |


(def algo-spec-long (assoc algo-spec :trailing-n 1000))

(def env (create-env :bardb-dynamic))
(def strategy (dsl/add-time-strategy env algo-spec-long sentiment-spread))
(run-backtest env window)

(distribution @strategy)

;; | :$group-name | :count |
;; |-------------:|-------:|
;; |         -6.0 |     11 |
;; |         -4.0 |     76 |
;; |         -2.0 |    221 |
;; |          0.0 |    278 |
;; |          2.0 |    260 |
;; |          4.0 |     99 |
;; |          6.0 |     55 |