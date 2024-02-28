(ns juan.study.backtest
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.algo.backtest :refer [backtest-algo backtest-algo-date]]
   [ta.viz.publish :as p]))

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

(def table-spec
  {:topic :juan-daily-table
   :class "table-head-fixed padding-sm table-red table-striped table-hover"
   :style {:width "50vw"
           :height "40vh"
           :border "3px solid green"}
   :columns [{:path :date}
             {:path :atr}
             {:path :close}
             {:path :ppivotnr}]})


 (p/publish-table nil table-spec @(:day combined))

(def chart-spec
  {:topic :juan-daily-chart
   :charts [{:close "line"}
            {:atr "line"}
            {:volume "line"}]})

(p/publish-ds->highstock nil chart-spec @(:day combined))



(tc/select-columns @(:minute combined) 
                   [:date :close :doji])
;; => :_unnamed [9620 3]:
;;    
;;    |                :date |  :close | :doji        |
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
                                        :close :spike :doji :spike-doji
                                        :long :short])
;; => :_unnamed [10008 9]:
;;    
;;    |                :date | :daily-close | :daily-atr |  :close | :spike |  :doji | :spike-doji |    :long |   :short |
;;    |----------------------|-------------:|-----------:|--------:|--------|--------|-------------|----------|----------|
;;    | 2024-02-13T22:51:00Z |      1.07172 |   0.012298 | 1.07101 |  :long |  :flat |       :flat |          |  :p0-low |
;;    | 2024-02-13T22:52:00Z |      1.06997 |   0.011557 | 1.07101 | :short |  :flat |       :flat |          |  :p1-low |
;;    | 2024-02-13T22:55:00Z |      1.07090 |   0.010803 | 1.07100 | :short |  :flat |       :flat |          | :p0-high |
;;    | 2024-02-13T22:56:00Z |      1.06673 |   0.010135 | 1.07101 | :short |  :flat |       :flat |          | :p1-high |
;;    | 2024-02-13T22:57:00Z |      1.06842 |   0.009180 | 1.07103 | :short |  :flat |       :flat |          |          |
;;    | 2024-02-13T22:58:00Z |      1.06986 |   0.008270 | 1.07107 | :short |  :flat |       :flat | :p0-high |          |
;;    | 2024-02-13T22:59:00Z |      1.08787 |   0.008894 | 1.07106 |  :long |  :flat |       :flat | :p1-high |          |
;;    | 2024-02-13T23:00:00Z |      1.08464 |   0.008115 | 1.07081 |  :long | :short |       :flat |          |          |
;;    | 2024-02-13T23:01:00Z |      1.08519 |   0.007450 | 1.07085 |  :long |  :flat |       :flat |          |          |
;;    | 2024-02-13T23:02:00Z |      1.09088 |   0.007021 | 1.07088 |  :long |  :flat |       :flat |          |          |
;;    |                  ... |          ... |        ... |     ... |    ... |    ... |         ... |      ... |      ... |
;;    | 2024-02-22T21:49:00Z |      1.08189 |   0.005201 | 1.08230 | :short |  :flat |       :flat |          | :p0-high |
;;    | 2024-02-22T21:50:00Z |      1.08189 |   0.005201 | 1.08229 | :short | :short |      :short |          | :p0-high |
;;    | 2024-02-22T21:51:00Z |      1.08189 |   0.005201 | 1.08229 | :short |  :flat |       :flat |          | :p0-high |
;;    | 2024-02-22T21:52:00Z |      1.08189 |   0.005201 | 1.08231 | :short |  :long |       :flat |          | :p0-high |
;;    | 2024-02-22T21:53:00Z |      1.08189 |   0.005201 | 1.08234 | :short |  :flat |       :flat |          | :p0-high |
;;    | 2024-02-22T21:54:00Z |      1.08189 |   0.005201 | 1.08234 | :short |  :flat |       :flat |          | :p0-high |
;;    | 2024-02-22T21:55:00Z |      1.08189 |   0.005201 | 1.08229 | :short | :short |      :short |          | :p0-high |
;;    | 2024-02-22T21:56:00Z |      1.08189 |   0.005201 | 1.08222 | :short | :short |      :short |          | :p0-high |
;;    | 2024-02-22T21:57:00Z |      1.08189 |   0.005201 | 1.08220 | :short | :short |      :short |          | :p0-high |
;;    | 2024-02-22T21:58:00Z |      1.08189 |   0.005201 | 1.08229 | :short |  :flat |       :flat |          | :p0-high |
;;    | 2024-02-22T21:59:00Z |      1.08189 |   0.005201 | 1.08226 | :short | :short |      :short |          | :p0-high |


(def combined-table-spec
  {:topic :juan-combined-table
   :class "table-head-fixed padding-sm table-red table-striped table-hover"
   :style {:width "50vw"
           :height "40vh"
           :border "3px solid green"}
   :columns [{:path :date}
             {:path :daily-close}
             {:path :daily-atr}
             {:path :close}
             {:path :spike}
             {:path :doji}
             {:path :spike-doji}
             {:path :long}
             {:path :short}
             ]})

(p/publish-table nil combined-table-spec @(:signal combined))

(def combined-chart-spec
  {:topic :juan-combined-chart
   :charts [{:daily-close "line"
             :close "line"}
            {:atr "line"}
            {:volume "line"}]})

(p/publish-ds->highstock nil combined-chart-spec @(:signal combined))



(require '[ta.helper.ds :refer [ds->str]])

(->> (tc/select-columns @(:signal combined) [:date :daily-close :daily-atr ;:daily-pivotnr
                                             ;:daily-date
                                            :close :spike :doji :spike-doji
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
;;    |               :doji       |        :keyword |    10008 |          0 |


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
;;    |              :doji        |        :keyword |    10008 |          0 |
;;    |                :daily-atr |        :float64 |    10008 |          0 |
;;    |              :daily-close |        :float64 |    10008 |          0 |
;;    |             :daily-pivots |        :dataset |    10008 |          0 |
;;    |            :daily-pivotnr |          :int64 |    10008 |          0 |
;;    |                    :spike |        :keyword |    10008 |          0 |
;;    |                     :long |        :keyword |     2495 |       7513 |
;;    |                    :short |        :keyword |     3466 |       6542 |
