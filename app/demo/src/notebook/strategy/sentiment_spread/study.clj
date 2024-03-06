(ns notebook.strategy.sentiment-spread.study
  "Sentiment Spreads
  Backtest of a strategy described in 
  https://cssanalytics.wordpress.com/2010/09/19/creating-an-ensemble-intermarket-spy-model-with-etf-rewinds-sentiment-spreads/
  The indicator according to Jeff Pietsch (who is the creator of ETF Rewind) is most valuable for 
  intraday-trading as an indicator that captures the market’s sentiment towards risk assets. 
  A positive spread or positive differential return implies that the market is willing to take risk and thus 
  likely to go higher. By extension, the more spreads that are positive, 
  or the greater the sum of the spreads, the more likely the market will go up and vice versa"
  (:require
   [tablecloth.api :as tc]
   [ta.algo.backtest :refer [backtest-algo]]
   [notebook.strategy.sentiment-spread.vega :as v]))

(def algo-spec {:type :time
                :algo 'notebook.strategy.sentiment-spread.algo/sentiment-spread
                :calendar [:us :d]
                :import :kibot
                :trailing-n 1000
                :market "SPY"
                :spreads [[:consumer-sentiment "XLY" "XLP"]
                          [:smallcap-speculative-demand "IWM" "SPY"]
                          [:em-speculative-demand "EEM" "EFA"]
                          [:innovation-vs-safehaven "XLK" "GLD"]
                          [:stocks-vs-bonds "SPY" "AGG"]
                          [:quality-yield-spreads "HYG" "AGG"]
                          [:yen-eur-currency "FXE" "FXY"]
                          ; 8th spread- VXX-VXZ – due to insufficient historical data.
                          ]})

(def sentiment-ds
  (backtest-algo :bardb-dynamic algo-spec))


@sentiment-ds

(tc/info @sentiment-ds)
;; => _unnamed: descriptive-stats [12 11]:
;;    
;;    |                    :col-name |       :datatype | :n-valid | :n-missing |                 :min |                    :mean |                 :max | :standard-deviation |       :skew |               :first |                :last |
;;    |------------------------------|-----------------|---------:|-----------:|----------------------|--------------------------|----------------------|--------------------:|------------:|----------------------|----------------------|
;;    |          :consumer-sentiment |        :float32 |      985 |          0 |               -22.87 |                   0.4639 |                20.82 |      6.65124058E+00 | -0.39324198 |                7.316 |                3.675 |
;;    | :smallcap-speculative-demand |        :float32 |      985 |          0 |               -25.54 |                   -1.468 |                25.66 |      7.71564796E+00 |  0.29455558 |               -1.390 |               -7.340 |
;;    |       :em-speculative-demand |        :float32 |      985 |          0 |               -4.720 |                  -0.1724 |                5.650 |      1.43302939E+00 |  0.23501041 |               -4.165 |               -1.210 |
;;    |     :innovation-vs-safehaven |        :float32 |      985 |          0 |               -23.87 |                   0.8504 |                25.35 |      7.33385688E+00 | -0.16277561 |                4.970 |                3.040 |
;;    |             :stocks-vs-bonds |        :float32 |      985 |          0 |               -47.79 |                    2.384 |                39.83 |      1.23799362E+01 | -0.66794398 |                17.52 |                14.67 |
;;    |       :quality-yield-spreads |        :float32 |      985 |          0 |               -3.808 |                   0.1681 |                3.775 |      9.91391559E-01 | -0.15563771 |                3.060 |              -0.2100 |
;;    |            :yen-eur-currency |        :float32 |      985 |          0 |               -4.235 |                   0.2388 |                5.250 |      1.38861694E+00 |  0.06231495 |                2.760 |               0.3850 |
;;    |                        :date | :packed-instant |      985 |          0 | 2020-05-28T21:00:00Z | 2022-04-18T02:10:06.700Z | 2024-03-06T22:00:00Z |      3.44122216E+10 |  0.00001107 | 2020-05-28T21:00:00Z | 2024-03-06T22:00:00Z |
;;    |                         :pos |         :double |      985 |          0 |                0.000 |                    3.821 |                7.000 |      1.37654285E+00 | -0.13777691 |                5.000 |                4.000 |
;;    |                         :neg |         :double |      985 |          0 |                0.000 |                    3.179 |                7.000 |      1.37654285E+00 |  0.13777691 |                2.000 |                3.000 |
;;    |                   :sentiment |         :double |      985 |          0 |               -7.000 |                   0.6426 |                7.000 |      2.75308569E+00 | -0.13777691 |                3.000 |                1.000 |
;;    |                      :market |        :float64 |      985 |          0 |                300.0 |                    411.1 |                512.8 |      4.41087269E+01 | -0.35145211 |                303.0 |                512.3 |


; correlation between factors and spx
; (stats/cor 'm spy :method "pearson" :use "pairwise.complete.obs")


(defn distribution [sentiment-ds]
  (-> sentiment-ds
      (tc/group-by :sentiment)
      (tc/aggregate
       {:count (fn [ds] (tc/row-count ds))})
      (tc/order-by :$group-name)))

(distribution @sentiment-ds)
;; => _unnamed [8 2]:
;;    
;;    | :$group-name | :count |
;;    |-------------:|-------:|
;;    |         -7.0 |      9 |
;;    |         -5.0 |     55 |
;;    |         -3.0 |    174 |
;;    |         -1.0 |    208 |
;;    |          1.0 |    211 |
;;    |          3.0 |    216 |
;;    |          5.0 |     76 |
;;    |          7.0 |     46 |


(def algo-spec-4000 (assoc algo-spec :trailing-n 4000))

(def sentiment-4000-ds
  (backtest-algo :bardb-dynamic algo-spec-4000))

(v/publish-vega @sentiment-4000-ds :sentiment)


(distribution @sentiment-4000-ds)
;; => _unnamed [8 2]:
;;    
;;    | :$group-name | :count |
;;    |-------------:|-------:|
;;    |         -7.0 |     37 |
;;    |         -5.0 |    229 |
;;    |         -3.0 |    659 |
;;    |         -1.0 |    832 |
;;    |          1.0 |    975 |
;;    |          3.0 |    891 |
;;    |          5.0 |    324 |
;;    |          7.0 |     38 |


