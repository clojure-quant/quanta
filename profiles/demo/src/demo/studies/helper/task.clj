(ns demo.studies.task
  (:require
   [taoensso.timbre :refer [trace debug info infof warn error]]
   [tablecloth.api :as tablecloth]
   [webly.log]
   [ta.helper.print :as helper]
   [ ta.backtest.backtester :as backtest]
   [demo.env.config :refer [w-crypto]]
   [demo.studies.helper.bollinger :as bs]))

(webly.log/timbre-config!
 {:timbre-loglevel
  [[#{"pinkgorilla.nrepl.client.connection"} :info]
   [#{"org.eclipse.jetty.*"} :info]
   [#{"webly.*"} :info]
   [#{"*"} :info]]})

(def default-options {:sma-length 20
                      :stddev-length 20
                      :mult-up 2.0
                      :mult-down 2.0
                      :forward-size 20})

(defn task-bollinger-study [& _]
  (info "running bollinger strategy with options: " default-options)
  (let [r (backtest/run-study w-crypto "ETHUSD" "D"
                              bs/bollinger-study
                              default-options)]
    (bs/print-all r :ds-study)
    (bs/print-all r :ds-events-all)
    (bs/print-all r :ds-events-forward)
    (bs/print-all r :ds-performance)
    (bs/print-backtest-numbers r)

    (backtest/save-study w-crypto (:ds-study r) "ETHUSD" "D" "bollinger-upcross")
    (info "study calculation finished.")))

(defn task-bollinger-optimizer [& _]
  (info "running bollinger strategy optimizer")
  (->
   (for [length (range 10 200 10)
      ; mult-up (range 0.5 3.5 0.5)
         ]
     (let [options {:sma-length length
                    :stddev-length length
                    :mult-up  1.5
                    :mult-down 1.5
                    :forward-size length}
           r (backtest/run-study w-crypto "ETHUSD" "D"
                                 bs/bollinger-study
                                 options)]
       (:backtest-numbers r)))
   (tablecloth/dataset)
   (bs/print-ds-cols-all [:sma-length
                          :down-count
                          :up-count
                          :goodness])))

(comment

  (task-bollinger-study)

  (task-bollinger-optimizer)

 ; 
  )