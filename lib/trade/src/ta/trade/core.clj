(ns ta.trade.core
  (:require 
   [tablecloth.api :as tc]
   [ta.trade.signal :refer [trade-signal]]))


(defn signal-col? [ds]
  (:signal ds))

(defn backtest [bar-ds]
  (assert (signal-col? bar-ds))
  (let [signal-ds (trade-signal bar-ds)]
     
     ))

