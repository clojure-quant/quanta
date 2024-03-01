(ns ta.algo.spec.type.formula
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]))

(defn- preprocess-fun [fun]
  (if (symbol? fun)
    (requiring-resolve fun)
    fun))

(defn create-formula-algo [{:keys [algo] :as spec}]
  (assert algo)
  (let [algo-fn (preprocess-fun algo)]
    (fn [env spec & args]
      (try
        (apply algo-fn env spec args)
        (catch Exception ex
          (error "exception calculating formula-strategy spec: " spec)
          nil)))))
