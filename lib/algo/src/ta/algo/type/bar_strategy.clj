(ns ta.algo.type.bar-strategy
  (:require 
    [tablecloth.api :as tc]
    [taoensso.timbre :refer [trace debug info warn error]]
    [ta.algo.chain :refer [make-chain]]
    [ta.algo.env.trailing-window :refer [create-trailing-bar-loader]]))

(defn run-algo-safe [algo-fn env spec ds-bars]
  (try
    (algo-fn env spec ds-bars)
    (catch Exception ex
      (error "exception in running algo.")
      (error "algo exception: " ex)
      {:error "Exception!"})))

(defn create-trailing-barstrategy [{:keys [trailing-n asset algo] :as spec}]
  (assert trailing-n)
  (assert asset)
  (assert algo)
  (let [algo-fn (make-chain algo)
        load-fn (create-trailing-bar-loader spec)]
     (assert algo-fn)
     (fn [env _spec time]
       ;(println "calculating barstrategy for time: " time)
       (when time 
         (let [ds-bars (load-fn env spec time)]
           (if (and ds-bars (> (tc/row-count ds-bars) 0))
             (run-algo-safe algo-fn env spec ds-bars)
             {:error "no ds-bars available. "
              :time time
              :spec spec})))
         )))

