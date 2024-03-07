(ns notebook.strategy.interact
  (:require
   [ta.interact.template :as template]))

(def crypto-watch
  {:id :crypto-watch
   :algo {:type :trailing-bar
          :trailing-n 300
          :calendar [:crypto :m]
          :asset "BTCUSDT"
          :feed :bybit
          :import :bybit
          :algo 'notebook.strategy.live.crypto/nil-algo}
   :viz 'notebook.strategy.live.crypto/calc-viz-highchart
   :options [{:path :asset
              :name "Asset"
              :spec ["BTCUSDT" "ETHUSDT"]}
             {:path :trailing-n
              :name "trailing-n"
              :spec [100 300 500 1000 2000 3000 5000 10000]}]})


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
   :viz 'notebook.strategy.sentiment-spread.vega/calc-viz-vega})


(def juan
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
   :key :signal})

(def sma-eth
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
   :viz 'notebook.strategy.sma-crossover.viz/calc-viz-sma})

(def asset-compare
  {:id :asset-compare
   :algo {:type :time
          :algo 'notebook.strategy.asset-compare.algo/asset-compare-algo
          :calendar [:us :d]
          :assets ["GLD" "UUP" "SPY" "QQQ" "IWM" "EEM" "EFA" "IYR" "USO" "TLT"]
          :import :kibot
          :trailing-n 1000}
   :viz 'notebook.strategy.asset-compare.viz/calc-viz-vega})


(defn add-strategies []
  (doall
   (map template/add [crypto-watch
                  sentiment-spread
                  juan
                  sma-eth
                  asset-compare])))

(comment
  (add-strategies)

 ; 
  )

