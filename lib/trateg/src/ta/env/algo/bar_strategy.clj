(ns ta.env.algo.bar-strategy
  (:require 
    [taoensso.timbre :refer [trace debug info warn error]]
    [tablecloth.api :as tc]
    [ta.env.algo.trailing-window :refer [trailing-window-load-bars]]))

(defn run-algo-safe [algo-calc env opts ds-bars]
  (try
    (algo-calc env opts ds-bars)
    (catch Exception ex
      (error "exception in running algo.")
      (error "exception: " ex)
      {:error "Exception!"})))


(defn trailing-window-barstrategy [env opts time]
  (println "calculating barstrategy for time: " time)
  (if time 
     (let [ds-bars (trailing-window-load-bars env opts time)
           {:keys [algo-calc]} opts
           result (if (> (tc/row-count ds-bars) 0)
                    (run-algo-safe algo-calc env opts ds-bars)
                    {:error "empty-bar-series"})]
       result)
    {:error "time is nil"}
    ))


