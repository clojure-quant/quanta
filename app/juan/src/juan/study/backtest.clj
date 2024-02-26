(ns juan.study.backtest
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.algo.backtest :refer [backtest-algo backtest-algo-date]]))

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
         :algo   ['juan.algo.intraday/ensure-date-unique
                  'juan.algo.daily/daily] 
         :calendar [:us :d]
         :asset "EURUSD"
         :import :kibot
         :feed :fx
         :trailing-n 80
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
            ; volume-pivots (currently not added)
            ;:step 10.0
            ;:percentile 70
            }
   :signal {:formula [:day :minute]
            :spike-atr-prct-min 0.5
            :pivot-max-diff 0.001
            :algo 'juan.algo.combined/daily-intraday-combined}])

;(def combined (backtest-algo :duckdb algo-spec))
; (def combined (backtest-algo :bardb-dynamic algo-spec))

(def combined (backtest-algo-date :duckdb algo-spec
  (t/zoned-date-time "2024-02-22T17:00-05:00[America/New_York]")))


(tc/select-columns @(:day combined) [:date :atr :close :ppivotnr])
;; => :_unnamed [80 4]:
;;    
;;    |                :date |     :atr |  :close | :ppivotnr |
;;    |----------------------|---------:|--------:|----------:|
;;    | 2023-11-08T05:00:00Z | 0.005680 | 1.07090 |         6 |
;;    | 2023-11-09T05:00:00Z | 0.005766 | 1.06673 |         6 |
;;    | 2023-11-10T05:00:00Z | 0.005565 | 1.06842 |         6 |
;;    | 2023-11-13T05:00:00Z | 0.005409 | 1.06986 |         6 |
;;    | 2023-11-14T05:00:00Z | 0.006787 | 1.08787 |         6 |
;;    | 2023-11-15T05:00:00Z | 0.006762 | 1.08464 |         6 |
;;    | 2023-11-16T05:00:00Z | 0.006851 | 1.08519 |         6 |
;;    | 2023-11-17T05:00:00Z | 0.007176 | 1.09088 |         6 |
;;    | 2023-11-20T05:00:00Z | 0.007152 | 1.09401 |         6 |
;;    | 2023-11-21T05:00:00Z | 0.007237 | 1.09108 |         6 |
;;    |                  ... |      ... |     ... |       ... |
;;    | 2024-02-13T05:00:00Z | 0.006680 | 1.07085 |         6 |
;;    | 2024-02-14T05:00:00Z | 0.006147 | 1.07274 |         6 |
;;    | 2024-02-15T05:00:00Z | 0.005810 | 1.07724 |         6 |
;;    | 2024-02-16T05:00:00Z | 0.005193 | 1.07743 |         6 |
;;    | 2024-02-19T05:00:00Z | 0.004834 | 1.07769 |         6 |
;;    | 2024-02-20T05:00:00Z | 0.005208 | 1.08075 |         6 |
;;    | 2024-02-20T05:00:00Z | 0.005628 | 1.08075 |         6 |
;;    | 2024-02-21T05:00:00Z | 0.005499 | 1.08189 |         6 |
;;    | 2024-02-22T05:00:00Z | 0.006021 | 1.08226 |         6 |
;;    | 2024-02-22T05:00:00Z | 0.006380 | 1.08226 |         6 |
;;    | 2024-02-23T05:00:00Z | 0.005715 | 1.08175 |         6 |

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


(tc/select-columns @(:signal combined) [:date :daily-close :daily-atr 
                                        :close :spike :doji-signal
                                        :long])
;; => :_unnamed [9620 7]:
;;    
;;    |                :date | :daily-close | :daily-atr |  :close | :spike | :doji-signal | :long |
;;    |----------------------|-------------:|-----------:|--------:|--------|--------------|-------|
;;    | 2024-02-15T05:22:00Z |      0.00000 |   0.000000 | 1.07278 | :short |        :flat |       |
;;    | 2024-02-15T05:23:00Z |      0.00000 |   0.000000 | 1.07283 | :short |        :flat |       |
;;    | 2024-02-15T05:24:00Z |      0.00000 |   0.000000 | 1.07282 | :short |        :flat |       |
;;    | 2024-02-15T05:25:00Z |      0.00000 |   0.000000 | 1.07283 | :short |        :flat |       |
;;    | 2024-02-15T05:26:00Z |      0.00000 |   0.000000 | 1.07298 | :short |        :flat |       |
;;    | 2024-02-15T05:27:00Z |      0.00000 |   0.000000 | 1.07300 | :short |        :flat |       |
;;    | 2024-02-15T05:28:00Z |      0.00000 |   0.000000 | 1.07300 | :short |        :long |       |
;;    | 2024-02-15T05:29:00Z |      0.00000 |   0.000000 | 1.07308 | :short |        :flat |       |
;;    | 2024-02-15T05:30:00Z |      0.00000 |   0.000000 | 1.07313 | :short |        :flat |       |
;;    | 2024-02-15T05:31:00Z |      0.00000 |   0.000000 | 1.07318 | :short |        :flat |       |
;;    |                  ... |          ... |        ... |     ... |    ... |          ... |   ... |
;;    | 2024-02-23T21:49:00Z |      1.08175 |   0.005715 | 1.08208 | :short |        :flat |       |
;;    | 2024-02-23T21:50:00Z |      1.08175 |   0.005715 | 1.08211 | :short |        :flat |       |
;;    | 2024-02-23T21:51:00Z |      1.08175 |   0.005715 | 1.08210 | :short |       :short |       |
;;    | 2024-02-23T21:52:00Z |      1.08175 |   0.005715 | 1.08209 | :short |        :flat |       |
;;    | 2024-02-23T21:53:00Z |      1.08175 |   0.005715 | 1.08219 | :short |        :flat |       |
;;    | 2024-02-23T21:54:00Z |      1.08175 |   0.005715 | 1.08209 | :short |       :short |       |
;;    | 2024-02-23T21:55:00Z |      1.08175 |   0.005715 | 1.08208 | :short |        :flat |       |
;;    | 2024-02-23T21:56:00Z |      1.08175 |   0.005715 | 1.08195 | :short |       :short |       |
;;    | 2024-02-23T21:57:00Z |      1.08175 |   0.005715 | 1.08196 | :short |        :flat |       |
;;    | 2024-02-23T21:58:00Z |      1.08175 |   0.005715 | 1.08185 | :short |       :short |       |
;;    | 2024-02-23T21:59:00Z |      1.08175 |   0.005715 | 1.08175 |  :flat |        :flat |       |


(require '[ta.helper.ds :refer [ds->str]])

(->> (tc/select-columns @(:signal combined) [:date :daily-close :daily-atr :daily-pivotnr
                                             :daily-date
                                            :close :spike :doji-signal
                                            :long :short])
    ds->str
    (spit "/tmp/juan.txt"))


(->> (tc/select-columns @(:day combined) [:date :atr :close :ppivotnr])
     ds->str
     (spit "/tmp/juan-daily.txt"))

(-> @(:day combined) tc/info
    (tc/select-columns [:col-name  :datatype :n-valid :n-missing]))
;; => :_unnamed: descriptive-stats [13 4]:
;;    
;;    |     :col-name |       :datatype | :n-valid | :n-missing |
;;    |---------------|-----------------|---------:|-----------:|
;;    |         :open |        :float64 |       79 |          0 |
;;    |        :epoch |          :int64 |       79 |          0 |
;;    |         :date | :packed-instant |       79 |          0 |
;;    |        :close |        :float64 |       79 |          0 |
;;    |       :volume |        :float64 |       79 |          0 |
;;    |         :high |        :float64 |       79 |          0 |
;;    |          :low |        :float64 |       79 |          0 |
;;    |        :ticks |          :int64 |       79 |          0 |
;;    |        :asset |         :string |       79 |          0 |
;;    |          :atr |        :float64 |       79 |          0 |
;;    |      :close-1 |        :float64 |       79 |          0 |
;;    | :pivots-price |        :dataset |       79 |          0 |
;;    |     :ppivotnr |          :int64 |       79 |          0 |


(-> @(:minute combined) tc/info
    (tc/select-columns [:col-name  :datatype :n-valid :n-missing]))
;; => :_unnamed: descriptive-stats [13 4]:
;;    
;;    |                 :col-name |       :datatype | :n-valid | :n-missing |
;;    |---------------------------|-----------------|---------:|-----------:|
;;    |                     :open |        :float64 |    10008 |          0 |
;;    |                    :epoch |          :int64 |    10008 |          0 |
;;    |                     :date | :packed-instant |    10008 |          0 |
;;    |                    :close |        :float64 |    10008 |          0 |
;;    |                   :volume |        :float64 |    10008 |          0 |
;;    |                     :high |        :float64 |    10008 |          0 |
;;    |                      :low |        :float64 |    10008 |          0 |
;;    |                    :ticks |          :int64 |    10008 |          0 |
;;    |                    :asset |         :string |    10008 |          0 |
;;    |               :open-close |        :float64 |    10008 |          0 |
;;    | :open-close-over-low-high |        :float64 |     9919 |         89 |
;;    |               :volume-sma |        :float64 |    10008 |          0 |
;;    |              :doji-signal |        :keyword |    10008 |          0 |


(-> @(:signal combined) tc/info
    (tc/select-columns [:col-name  :datatype :n-valid :n-missing]))
;; => :_unnamed: descriptive-stats [20 4]:
;;    
;;    |                 :col-name |       :datatype | :n-valid | :n-missing |
;;    |---------------------------|-----------------|---------:|-----------:|
;;    |                     :open |        :float64 |    10008 |          0 |
;;    |                    :epoch |          :int64 |    10008 |          0 |
;;    |                     :date | :packed-instant |    10008 |          0 |
;;    |                    :close |        :float64 |    10008 |          0 |
;;    |                   :volume |        :float64 |    10008 |          0 |
;;    |                     :high |        :float64 |    10008 |          0 |
;;    |                      :low |        :float64 |    10008 |          0 |
;;    |                    :ticks |          :int64 |    10008 |          0 |
;;    |                    :asset |         :string |    10008 |          0 |
;;    |               :open-close |        :float64 |    10008 |          0 |
;;    | :open-close-over-low-high |        :float64 |     9919 |         89 |
;;    |               :volume-sma |        :float64 |    10008 |          0 |
;;    |              :doji-signal |        :keyword |    10008 |          0 |
;;    |                :daily-atr |        :float64 |    10008 |          0 |
;;    |              :daily-close |        :float64 |     3178 |       6830 |
;;    |             :daily-pivots |        :dataset |     2990 |       7018 |
;;    |            :daily-pivotnr |          :int64 |    10008 |          0 |
;;    |                    :spike |        :keyword |    10008 |          0 |
;;    |                     :long |        :keyword |      606 |       9402 |
;;    |                    :short |        :keyword |      967 |       9041 |


;6 first
;0 2024-02-13T23:34:00Z
;6 2024-02-14T10:02:00Z
;0 2024-02-14T12:05:00Z
;6; 2024-02-14T21:10:00Z
;0; 2024-02-14T22:50:00Z 
;6 2024-02-15T08:20:00Z
;0 2024-02-15T09:31:00Z
;6 2024-02-16T06:36:00Z

;daily close:
;| 2024-02-22T21:41:00Z |    
;| 2024-02-21T05:00:00Z | 0.005201 | 1.08189 |         6 |
