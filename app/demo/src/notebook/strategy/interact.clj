(ns notebook.strategy.interact
  (:require
   [ta.interact.template :as template]))

(def crypto-watch
  {:id :crypto-watch
   :algo {:type :trailing-bar
          :trailing-n 300
          :calendar [:crypto :m]
          :asset "BTCUSDT"
          :dummy "just some text"
          :super-super-fast? true
          :feed :bybit
          :import :bybit
          :algo 'notebook.strategy.live.crypto/nil-algo}
   :viz 'notebook.strategy.live.crypto/calc-viz-highchart
   :options [{:path :asset
              :name "Asset"
              :spec ["BTCUSDT" "ETHUSDT"]}
             {:path :trailing-n
              :name "trailing-n"
              :spec [100 300 500 1000 2000 3000 5000 10000]}
             {:path :dummy
              :name "dummy-text"
              :spec :string}
             {:path :super-super-fast?
              :name "SuperSuperFast?"
              :spec :bool}
             ]})

(def sentiment-spread
  {:id :sentiment-spread
   :algo {:type :time
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
                    ]}
   :viz 'notebook.strategy.sentiment-spread.vega/calc-viz-vega
   :options [{:path :market
              :name "Market"
              :spec ["SPY" "QQQ" "IWM"]}]})


(def juan-fx
  {:id :juan-fx
   :algo  [:day {:type :trailing-bar
                 :algo   ['juan.algo.intraday/ensure-date-unique
                          'juan.algo.daily/daily]
                 :calendar [:us :d]
                 :asset "EUR/USD"
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
                    :asset "EUR/USD"
                    :import :kibot-http
                    :trailing-n 1440 ; 24 hour in minute bars
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
                    :algo 'juan.algo.combined/daily-intraday-combined}]
   :viz 'juan.notebook.viz/calc-viz-combined-highchart
   :key :signal
   :options [{:path [1 :asset]
              :name "asset"
              :spec ["EUR/USD" "USD/CHF" "GBP/USD" "USD/SEK"
                     "USD/NOK" "USD/CAD" "USD/JPY" "AUD/USD" "NZD/USD" ]}
             {:path [1 :atr-n]
              :name "ATR#"
              :spec [10 20 30]}
             {:path [1 :percentile]
              :name "Percentile"
              :spec [10 20 30 40 50 60 70 80 90]}
             {:path [1 :step]
              :name "Step"
              :spec [0.001 0.0001 0.00004]}
             {:path [3 :max-open-close-over-low-high]
              :name "co/lh max"
              :spec [0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9]}
              {:path [3 :pivot-max-diff]
              :name "pivot-max-diff"
              :spec [0.1 0.01 0.001 0.0001 0.00001]}
             {:path [3 :asset]
              :name "asset"
              :spec ["EUR/USD" "USD/CHF" "GBP/USD" "USD/SEK"
                     "USD/NOK" "USD/CAD" "USD/JPY" "AUD/USD" "NZD/USD"]}]})  

(def sma-crypto
  {:id :sma-eth
   :algo {:type :trailing-bar
          :algo 'notebook.strategy.sma-crossover.algo/sma-crossover-algo
          :calendar [:forex :m]
          :asset "ETHUSDT"
          :feed :bybit
          :import :bybit
          :trailing-n 1000
          :sma-length-st 20
          :sma-length-lt 200}
   :viz 'notebook.strategy.sma-crossover.viz/calc-viz-sma
   :options [{:path :asset
              :name "Asset"
              :spec ["BTCUSDT" "ETHUSDT"]}
             {:path :trailing-n
              :name "trailing-n"
              :spec [100 300 500 1000 2000 3000 5000 10000]}
             {:path :sma-length-st
              :name "sma-st"
              :spec [10 20 50 100]}
             {:path :sma-length-lt
              :name "sma-lt"
              :spec [100 200 500 1000]}]})

(def asset-compare
  {:id :asset-compare
   :algo {:type :time
          :algo 'notebook.strategy.asset-compare.algo/asset-compare-algo
          :calendar [:us :d]
          :assets ["GLD" "UUP" "SPY" "QQQ" "IWM" "EEM" "EFA" "IYR" "USO" "TLT"]
          :import :kibot
          :trailing-n 1000}
   :viz 'notebook.strategy.asset-compare.viz/calc-viz-vega})


(defn add-templates []
  (doall
   (map template/add [crypto-watch
                  sentiment-spread
                  juan-fx
                  sma-crypto
                  asset-compare])))


(comment
  (add-templates)

 ; 
  )

