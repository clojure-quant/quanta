(ns ta.algo.spec.type
  (:require
   [de.otto.nom.core :as nom]
   [ta.algo.spec.type.bar-strategy :as bs]
   [ta.algo.spec.type.time :as ts]
   [ta.algo.spec.type.formula :as f]))

(defmulti create-algo :type)

(defmethod create-algo :time [spec]
  (ts/create-time-algo spec))

(defmethod create-algo :trailing-bar [spec]
  (bs/create-trailing-barstrategy spec))

(defmethod create-algo :default [spec]
  (nom/fail ::create-algo {:message "cannot create algo - :type missing / invalid"
                           :location :time-fn-type
                           :spec spec}))

#_(defmethod create-algo :formula [spec]
    (f/create-formula-algo spec))