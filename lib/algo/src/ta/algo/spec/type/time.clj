(ns ta.algo.spec.type.time
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [de.otto.nom.core :as nom]
   [ta.algo.error-report :refer [save-error-report]]
   [ta.algo.spec.parser.chain :as chain]))

(defn create-time-algo [{:keys [algo] :as spec}]
  (info "create time algo: " spec)
  (cond

    (nil? spec)
    (nom/fail ::create-formula-algo {:message "cannot create time-algo - spec is nil"
                                     :location :formula})
    (not (map? spec))
    (nom/fail ::create-formula-algo {:message "cannot create time-algo - spec is not a map"
                                     :location :formula
                                     :spec spec})
    (not algo)
    (nom/fail ::create-formula-algo {:message "cannot create time-algo -  spec/algo is nil"
                                     :location :formula
                                     :spec spec})

    :else
    (let [algo-fn (chain/make-chain algo)]
      (if (nom/anomaly? algo-fn)
        algo-fn
        (fn [env spec time]
          (try
            (algo-fn env spec time)
            (catch Exception ex
              (let [filename (save-error-report "time-calc" spec ex)]
                (error "time-calc " spec " exception. details: " filename)
                (nom/fail ::algo-calc {:message "time-calc exception!"
                                       :location :time
                                       :spec spec
                                       :time time})))))))))



