(ns demo.goldly.services
  (:require
   [goldly.service.core :as service]
   [ta.warehouse :as wh]
   [ta.backtest.table :refer [table-spec]]
   [ta.backtest.chart :as c]
   [demo.studies.sma :as study]))


(wh/init-tswh {:series "./db/"
               :list "./resources/etf/"})
#_(defn symbols []
  ["FSDAX" "FDCPX" "FMCCX"])

(defn symbols []
  (wh/load-list  "fidelity-select"))

(defn- study [symbol]
  (-> (wh/load-ts symbol)
      study/sma-study))

(defn table [symbol]
  (-> (study symbol)
      table-spec))

;(defn chart)
;(c/study-chart d [{:sma200 "line"
;                   :sma30 "line"}
;                  {:open "line"}
;                  {:volume "column"}])

;sma-charts

(service/add
 {:ta/symbols symbols
  :ta/load-ts wh/load-ts ; needs symbol parameter
  :ta/table-spec table})

