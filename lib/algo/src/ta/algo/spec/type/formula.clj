(ns ta.algo.spec.type.formula
  (:require
   [de.otto.nom.core :as nom]
   [taoensso.timbre :refer [trace debug info warn error]]
   [ta.algo.compile :refer [compile-symbol]]
   [ta.algo.error-report :refer [save-error-report]]))

(defn create-formula-algo
  "on init:
     -success: returns (fn [env spec & args])
     -error: returns nom/anomaly.
   on runtime:
     -success: returns result of the fn
     -error: returns nom-anomaly"
  [{:keys [algo] :as spec}]
  (cond

    (nil? spec)
    (nom/fail ::create-formula-algo {:message "cannot create formula - spec is nil"
                                     :location :formula})
    (not (map? spec))
    (nom/fail ::create-formula-algo {:message "cannot create formula - spec is not a map"
                                     :location :formula
                                     :spec spec})
    (not algo)
    (nom/fail ::create-formula-algo {:message "cannot create formula -  spec/algo is nil"
                                     :location :formula
                                     :spec spec})

    :else
    (let [algo-fn (compile-symbol algo)]
      (if (nom/anomaly? algo-fn)
        algo-fn
        (fn [env spec & args]
          (try
            (apply algo-fn env spec args)
            (catch Exception ex
              (let [filename (save-error-report "formula-calc" spec ex)]
                (error "formula-calc " spec " exception. details: " filename)
                (nom/fail ::formula-calc {:message "formula calc exception!"
                                          :location :formula
                                          :ex ex
                                          :spec spec
                                          :args args})))))))))
