(ns ta.algo.spec.type
  (:require
   [ta.algo.spec.type.bar-strategy :as bs]
   [ta.algo.spec.type.time :as ts]))

(defmulti create-algo :type)

(defmethod create-algo :time [spec]
  (ts/create-time-algo spec))

(defmethod create-algo :trailing-bar [spec]
  (bs/create-trailing-barstrategy spec))