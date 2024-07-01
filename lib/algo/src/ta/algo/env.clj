(ns ta.algo.env
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [de.otto.nom.core :as nom]
   [ta.engine.javelin :refer [create-engine-javelin]]
   [ta.engine.ops :as ops]
   [ta.algo.spec.ops :as spec-ops]
   [ta.algo.env.protocol :as algo-env]))

(defrecord env [bar-db engine watcher]
  algo-env/algo-env
  ; cell
  (get-bar-db [this]
    (:bar-db this))
  (get-engine [this]
    (:engine this))
  (set-watcher [this w]
    (warn "setting watcher!")
    (reset! (:watcher this) w))
  (add-algo [this spec]
    (let [e (algo-env/get-engine this)
          ops (spec-ops/spec->ops this spec)]
      (if (nom/anomaly? ops)
        ops
        (do
          (if-let [w @(:watcher this)]
            (w spec)
            (warn "no watcher set - cannot trigger it!!"))
          (info "adding engine ops: " ops)
          (ops/add-ops e ops)))))

  (remove-algo [this spec]
    nil))

(defn create-env [bar-db engine]
  (env. bar-db engine (atom nil)))

(defn create-env-javelin [bar-db]
  (create-env bar-db (create-engine-javelin)))

