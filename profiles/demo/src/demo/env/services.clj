(ns demo.env.services
  (:require
   [goldly.service.core :as service]
   [ta.warehouse :as wh]
   [ta.viz.table :refer [ds->table]]
   ;[ta.backtest.chart :as c]
   [demo.study.sma :refer [sma-study]]
   [demo.env.config] ; side-effects
   ))

(defn- study [symbol]
  (-> (wh/load-ts :crypto symbol)
      sma-study))

(defn table [symbol]
  (-> (study symbol)
      ds->table))

;(defn chart)
;(c/study-chart d [{:sma200 "line"
;                   :sma30 "line"}
;                  {:open "line"}
;                  {:volume "column"}])

;sma-charts

(service/add
 {:ta/symbols  wh/load-list
  :ta/load-ts (partial wh/load-symbol :crypto); needs symbol parameter
  :ta/table table})


