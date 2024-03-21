(ns ta.algo.spec.type.time
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [de.otto.nom.core :as nom]
   [ta.algo.spec.parser.chain :as chain]))

(defn create-time-algo [{:keys [algo] :as spec}]
  (assert algo)
  (let [algo-fn (chain/make-chain algo)]
    (fn [env spec time]
      (try
        (algo-fn env spec time)
        (catch Exception ex
          (error "exception calculating time-strategy. " spec)
          (error ex)
          (nom/fail ::algo-calc {:message "algo calc exception!"
                                 :location :time
                                 :spec spec
                                 :time time}))))))



