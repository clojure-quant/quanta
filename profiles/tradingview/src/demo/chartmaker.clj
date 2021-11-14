(ns demo.chartmaker
  (:require
   [modular.persist.edn] ; side-effects
   [modular.config :as config]
   [ta.tradingview.chart.maker :refer [make-chart]]
   [ta.tradingview.chart.template :refer [dt trendline pitchfork gann gann-vertical]]))

(config/set!
 :ta
 {:warehouse {:list "../resources/symbollist/"
              :series  {:crypto "../db/crypto/"
                        :stocks "../db/stocks/"
                        :random "../db/random/"
                        :shuffled  "../db/shuffled/"}}
  :tradingview {:charts-path "../resources/tradingview-charts/"
                :template-path "src/ta/tradingview/chart/template/"}})

(defn make-trendline []
  (let [s "MSFT"
        chart-name (str "auto-test-trendline " s)
        client-id 77
        user-id 77
        chart-id 100]
    (make-chart client-id user-id chart-id s chart-name
                [(trendline {:symbol "MSFT"
                             :ap 300.0
                             :bp 330.0
                             :at (dt "2021-08-04T00:00:00")
                             :bt (dt "2021-11-04T00:00:00")})])))

(defn make-gann []
  (let [s "MSFT"
        chart-name (str "auto-test-gann " s)
        client-id 77
        user-id 77
        chart-id 101]
    (make-chart client-id user-id chart-id s chart-name
                [(gann
                  {:symbol s
                   :ap 300.0 :at (dt "2021-08-04T00:00:00")
                   :bp 330.0 :bt (dt "2021-11-04T00:00:00")})])))

(defn make-pitchfork []
  (let [s "MSFT"
        chart-name (str "auto-test-pitchfork " s)
        client-id 77
        user-id 77
        chart-id 103]
    (make-chart client-id user-id chart-id s chart-name
                [(pitchfork
                  {:symbol s
                   :ap 300.0  :at (dt "2021-08-04T00:00:00")
                   :bp 330.0  :bt (dt "2021-11-04T00:00:00")
                   :cp 250.0  :ct (dt "2021-09-04T00:00:00")})])))

(defn make-gann-vert []
  (let [s "MSFT"
        chart-name (str "auto-test-gann-vert " s)
        client-id 77
        user-id 77
        chart-id 102]
    (make-chart client-id user-id chart-id s chart-name
                (let [a-t (dt "2021-08-04T00:00:00")
                      d-t 3196800]
                  (concat (gann-vertical s 250.0 200.0 20 a-t (+ a-t d-t))
                          (gann-vertical s 250.0 400.0 10 a-t (+ a-t (* 2 d-t))))))))

(defn make-demo-charts [& _]
  (make-trendline)
  (make-gann)
  (make-pitchfork)
  (make-gann-vert))

(comment

  (make-chart 77 77 123 "AMZN" "test - empty chart" [])

  (make-trendline)
  (make-gann)
  (make-gann-vert)
  (make-pitchfork)

;  
  )