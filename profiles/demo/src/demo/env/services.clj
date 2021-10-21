(ns demo.env.services
  (:require
   [goldly.service.core :as service]
   [ta.helper.ds :refer [ds->map show-meta cols-of-type]]
   [ta.backtest.date :refer [ds-convert-col-instant->localdatetime]]
   [ta.warehouse :as wh]
   [ta.warehouse.overview :refer [warehouse-overview]]
   [demo.env.config] ; side-effects
   [demo.env.algos :refer [algo-names algo-metrics algo-table algo-chart]]))

(defn overview-map [w f]
  (let [ds-overview (warehouse-overview w f)
        m (-> ds-overview
              ds-convert-col-instant->localdatetime
              ds->map)]
    (println "overview-types: " (show-meta ds-overview))
    (println "overview type packet-instant" (cols-of-type ds-overview :packed-instant))
    (println "overview-map: " m)
    m))

(service/add
 {; warehouse
  :ta/symbols wh/load-list ; param: symbol-list-name 
  :ta/warehouse-overview overview-map ; param: wh-key frequency
  :ta/load-ts (partial wh/load-symbol :crypto); needs symbol parameter

  ; algo
  :algo/names algo-names
  :algo/metrics algo-metrics  ; used in backtest
  :algo/table algo-table      ; used in study-table
  :algo/chart algo-chart      ; used in study-highchart
  })


