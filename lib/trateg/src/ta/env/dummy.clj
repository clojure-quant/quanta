(ns ta.env.trailing-barstrategy-algo
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [manifold.stream :as s]
   [ta.algo.core :as algo]
   [ta.warehouse.duckdb :as duck]
))

;; 2. get state of current algo state.

(def algo-state
  (atom {}))

(defn set-output [algo ds-algo]
  (swap! algo-state assoc (:id algo) ds-algo))

