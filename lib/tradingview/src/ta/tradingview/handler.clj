(ns ta.tradingview.handler
  (:require
   [modular.webserver.middleware.api :refer [wrap-api-handler]]
   [ring.middleware.multipart-params :refer [wrap-multipart-params]]
   ; tradingview handlers..
   [ta.tradingview.handler.time :refer [time-handler]]
   [ta.tradingview.handler.config :refer [config-handler]]
   [ta.tradingview.handler.history :refer [history-handler]]
   [ta.tradingview.handler.marks :refer [marks-handler]]
   [ta.tradingview.handler.asset :refer [search-handler symbols-handler]]
   [ta.tradingview.handler.storage :refer [save-chart-handler load-chart-handler
                                           modify-chart-handler delete-chart-handler
                                           load-template-handler save-template-handler]]))

(def wrapped-time-handler (wrap-api-handler time-handler))
(def wrapped-config-handler (wrap-api-handler config-handler))
(def wrapped-symbols-handler (wrap-api-handler symbols-handler))
(def wrapped-search-handler (wrap-api-handler search-handler))
(def wrapped-history-handler (wrap-api-handler history-handler))
(def wrapped-marks-handler (wrap-api-handler marks-handler))

(def wrapped-save-chart-handler (wrap-multipart-params (wrap-api-handler save-chart-handler)))
(def wrapped-modify-chart-handler (wrap-multipart-params (wrap-api-handler modify-chart-handler)))
(def wrapped-delete-chart-handler (wrap-api-handler delete-chart-handler))
(def wrapped-load-chart-handler (wrap-api-handler load-chart-handler))

(def wrapped-load-template-handler (wrap-api-handler load-template-handler))
(def wrapped-save-template-handler (wrap-multipart-params (wrap-api-handler save-template-handler)))

