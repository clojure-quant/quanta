(ns ta.env.trailing-barstrategy-algo
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [manifold.stream :as s]
   [ta.algo.core :as algo]
   [ta.warehouse.duckdb :as duck]
))


(defn create-trailing-algo-calculator [{:keys [get-series] :as env}
                                       {:keys [asset window algo-ns opts] :as algo}]
  (fn [time]
     (let [ctx {:subscribe (fn [asset])
                :get-series (fn [])}
           algo (algo/run-algo ctx algo-ns)
           on-bar (gen-on-bar-calculate window algo ctx)]
     ))  
    
    )
  
;; 2. get state of current algo state.

(def algo-state
  (atom {}))

(defn set-output [algo ds-algo]
  (swap! algo-state assoc (:id algo) ds-algo))

(defn- algo-calendar-category [algo]
  (let [{:keys [window]} algo
        {:keys [calendar interval]} window]
    [calendar interval]))



      on-bar-fn (partial calculate-algo env algo)]} bar-category (algo-calendar-category algo)
      
      
      
      ;(defn remove-bar-algo [])
