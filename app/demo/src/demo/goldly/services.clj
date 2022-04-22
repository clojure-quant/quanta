(ns demo.goldly.services
  (:require
   [goldly.service.core :as service]
   [ta.helper.ds :refer [ds->map show-meta cols-of-type]]
   [ta.helper.date :refer [now-datetime]]
   [ta.helper.date-ds :refer [ds-convert-col-instant->localdatetime]]
   [ta.warehouse :as wh]
   [ta.warehouse.overview :refer [warehouse-overview]]
   [ta.algo.manager :refer [algo-names algo-info algo-run-window-browser algo-run-browser algo-marks algo-shapes]]
   [demo.env.config] ; side-effects
   [demo.env.algos] ; side-effects
   [ta.gann.svg-plot :refer [get-gann-spec get-boxes]]
   [ta.tradingview.handler-datasource :refer [server-config symbol-info symbol-search server-time]]
   
   ))

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
  :algo/info algo-info
  :algo/run-window algo-run-window-browser
  :algo/run algo-run-browser
  :algo/marks algo-marks
  :algo/shapes algo-shapes

  ; tradingview api (via websocket)
  :tv/config (fn [] server-config)
  :tv/symbol-info (fn [symbol]
                    (println "symbol-info for:" symbol)
                    (symbol-info symbol))
  :tv/symbol-search symbol-search
  :tv/time server-time

  ; gann
  :gann/chart get-gann-spec
  :gann/boxes get-boxes

  ; testing
  :date now-datetime})


