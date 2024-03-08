(ns ta.algo.spec.ops
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [ta.algo.spec.type :refer [create-algo]]
   [ta.algo.spec.type.formula :refer [create-formula-algo]]))

(defn wrap-env [env spec fun]
  (partial fun env spec))

(defn spec->op [env spec]
  (let [{:keys [calendar formula value]} spec]
    (cond
      calendar
      {:calendar calendar :time-fn (->> spec create-algo (wrap-env env spec))}
      formula
      {:formula formula :formula-fn (->> spec create-formula-algo (wrap-env env spec))}
      value
      {:value value})))

(defn spec->ops [env spec]
  (if (map? spec)
    [[1 (spec->op env spec)]]
    (let [global-opts? (and (odd? (count spec)) 
                            (map? (first spec)))
          [global-opts spec] (if global-opts?
                                 [(first spec) (rest spec)]
                                 [{} spec])]
      (warn "global-opts: " global-opts)
    (->> (map (fn [[id spec]]
                (let [spec (merge global-opts spec)]
                  (warn "merged spec: " spec)
                [id (spec->op env spec)]))
              (partition 2 spec))
         (into [])))))

(comment
  (require '[algo.env :as algo-env])
  (def e (algo-env/create-env-javelin nil))
  e

  (defn dummy [_env spec time]
    {:time time :spec spec})

  (defn combine [_env spec & args]
    {:spec spec :args args})

  (spec->ops e {:calendar [:us :d] :algo 'ta.algo.spec/dummy :type :time})
  (spec->ops e [:a {:calendar [:us :h] :algo 'ta.algo.spec/dummy :type :time}
                :b {:calendar [:us :m] :algo 'ta.algo.spec/dummy :type :time}
                :c {:formula [:a] :algo 'ta.algo.spec/combine :type :time}])

  ;
  )
