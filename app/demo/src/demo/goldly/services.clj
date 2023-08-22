(ns demo.goldly.services
  (:require
   [goldly.service.core :as service]
   [ta.helper.ds :refer [ds->map show-meta cols-of-type]]
   [ta.helper.date]
   [ta.helper.date-ds :refer [ds-convert-col-instant->localdatetime]]
   [ta.warehouse :as wh]
   [ta.warehouse.symbollist]
   [ta.warehouse.overview]
   [ta.algo.manager]
   [ta.gann.db]
   [ta.gann.svg-plot]
   [ta.tradingview.handler-datasource]
   [demo.env.algos] ; side-effects
   ))

(defn overview-map [w f]
  (let [ds-overview (ta.warehouse.overview/warehouse-overview w f)
        m (-> ds-overview
              ds-convert-col-instant->localdatetime
              ds->map)]
    (println "overview-types: " (show-meta ds-overview))
    (println "overview type packet-instant" (cols-of-type ds-overview :packed-instant))
    (println "overview-map: " m)
    m))

(service/add
 { ; warehouse
  :ta/lists ta.warehouse.symbollist/get-lists
  :ta/symbols ta.warehouse.symbollist/load-list ; param: symbol-list-name
  :ta/warehouse-overview overview-map           ; param: wh-key frequency
  :ta/load-ts (partial wh/load-symbol :crypto)  ; needs symbol parameter

; algo
  :algo/names ta.algo.manager/algo-names
  :algo/info ta.algo.manager/algo-info
  :algo/run-window ta.algo.manager/algo-run-window-browser
  :algo/run ta.algo.manager/algo-run-browser
  :algo/marks ta.algo.manager/algo-marks
  :algo/shapes ta.algo.manager/algo-shapes

; tradingview api (via websocket)
  :tv/config (fn [] ta.tradingview.handler-datasource/server-config)
  :tv/symbol-info ta.tradingview.handler-datasource/symbol-info
  :tv/symbol-search ta.tradingview.handler-datasource/symbol-search
  :tv/time ta.tradingview.handler-datasource/server-time

; gann
  :gann/load ta.gann.db/load-gann
  :gann/save ta.gann.db/save-gann
  :gann/svg ta.gann.svg-plot/gann-svg-web ; gann-svg
  :gann/boxes ta.gann.svg-plot/get-boxes

; testing
  :date ta.helper.date/now-datetime})
