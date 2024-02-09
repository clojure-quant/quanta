(ns ta.env.dsl.barstrategy
  (:require
    [taoensso.timbre :refer [trace debug info warn error]]
    [ta.env.live.trailing-window-algo :refer [trailing-window-algo]]
    [ta.env.live-bargenerator :refer [add]]))


(defn add-bar-strategy [state algo-bar-strategy-wrapped]
  (add state (trailing-window-algo algo-bar-strategy-wrapped)))

(defn add-bar-strategies [state strategies]
  (info "add bar-strategies: " strategies)
  (let [add (partial add-bar-strategy state)]
    (doall
     (map add strategies))))