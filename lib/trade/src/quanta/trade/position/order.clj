(ns quanta.trade.position.order)



(defn order [order])




(defn filled? [order]
  
  )

(defn enter-position [opts side]
  (let [qty (qty-entry opts)

        (create-order opts side qty)


(defn order->position [order])
{:side entry
 :asset asset
 :qty (positionsize size-rule close)
 :entry-idx idx
 :entry-date date
 :entry-price close}
        

        (defn position->order [opts side qty]
          (let [order (create-order opts side qty)
                (if (filled? 