(ns demo.goldly.services
  (:require
   [goldly.service.core :as service]
   [ta.warehouse :as wh]
   [ta.backtest.table :refer [ds->table]]
   [ta.backtest.chart :as c]
   [demo.studies.helper.sma :as study]
   [demo.config :refer [w]]
   ))

(defn- study [symbol]
  (-> (wh/load-ts w symbol)
      study/sma-study))

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
 {:ta/symbols (partial wh/load-list w)
  :ta/load-ts (partial wh/load-ts w); needs symbol parameter
  :ta/table table})

