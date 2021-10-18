(ns ta.algo.buy-hold
  (:require
   [tablecloth.api :as tc]))

(defn buyhold-signal-bar-length [n]
  (concat [:buy]
          (repeat (- n 2) :hold)
          [:flat]))

(comment
  (buyhold-signal-bar-length 5)
 ; 
  )

(defn buy-hold-signal [ds _ #_options]
  (tc/add-columns ds {:signal (-> ds tc/row-count buyhold-signal-bar-length)}))
