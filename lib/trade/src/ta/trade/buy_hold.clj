(ns ta.trade.buy-hold
  (:require
   [tablecloth.api :as tc]))

(defn buyhold-signal-bar-length [n]
  (concat [:buy]
          (repeat (- n 2) :hold)
          [:flat]))

(defn buy-hold-algo [_env _opts bar-ds]
  (tc/add-columns bar-ds {:signal (-> bar-ds tc/row-count buyhold-signal-bar-length)}))

(comment
  
  (buyhold-signal-bar-length 5)
 ; 
  )


