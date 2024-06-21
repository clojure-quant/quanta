(ns ta.algo.spec.type.bar-strategy
  (:require
   [de.otto.nom.core :as nom]
   [taoensso.timbre :refer [trace debug info warn error]]
   [tablecloth.api :as tc]
   [ta.algo.spec.parser.chain :as chain]
   [ta.algo.env.core :refer [get-trailing-bars]]
   [ta.algo.error-report :refer [save-error-report]]))

(defn create-error [spec ex]
  (let [filename (save-error-report (str "run-algo-" (:algo spec)) spec ex)]
    (error "run-algo " spec " exception. details: " filename)
    (nom/fail ::algo-calc {:message "algo calc exception!"
                           :location :bar-strategy-algo
                           :file filename
                           :spec spec
                                   ;:ds-bars ds-bars ; dataset cannot be sent to the browser.
                           })))

(defn run-algo-safe [algo-fn env spec ds-bars]
  (warn "run-algo-safe: " (:algo spec))
  (cond
    ; algo-fn is nil (most likely a compile error)
    (nil? algo-fn)
    (nom/fail ::algo-calc {:message "bar-strategy fn compile error"
                           :location :bar-strategy-algo
                           :spec spec})
    ; bar data is nil or has no rows
    (or (nil? ds-bars) (= (tc/row-count ds-bars) 0))
    (nom/fail ::algo-calc {:message "bar-strategy cannot calc because no bars!"
                           :location :bar-strategy-algo
                           :spec spec})
    ; run algo
    :else
    (try
      (warn "run-algo-safe else.. fn: " algo-fn)
      (algo-fn env spec ds-bars)
      (catch AssertionError ex (create-error spec ex))
      (catch Exception ex (create-error spec ex)))))

(defn create-trailing-bar-loader [{:keys [asset calendar trailing-n] :as spec}]
  (cond
    ; fail once, when required parameters are missing  
    (or (not trailing-n)
        (not asset)
        (not calendar))
    (nom/fail ::algo-calc {:message "algo load-bars needs :trailing-n :asset :calendar"
                           :location :bar-strategy-load-bars
                           :spec spec})
    ; init happy path
    :else
    (fn [env spec time]
      (cond
      ; no result if time is not valid
        (or (nil? time) (nom/anomaly? time))
        (nom/fail ::algo-calc {:message "algo calc needs a valid time"
                               :location :bar-strategy-load-bars
                               :time time
                               :spec spec})
      ; runtime happy path
        :else
        (try
          (get-trailing-bars env spec time)
          (catch Exception ex
            (let [filename (save-error-report "run-algo-load-bars" spec ex)]
              (error "load-bars-algo " spec " exception. details: " filename)
              (nom/fail ::algo-calc {:message "algo calc load-bars exception!"
                                     :location :bar-strategy-load-bars
                                     :spec spec
                                     :time time}))))))))

(defn create-trailing-barstrategy [{:keys [trailing-n asset algo] :as spec}]
  (cond

    (nil? spec)
    (nom/fail ::create-barstrategy-algo {:message "cannot create barstrategy-algo - spec is nil"
                                         :location :barstrategy})
    (not (map? spec))
    (nom/fail ::create-barstrategy-algo {:message "cannot create barstrategy-algo - spec is not a map"
                                         :location :barstrategy
                                         :spec spec})
    (not algo)
    (nom/fail ::create-barstrategy-algo {:message "cannot create barstrategy-algo -  spec/algo is nil"
                                         :location :barstrategy
                                         :spec spec})

    (not trailing-n)
    (nom/fail ::create-barstrategy-algo {:message "cannot create barstrategy-algo -  trailing-n is nil"
                                         :location :barstrategy
                                         :spec spec})

    (not asset)
    (nom/fail ::create-barstrategy-algo {:message "cannot create barstrategy-algo -  asset is nil"
                                         :location :barstrategy
                                         :spec spec})

    (not trailing-n)
    (nom/fail ::create-barstrategy-algo {:message "cannot create barstrategy-algo -  trailing-n is nil"
                                         :location :barstrategy
                                         :spec spec})

    :else
    (let [algo-fn (chain/make-chain algo)
          load-fn (create-trailing-bar-loader spec)]
      (if (nom/anomaly? algo-fn)
        algo-fn
        (fn [env _spec time]
          (when time
            (let [ds-bars (load-fn env spec time)]
              (if (nom/anomaly? ds-bars)
                ds-bars
                (run-algo-safe algo-fn env spec ds-bars)))))))))

