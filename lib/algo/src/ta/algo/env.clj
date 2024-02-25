(ns ta.algo.env
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [modular.system]
   [ta.engine.javelin :refer [create-engine-javelin]]
   [ta.engine.ops :as ops]
   [ta.algo.spec :as spec]))

(defprotocol algo-env
  (get-bar-db [this])
  (get-engine [this])
  ; algo
  (add-algo [this spec])
  (remove-algo [this spec]))


(defrecord env [bar-db engine]
  algo-env
  ; cell
  (get-bar-db [this]
    (:bar-db this))
  (get-engine [this]
    (:engine this))
  (add-algo [this spec]
    (let [e (get-engine this)
          ops (spec/spec->ops this spec)] 
      (info "adding engine ops: " ops)
      (ops/add-ops e ops)))
  (remove-algo [this spec]
    nil))

(defn create-env [bar-db engine]
  (env. bar-db engine))

(defn create-env-javelin [bar-db]
  (create-env bar-db (create-engine-javelin)))

