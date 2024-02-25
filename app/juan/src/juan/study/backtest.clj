(ns juan.study.backtest
  (:require
   [tablecloth.api :as tc]
   [ta.algo.backtest :refer [backtest-algo]]))

(def settings
  {:atr-n 20 ; number days used for atr calculation
   :sentiment-treshold 70.0
   :spike-atr-min-prct 30.0 ; 75 (currently set lower for testing)
   :future "23Z" ; used ?
   :future-year 23 ; future year for volume pivots
   :future-month 12 ; future month for volume pivots
   :pivot-max-pip-distance 5.0})

(def algo-spec
  [:day {:type :trailing-bar
         :algo  'juan.algo.daily/daily
         :calendar [:us :d]
         :asset "EURUSD"
         :import :kibot
         :feed :fx
         :trailing-n 20
         :atr-n 10
         :step 0.0001
         :percentile 70}
   :minute {:calendar [:forex :m]
            :algo  ['juan.algo.intraday/ensure-date-unique
                    'juan.algo.doji/doji-signal]
            :type :trailing-bar
            :asset "EURUSD"
            :import :kibot-http
            :trailing-n 10000
            ; doji
            :max-open-close-over-low-high 0.3
            :volume-sma-n 30
            ; pivots
            :step 10.0
            :percentile 70}
   :signal {:formula [:day :minute]
            :algo 'juan.algo.combined/daily-intraday-combined}])

(def combined (backtest-algo :duckdb algo-spec))
; (def combined (backtest-algo :bardb-dynamic algo-spec))

(tc/select-columns @(:day combined) [:date :atr :close])
;; => :_unnamed [20 3]:
;;    
;;    |                :date |     :atr |  :close |
;;    |----------------------|---------:|--------:|
;;    | 2024-01-31T05:00:00Z | 0.009280 | 1.08180 |
;;    | 2024-02-01T05:00:00Z | 0.009302 | 1.08715 |
;;    | 2024-02-02T05:00:00Z | 0.009545 | 1.07864 |
;;    | 2024-02-05T05:00:00Z | 0.009251 | 1.07423 |
;;    | 2024-02-06T05:00:00Z | 0.008721 | 1.07542 |
;;    | 2024-02-07T05:00:00Z | 0.008145 | 1.07720 |
;;    | 2024-02-08T05:00:00Z | 0.007691 | 1.07775 |
;;    | 2024-02-09T05:00:00Z | 0.007097 | 1.07830 |
;;    | 2024-02-12T05:00:00Z | 0.006666 | 1.07720 |
;;    | 2024-02-13T05:00:00Z | 0.006680 | 1.07085 |
;;    | 2024-02-14T05:00:00Z | 0.006147 | 1.07274 |
;;    | 2024-02-15T05:00:00Z | 0.005810 | 1.07724 |
;;    | 2024-02-16T05:00:00Z | 0.005193 | 1.07743 |
;;    | 2024-02-19T05:00:00Z | 0.004834 | 1.07769 |
;;    | 2024-02-20T05:00:00Z | 0.005208 | 1.08075 |
;;    | 2024-02-20T05:00:00Z | 0.005628 | 1.08075 |
;;    | 2024-02-21T05:00:00Z | 0.005499 | 1.08189 |
;;    | 2024-02-22T05:00:00Z | 0.006021 | 1.08226 |
;;    | 2024-02-22T05:00:00Z | 0.006380 | 1.08226 |
;;    | 2024-02-23T05:00:00Z | 0.005715 | 1.08175 |

(tc/select-columns @(:minute combined) [:date :close :doji-signal])
;; => :_unnamed [9620 3]:
;;    
;;    |                :date |  :close | :doji-signal |
;;    |----------------------|--------:|--------------|
;;    | 2024-02-15T05:22:00Z | 1.07278 |        :flat |
;;    | 2024-02-15T05:23:00Z | 1.07283 |        :flat |
;;    | 2024-02-15T05:24:00Z | 1.07282 |        :flat |
;;    | 2024-02-15T05:25:00Z | 1.07283 |        :flat |
;;    | 2024-02-15T05:26:00Z | 1.07298 |        :flat |
;;    | 2024-02-15T05:27:00Z | 1.07300 |        :flat |
;;    | 2024-02-15T05:28:00Z | 1.07300 |        :long |
;;    | 2024-02-15T05:29:00Z | 1.07308 |        :flat |
;;    | 2024-02-15T05:30:00Z | 1.07313 |        :flat |
;;    | 2024-02-15T05:31:00Z | 1.07318 |        :flat |
;;    |                  ... |     ... |          ... |
;;    | 2024-02-23T21:49:00Z | 1.08208 |        :flat |
;;    | 2024-02-23T21:50:00Z | 1.08211 |        :flat |
;;    | 2024-02-23T21:51:00Z | 1.08210 |       :short |
;;    | 2024-02-23T21:52:00Z | 1.08209 |        :flat |
;;    | 2024-02-23T21:53:00Z | 1.08219 |        :flat |
;;    | 2024-02-23T21:54:00Z | 1.08209 |       :short |
;;    | 2024-02-23T21:55:00Z | 1.08208 |        :flat |
;;    | 2024-02-23T21:56:00Z | 1.08195 |       :short |
;;    | 2024-02-23T21:57:00Z | 1.08196 |        :flat |
;;    | 2024-02-23T21:58:00Z | 1.08185 |       :short |
;;    | 2024-02-23T21:59:00Z | 1.08175 |        :flat |


(tc/select-columns @(:signal combined) [:date :close :daily-atr :doji-signal])
;; => :_unnamed [9620 4]:
;;    
;;    |                :date |  :close | :daily-atr | :doji-signal |
;;    |----------------------|--------:|-----------:|--------------|
;;    | 2024-02-15T05:22:00Z | 1.07278 |   0.000000 |        :flat |
;;    | 2024-02-15T05:23:00Z | 1.07283 |   0.000000 |        :flat |
;;    | 2024-02-15T05:24:00Z | 1.07282 |   0.000000 |        :flat |
;;    | 2024-02-15T05:25:00Z | 1.07283 |   0.000000 |        :flat |
;;    | 2024-02-15T05:26:00Z | 1.07298 |   0.000000 |        :flat |
;;    | 2024-02-15T05:27:00Z | 1.07300 |   0.000000 |        :flat |
;;    | 2024-02-15T05:28:00Z | 1.07300 |   0.000000 |        :long |
;;    | 2024-02-15T05:29:00Z | 1.07308 |   0.000000 |        :flat |
;;    | 2024-02-15T05:30:00Z | 1.07313 |   0.000000 |        :flat |
;;    | 2024-02-15T05:31:00Z | 1.07318 |   0.000000 |        :flat |
;;    |                  ... |     ... |        ... |          ... |
;;    | 2024-02-23T21:49:00Z | 1.08208 |   0.005715 |        :flat |
;;    | 2024-02-23T21:50:00Z | 1.08211 |   0.005715 |        :flat |
;;    | 2024-02-23T21:51:00Z | 1.08210 |   0.005715 |       :short |
;;    | 2024-02-23T21:52:00Z | 1.08209 |   0.005715 |        :flat |
;;    | 2024-02-23T21:53:00Z | 1.08219 |   0.005715 |        :flat |
;;    | 2024-02-23T21:54:00Z | 1.08209 |   0.005715 |       :short |
;;    | 2024-02-23T21:55:00Z | 1.08208 |   0.005715 |        :flat |
;;    | 2024-02-23T21:56:00Z | 1.08195 |   0.005715 |       :short |
;;    | 2024-02-23T21:57:00Z | 1.08196 |   0.005715 |        :flat |
;;    | 2024-02-23T21:58:00Z | 1.08185 |   0.005715 |       :short |
;;    | 2024-02-23T21:59:00Z | 1.08175 |   0.005715 |        :flat |

