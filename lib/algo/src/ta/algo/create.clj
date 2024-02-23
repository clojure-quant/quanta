(ns ta.algo.create
  (:require 
   [ta.algo.type.bar-strategy :refer [create-trailing-barstrategy]]
   [ta.algo.type.time :refer [create-time-algo]]))

(defmulti create-algo :type)

(defmethod create-algo :time [spec]
  (create-time-algo spec))

(defmethod create-algo :trailing-bar [spec]
  (create-trailing-barstrategy spec))