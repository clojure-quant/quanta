(ns notebook.strategy.interact
  (:require
   [ta.interact.template :as template]
   [ta.db.asset.db :as db]
   [juan.asset-pairs :as juan-assets]))

(defn all-cryptos []
  (->> (db/symbols-available :crypto)
       (into [])))

(def watch-crypto
  {:id :watch-crypto
   :algo {:type :trailing-bar
          :trailing-n 300
          :calendar [:crypto :m]
          :asset "BTCUSDT"
          :import :bybit
          :feed :bybit
          :algo 'notebook.strategy.live.crypto/nil-algo
          ; irrelevant parameter; just ui demo.
          :dummy "just some text"
          :super-super-fast? true}
   :viz 'notebook.strategy.live.crypto/calc-viz-highchart
   :options (fn []
              [{:path :asset
              :name "Asset"
              :spec (all-cryptos) ; ["BTCUSDT" "ETHUSDT"]
                
                }
             {:path :trailing-n
              :name "trailing-n"
              :spec [100 300 500 1000 2000 3000 5000 10000]}
             {:path :dummy
              :name "dummy-text"
              :spec :string}
             {:path :super-super-fast?
              :name "SuperSuperFast?"
              :spec :bool}])})


(def sma-crypto
  {:id :sma-crypto
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
   :algo  [{:asset "USD/JPY"}
           :day {:type :trailing-bar
                 :algo   ['juan.algo.intraday/ensure-date-unique
                          'juan.algo.daily/daily]
                 :calendar [:forex :d]
                 :import :kibot
                 :feed :fx
                 ; daily opts
                 :trailing-n 120
                 ; atr-band
                 :atr-n 10
                 :atr-m 0.5
                 ; price pivots
                 :step 0.0001
                 :percentile 70}
           :minute {:calendar [:forex :m]
                    :algo  ['juan.algo.intraday/ensure-date-unique
                            'juan.algo.doji/doji-signal]
                    :type :trailing-bar
                    ;:import :kibot-http ; in live mode dont import
                    :trailing-n 1440 ; 24 hour in minute bars
                     ;  doji
                    :max-open-close-over-low-high 0.3
                    :volume-sma-n 30
            ; volume-pivots (currently not added)
            ;:step 10.0
            ;:percentile 70
                    }
           :signal {:formula [:day :minute]
                    :pivot-max-diff 0.001
                    :algo 'juan.algo.combined/daily-intraday-combined}]
   :viz 'juan.notebook.viz/calc-viz-combined-highchart
   :key :signal
   :options [{:path [0 :asset]
              :name "asset"
              :spec juan-assets/spot-fx-assets}
             {:path [2 :trailing-n]
              :name "daily#"
              :spec [2 5 10 20 30 50 80 100 120 150]}
             {:path [4 :trailing-n]
              :name "i#"
              :spec [720 1440 2880 5000 10000]}
             {:path [2 :atr-n]
              :name "dATRn"
              :spec [3 5 7 10 20 30 40 50]}
             {:path [2 :atr-m]
              :name "dATRm"
              :spec [0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0 1.1 1.2 1.3]}
             {:path [2 :percentile]
              :name "dPercentile"
              :spec [10 20 30 40 50 60 70 80 90]}
             {:path [2 :step]
              :name "dStep"
              :spec [0.001 0.0001 0.00004]}
             {:path [4 :max-open-close-over-low-high]
              :name "doji-co/lh max"
              :spec [0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9]}]})


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
   (map template/add [watch-crypto
                      sma-crypto
                      sentiment-spread
                      asset-compare
                      juan-fx])))


(comment
  (add-templates)

 ; 
  )

