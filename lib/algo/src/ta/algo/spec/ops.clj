(ns ta.algo.spec.ops
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [de.otto.nom.core :as nom]
   [ta.algo.spec.type :refer [create-algo]]
   [ta.algo.spec.type.formula :refer [create-formula-algo]]))

(defn make-run-fn [env spec fun]
  (if (nom/anomaly? fun)
    fun
    (fn [& args]
      (try
        (warn "running fn args: " args)
        (if fun
          (apply fun env spec args)
          (nom/fail ::algo-calc {:message "algo fn is nil (compile error)"
                                 :location :algo-fn-nil
                                 :spec spec}))
        (catch Exception ex
          (error "algo exception: " ex)
          (nom/fail ::algo-calc {:message "algo fn ex!"
                                 :location :algo-fn-ex
                                 :spec spec}))))))

(defn spec->op [env spec]
  (let [{:keys [calendar formula value type]} spec]
    (cond
      ; time-algo
      (and calendar type)
      (let [fun (make-run-fn env spec (create-algo spec))
            r {:calendar calendar
               :time-fn fun}]
        (if (nom/anomaly? fun) fun r))
      ; formula (uses other cells)
      formula
      (let [fun (make-run-fn env spec (create-formula-algo spec))
            r {:formula formula :formula-fn fun}]
        (if (nom/anomaly? fun) fun r))
      ; value (create imput cell)
      value
      {:value value}
      ; bad spec syntax
      :else
      (nom/fail ::algo- {:message "bad spec format (either: calendar-type, formula, value)"
                         :location :spec->op-error
                         :spec spec}))))

(defn spec->ops
  "returns ops or nom/anomaly"
  [env spec]
  (if (map? spec)
    ; convert map syntax to vector syntax
    (spec->ops env [1 spec]) ; [[1 (spec->op env spec)]]
    ; process vector syntax
    (let [global-opts? (and (odd? (count spec))
                            (map? (first spec)))
          [global-opts spec] (if global-opts?
                               [(first spec) (rest spec)]
                               [{} spec])]
      (info "global-opts: " global-opts)
      (->> (reduce (fn [r [id spec]]
                     (let [spec (merge global-opts spec)
                           op (spec->op env spec)]
                       (info "merged spec: " spec)
                       (cond
                         (nom/anomaly? r) r
                         (nom/anomaly? op) op
                         :else (conj r [id op]))))
                   []
                   (partition 2 spec))
           ;(into [])
           ))))

