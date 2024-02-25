(ns ta.algo.spec
  (:require 
   [algo.env :as algo-env]
   [ta.algo.spec.type :refer [create-algo]]))

(defn get-asset [spec] 
  (:asset spec))

(defn get-calendar [spec]
  (:calendar spec))

(defn get-trailing-n [spec]
  (:trailing-n spec))
  
(defn get-feed [spec]
  (:feed spec))

(defn wrap-env [env spec fun]
  (partial fun env spec))

(defn spec->op [env spec]
  (let [{:keys [calendar formula value]} spec]
    (cond 
      calendar
      {:calendar calendar :time-fn (->> spec create-algo (wrap-env env spec))}
      formula
      {:formula formula :formula-fn (->> spec create-algo (wrap-env env spec))}
      value
      {:value 27})))


(defn spec->ops [env spec]
  (if (map? spec)
      [[1 (spec->op env spec)]]
      (->> (map (fn [[id spec]]
                  [id (spec->op env spec)])
                (partition 2 spec))
           (into []))))

(comment 
  (def e (algo-env/create-env-javelin nil))
  e
  
  (defn dummy [_env spec time] 
    {:time time :spec spec})
  
  (defn combine [_env spec & args]
    {:spec spec :args args}
    )
  
  (spec->ops e {:calendar [:us :d] :algo 'ta.algo.spec/dummy :type :time})
  (spec->ops e [:a {:calendar [:us :h] :algo 'ta.algo.spec/dummy :type :time}
                :b {:calendar [:us :m] :algo 'ta.algo.spec/dummy :type :time}
                :c {:formula [:a] :algo 'ta.algo.spec/combine :type :time}
                ])

  ;
  )
