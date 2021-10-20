(ns demo.env.services
  (:require
   [goldly.service.core :as service]
   [tablecloth.api :as tc]
   [ta.warehouse :as wh]
   [ta.viz.table :refer [ds->table]]
   [ta.viz.study-highchart :refer [study-highchart]]
   [demo.env.config] ; side-effects
   [demo.env.algos :refer [algo-names run-algo chart-algo]]))

(defn- study-chart [w f symbol]
  (-> (wh/load-symbol w f symbol)
      (tc/select-rows (range 100))
      (study-highchart [{;:sma200 "line"
                         ;:sma30 "line"
                         }
                        {:open "line"}
                        {:volume "column"}])
      second))

;(defn table [symbol]
;  (-> (study symbol)
;      ds->table))

;(defn chart)
;(c/study-chart d [{:sma200 "line"
;                   :sma30 "line"}
;                  {:open "line"}
;                  {:volume "column"}])

(service/add
 {:ta/symbols wh/load-list ; param: symbol-list-name 
  :ta/load-ts (partial wh/load-symbol :crypto); needs symbol parameter
  :ta/highchart study-chart
  ;:ta/table table
  :ta/algos algo-names
  :ta/run-algo run-algo
  :ta/chart-algo chart-algo})


