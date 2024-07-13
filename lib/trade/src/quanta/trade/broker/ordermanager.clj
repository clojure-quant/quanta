(ns quanta.trade.broker.ordermanager
  [missionary.core :as m]
  [nano-id.core :refer [nano-id]]
  [tick.core :as t]
  ;[quanta.trade.broker.protocol :as B]
  ;[quanta.trade.broker.paper.orderfiller :refer [random-fill-flow]]
  )


(defn create-limit-order [this {:keys [asset side quantity limit
                                       order-id broker]
                                :as order-details}]
  (assert (string? asset) "limit-order :asset has to be a string")
  (assert (keyword? side) "limit-order :side has to be a keyword")
  (assert (contains? #{:buy :sell} side) "limit-order :side has to be either :long or :short")
  (assert (double? limit) "limit-order :limit needs to be double")
  (assert (double? quantity) "limit-order :quantity needs to be double")
  (let [order (if order-id
                order-details
                (assoc order-details :order-id (nano-id 6)))]
    (println "create limit order: " order) 
    (@(:! this) order)
    ))

(println "asdfasdf kjasdf haksdjf  34" 444)

(spit "bongo.txt" "asdf")


(defn start-ordermanager [broker]
  (let [!-a (atom nil)
        order-action-flow (m/observe (fn [!] 
                                       (! :init) 
                                       (reset! !-a !)
                                       #(println "ordermanager cancelled" :cancelled)))
        
        ]
    {:! !-a
     :order-action-flow order-action-flow}
    
    )
  
  )


(def om (start-ordermanager nil))

(create-limit-order om {:aset "BTC" :side :buy :limit 100.0 :qty 0.01})


(println "asdf")





