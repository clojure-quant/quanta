(ns ta.algo.spec.type.bar-strategy
  (:require
   [de.otto.nom.core :as nom]
   [taoensso.timbre :refer [trace debug info warn error]]
   [tablecloth.api :as tc]
   [ta.algo.spec.parser.chain :as chain]
   [ta.algo.env.core :refer [get-trailing-bars]]))

(defn run-algo-safe [algo-fn env spec ds-bars]
  (try
    (if (and ds-bars (> (tc/row-count ds-bars) 0))
      (algo-fn env spec ds-bars)
      (nom/fail ::algo-calc {:message "bar-strategy cannot calc because no bars!"
                             :location :bar-strategy-algo
                             :spec spec}))
    (catch Exception ex
      (error "exception in running algo.")
      (error "algo exception: " ex)
      (nom/fail ::algo-calc {:message "algo calc exception!"
                             :location :bar-strategy-algo
                             :spec spec
                             :ds-bars ds-bars}))))

(defn create-trailing-bar-loader [{:keys [asset calendar trailing-n] :as _spec}]
  ; fail once, when required parameters are missing
  (assert trailing-n)
  (assert asset)
  (assert calendar)
  (fn [env spec time]
    (if time
      (try
        (get-trailing-bars env spec time)
        (catch Exception ex
          (error "exception in loading bars: spec: "  spec)
          (error ex)
          (nom/fail ::algo-calc {:message "algo calc exception!"
                                 :location :bar-strategy-load-bars
                                 :spec spec
                                 :time time})))
      (nom/fail ::algo-calc {:message "algo calc needs a valid time"
                             :location :bar-strategy-load-bars
                             :spec spec}))))

(defn create-trailing-barstrategy [{:keys [trailing-n asset algo] :as spec}]
  (assert trailing-n)
  (assert asset)
  (assert algo)
  (let [algo-fn (chain/make-chain algo)
        load-fn (create-trailing-bar-loader spec)]
    (assert algo-fn)
    (fn [env _spec time]
       ;(println "calculating barstrategy for time: " time)
      (when time
        (let [ds-bars (load-fn env spec time)]
          (if (nom/anomaly? ds-bars)
            ds-bars
            (run-algo-safe algo-fn env spec ds-bars)))))))

