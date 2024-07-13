(ns quanta.trade.position
  (:require
   [missionary.core :as m]
   [ta.calendar.core :refer [calendar-seq]]
   [quanta.trade.position.size :refer [positionsize]]
   ;[quanta.trade.position.order :refer [Order]]
   [quanta.trade.supervisor :refer [error]]
   [quanta.trade.position.exit.time :refer [get-exit-time time-trigger]]))

(defn EnterPosition [algo-opts side]
  (let [qty (positionsize algo-opts side)
        order-opts (assoc algo-opts
                          :side signal :qty qty)
        order (m/? (Order order-opts))] ; order is a task which produces an effect
    (if (filled? order)
      (order->position order)
      (error order)))

  (defn ExitSignal
    "returns a missionary task.
   task will eventually return either of :time :profit :loss"
    [algo-opts position]
    (let [exit-time (get-exit-time algo-opts (:entry-date position))
          exit-tasks (->> [(when exit-time
                             (time-trigger exit-time))]
                          (remove nil?))]
      (apply m/race exit-tasks)))

  (defn ExitPosition [algo-opts position]
    ;(M/ap (m/?> 
    )
  (comment
    (require '[tick.core :as t])
    (def algo-opts {:calendar [:crypto :m]
                    :exit [:time 2]})
    (def position {:entry-date (t/instant)})

    (m/? (ExitSignal algo-opts position))
    ;; => :time

    ;; this one has a position that is older and older, so
    ;; it might be that this task returns immediately, because
    ;; the current time is already below the time of the exit-date 

    (m/? (ExitSignal algo-opts {:entry-date (t/instant)}))
    ;; => :time



    
    ; 
    )
