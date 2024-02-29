(ns notebook.playground.live.tickerplant
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [manifold.stream :as s]
   [modular.system]
   [ta.live.tickerplant :refer [start-tickerplant]]))

; alternative way to generate a live-bargenerator
;(def duckdb (modular.system/system :duckdb))
;(def feed (modular.system/system :feed))
;duckdb
;feed
;(def live (env/create-live-environment feed duckdb))

(def live (modular.system/system :live))
live

 [{:keys [algo-env feeds]}]