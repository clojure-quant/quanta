(ns quanta.notebook.algo-ops
  (:require
   [ta.algo.env :as algo-env]
   [ta.algo.spec.ops :as ops]
   [ta.algo.spec.type :refer [create-algo]]
   ))

(def e (algo-env/create-env-javelin nil))

e

;; 1. time-based algo spec

(defn secret [env spec time]
  (str "the spec is: " spec " (calculated: " time ")"))

(def spec {:type :time
           :calendar [:us :d]
           :data 42
           :algo 'quanta.notebook.algo-ops/secret})

(ops/spec->ops e spec)

; 2. compile error 

(def spec-bad {:type :time
               :calendar [:us :d]
               :data 42
               :algo 'quanta.notebook.algo-ops/secret-bad})

(ops/spec->ops e spec-bad)

(defn dummy [_env spec time]
  {:time time :spec spec})

(defn combine [_env spec & args]
  {:spec spec :args args})

(ops/spec->ops e {:calendar [:us :d] :algo 'quanta.notebook.algo-ops/dummy :type :time})
(ops/spec->ops e [:a {:calendar [:us :h] :algo 'quanta.notebook.algo-ops/dummy :type :time}
                  :b {:calendar [:us :m] :algo 'quanta.notebook.algo-ops/dummy :type :time}
                  :c {:formula [:a] :algo 'quanta.notebook.algo-ops/combine :type :time}])

(ops/spec->ops
 e
 [:a {:calendar [:us :h] :algo 'quanta.notebook.algo-ops/dummy-bad :type :time}
  :b {:calendar [:us :m] :algo 'quanta.notebook.unknown-bad/dummy :type :time}
  :c {:formula [:a] :algo 'quanta.notebook.algo-ops/combine :type :time}])


(ops/spec->op 
 e 
 {:calendar [:us :d] 
  :algo 'quanta.notebook.algo-ops3/dummy 
  :type :time})


(create-algo
 {:calendar [:us :d]
  :algo 'quanta.notebook.algo-ops3/dummy
  :type :time})

(create-algo
 {:calendar [:us :d]
  :algo 'quanta.notebook.algo-ops/dummy-unkown-15
  :type :time})
