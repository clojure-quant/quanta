(ns demo.studies.dataset
  (:require
   [net.cgrand.xforms :as x]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.functional :as dfn]
   ;[ta.data.date :refer [parse-date]]
   [ta.warehouse :as wh]
   [ta.series.indicator :as ind]
   [ta.backtest.core :as bt]
   [ta.backtest.chart :as c]
   [demo.config :refer [w]]
   [ta.data.date :refer  [->epoch]]
   [demo.studies.helper.sma :refer [sma-study]]))

(def d (->
        (wh/load-ts w "MSFT")
        sma-study))
d

(def d-epoch (tds/column-map d :epoch #(->epoch %) [:date]))

(c/series-ohlc d-epoch)
(c/series d-epoch :close)

(c/study-chart d [{:sma200 "line"
                   :sma30 "line"}
                  {:open "line"}
                  {:volume "column"}])

;(def r (bt/calc-xf pre-process identity "XOM"))

;(r (parse-date "2021-02-03"))
;(r nil)

(into [] identity (range 5))

(into [] x/last (range 5))