(ns ta.env.dsl.barstrategy
  (:require
    [taoensso.timbre :refer [trace debug info warn error]]
    [ta.algo.type.bar-strategy :refer [trailing-window-barstrategy]]
    [ta.algo.core :refer [get-algo-calc]]
    [ta.env.live-bargenerator :refer [add]]))

(defn trailing-window-algo [algo-opts]
  (let [algo-ns (:algo-ns algo-opts)
        algo-calc (get-algo-calc algo-ns)]
    {:algo trailing-window-barstrategy
     :algo-opts (assoc algo-opts :algo-calc algo-calc)}))


(defn add-bar-strategy [state algo-bar-strategy-wrapped]
  (add state (trailing-window-algo algo-bar-strategy-wrapped)))

(defn add-bar-strategies [state strategies]
  (info "add bar-strategies: " strategies)
  (let [add (partial add-bar-strategy state)]
    (doall
     (map add strategies))))


(comment 

  (require '[modular.system])
  (def live (modular.system/system :live))


  (trailing-window-algo-run live {:bar-category [:us :m]
                                 :asset "EUR/USD"
                                 :trailing-n 5} time)
  
  (get-algo-calc 'demo.algo.sma3)
  
  (require '[ta.calendar.core :refer [current-close]])
  (def dt (current-close :us :m))
  dt

  
  (require '[ta.env.live-bargenerator :refer [calc-algo]])
  
  

  (calc-algo live
             (trailing-window-algo {:algo-ns 'demo.algo.sma3
                                    :bar-category [:us :m]
                                    :asset "EUR/USD"
                                    :trailing-n 5
                                    :sma-length-st 2
                                    :sma-length-lt 3})
             dt)
  
  ;
)