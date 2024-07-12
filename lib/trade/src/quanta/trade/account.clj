(ns quanta.trade.account
  (:require
   [nano-id.core :refer [nano-id]]))




(defprotocol order
  (order-status [this]))

(defrecord orderi [order-id order-details]
  order
  (order-status [{:keys [order-id order-details]}]
    (assoc order-details :order-id order-id)))

(defn create-limit-order [{:keys [asset side quantity limit] :as order-details}]
  (assert (string? asset) "limit-order :asset has to be a string")
  (assert (keyword? side) "limit-order :side has to be a keyword")
  (assert (contains? #{:buy :sell} side) "limit-order :side has to be either :long or :short")
  (assert (double? limit) "limit-order :limit needs to be double")
  (assert (double? quantity) "limit-order :quantity needs to be double")
  (let [order-id (nano-id 6)]
    (orderi. order-id order-details)))



(defprotocol account
  ; current
  ;  - cash
  ;  - positions
  ;  - orders
  ; historic
  ; - trades
  ; - orders
  ; - positions
  (get-current-state [this]))

(defrecord accounti [id state]
  account
  (get-current-state [{:keys [id state]}]
    (assoc @state :account-id id)))

(defn create-account [account-id initial-cash]
  (let [state (atom {:cash initial-cash
                     :positions []
                     :orders []})]
    (accounti. account-id state)))

(comment
  (def demo-account (create-account :demo1 10000))
  (get-current-state demo-account)

  (create-limit-order {:asset "BTCUSDT" 
                       :side :buy
                       :quantity 0.1
                       :limit 30000.0
                       })
  
  (create-limit-order {:asset "BTCUSDT"
                       :side :buy3
                       :quantity 0.1
                       :limit 30000.0})

  


; 
  )

