(ns demo.env.services
  (:require
   [goldly.service.core :as service]
   [ta.warehouse :as wh]
   [ta.viz.table :refer [ds->table]]
   ;[ta.backtest.chart :as c]
   [demo.studies.helper.sma :as study]
   [demo.env.config :refer [w-crypto]]))

(defn- study [symbol]
  (-> (wh/load-ts w-crypto symbol)
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
 {:ta/symbols (partial wh/load-list w-crypto)
  :ta/load-ts (partial wh/load-ts w-crypto); needs symbol parameter
  :ta/table table})

