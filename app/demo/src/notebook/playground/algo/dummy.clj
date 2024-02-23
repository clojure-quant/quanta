(ns notebook.playground.algo.dummy
  (:require
   [ta.algo.create :as a]))

(defn secret [env {:keys [data]} time]
  (str "the secret is: " data " (calculated: " time ")"))

(def algo
  (a/create-algo {:type :time
                  :data 42
                  :algo 'notebook.playground.algo.dummy/secret}))

algo

(algo nil {:data :v42} :now)