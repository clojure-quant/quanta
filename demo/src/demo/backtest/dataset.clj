(ns demo.dataset
  (:require
   [net.cgrand.xforms :as x]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.functional :as dfn]
   ;[ta.data.date :refer [parse-date]]
   [ta.warehouse :as wh]
   [ta.series.indicator :as ind]
   [ta.backtest.core :as bt]
   [ta.backtest.chart :as c]
   [demo.config]
   [ta.data.date :refer [->epoch]]
   ))

:gorilla/on





(def d (wh/load-ts "MSFT"))
d

(def d-epoch (tds/column-map d :epoch #(->epoch %) [:date]))

(c/series-ohlc d-epoch)
(c/series d-epoch :close)

(c/study-chart d [{:close "line"}
                  {:open "line"}
                  {:volume "column"}
                  ])


(defn pre-process [d]
  (let [sma30 (ind/sma 30 (d :close))
        sma200 (ind/sma 200 (d :close))]
    (-> d
        (assoc :sma30 sma30)
        (assoc :rsma30 (dfn// (d :close) sma30))
        (assoc :sma200 sma30)
        (assoc :rsma200 (dfn// (d :close) sma200)))))


(def r (bt/calc-xf pre-process identity "XOM"))

(r (parse-date "2021-02-03"))
(r nil)

(into [] identity (range 5))

(into [] x/last (range 5))