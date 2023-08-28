(ns ta.algo.buy-hold
  (:require
   [tablecloth.api :as tc]
   [ta.algo.manager :refer [add-algo]]))

(defn buyhold-signal-bar-length [n]
  (concat [:buy]
          (repeat (- n 2) :hold)
          [:flat]))

(comment
  (buyhold-signal-bar-length 5)
 ; 
  )

(defn buy-hold-signal [ds _options]
  (tc/add-columns ds {:signal (-> ds tc/row-count buyhold-signal-bar-length)}))

(add-algo
 {:name "buy-hold"
  :comment "much better than b/h nasdaq"
  :algo buy-hold-signal
  :charts [{:trade "flags"}
           {:volume "column"}]
  :options {:w :stocks
            :symbol "SPY"
            :frequency "D"}})

