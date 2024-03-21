(ns ta.tradingview.handler.time
  (:require
   [tick.core :as t]
   [ring.util.response :as res]))

(defn server-time []
  (-> (t/instant) (t/long)))

(defn time-handler [_req]
  (let [now-epoch (server-time)]
    (res/response (str now-epoch))))
