(ns ta.algo.type.time
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [ta.algo.chain :refer [make-chain]]))

(defn create-time-algo [{:keys [algo] :as spec}]
  (assert algo)
  (let [algo-fn (make-chain algo)]
    (fn [env spec time]
      (try
        (algo-fn env spec time)
        (catch Exception ex
          (warn "exception calculating time-strategy.")
          nil)))))

 