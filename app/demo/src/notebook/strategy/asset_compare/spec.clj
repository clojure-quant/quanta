(ns notebook.strategy.asset-compare.spec
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [tablecloth.api :as tc]
   [ta.algo.env.core :as env]))

(def assets-full
  [{:asset "GLD" :name "Gold"}
   {:asset "UUP" :name "US Dollar"}
   {:asset "SPY" :name "S&P500"}
   {:asset "QQQ" :name "Nasdaq100"}
   {:asset "IWM" :name "Small Cap"}
   {:asset "EEM" :name "Emerging Markets"}
   {:asset "EFA" :name "International Equity"}
   {:asset "IYR" :name "Real Estate "}
   {:asset "USO" :name "Oil"}
   {:asset "TLT" :name "Treasurys"}])

(def assets (map :asset assets-full))

assets
;; => ("GLD" "UUP" "SPY" "QQQ" "IWM" "EEM" "EFA" "IYR" "USO" "TLT")


(def compare-spec
  {:type :time
   :algo 'notebook.strategy.asset-compare.algo/asset-compare-algo
   :calendar [:us :d]
   :assets assets
   :import :kibot
   :trailing-n 1000})

(comment
  (require '[ta.algo.backtest :refer [backtest-algo]])

  (def r (backtest-algo :bardb-dynamic compare-spec))
  
  @r

  (tc/info @r)
  ;; => :_unnamed: descriptive-stats [9 12]:
  ;;    
  ;;    | :col-name |       :datatype | :n-valid | :n-missing |                 :min |                    :mean | :mode |                 :max | :standard-deviation |      :skew |               :first |                :last |
  ;;    |-----------|-----------------|---------:|-----------:|----------------------|--------------------------|-------|----------------------|--------------------:|-----------:|----------------------|----------------------|
  ;;    |     :date | :packed-instant |    10000 |          0 | 2020-05-07T21:00:00Z | 2022-04-07T14:09:46.800Z |       | 2024-03-06T22:00:00Z |      3.49202921E+10 | 0.00000817 | 2020-05-07T21:00:00Z | 2024-03-06T22:00:00Z |
  ;;    |     :open |        :float64 |     9610 |        390 |                20.73 |                    152.0 |       |                512.0 |      1.23554967E+02 | 1.08414914 |                      |                      |
  ;;    |    :epoch |          :int64 |     9610 |        390 |                0.000 |                    0.000 |       |                0.000 |      0.00000000E+00 |            |                      |                      |
  ;;    |   :volume |        :float64 |     9610 |        390 |            1.480E+05 |                2.227E+07 |       |            1.800E+08 |      2.16798620E+07 | 1.60269109 |                      |                      |
  ;;    |     :high |        :float64 |     9610 |        390 |                21.31 |                    153.0 |       |                514.2 |      1.24366478E+02 | 1.08103620 |                      |                      |
  ;;    |      :low |        :float64 |     9610 |        390 |                20.30 |                    150.9 |       |                512.0 |      1.22693933E+02 | 1.08806392 |                      |                      |
  ;;    |    :ticks |          :int64 |     9610 |        390 |                0.000 |                    0.000 |       |                0.000 |      0.00000000E+00 |            |                      |                      |
  ;;    |    :asset |         :string |    10000 |          0 |                      |                          |   EEM |                      |                     |            |                  GLD |                  TLT |
  ;;    |    :close |        :float64 |    10000 |          0 |                0.000 |                    151.9 |       |                512.8 |      1.23772879E+02 | 1.08666538 |                0.000 |                94.10 |



  (require '[ta.viz.ds.vega :refer [convert-data]])

  (-> @r 
      (convert-data [:date :asset :close])
      
      )
  

  ;  
  )



