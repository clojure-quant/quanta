(ns demo.viktor.task
  (:require
   [taoensso.timbre :refer [trace debug info infof warn error]]
   [tablecloth.api :as tablecloth]
   [webly.log]

   [ta.dataset.helper :as helper]
   [demo.viktor.backtest :as strategy]))

(webly.log/timbre-config!
 {:timbre-loglevel
  [[#{"pinkgorilla.nrepl.client.connection"} :info]
   [#{"org.eclipse.jetty.*"} :info]
   [#{"webly.*"} :info]
   [#{"*"} :info]]})

(def options {:sma-length 20
              :stddev-length 20
              :mult-up 1.5
              :mult-down 1.5})

(defn task-bollinger-study [& _]
  (info "running bollinger strategy with options: " options)
  (-> (strategy/run-study
       "ETHUSD" "D"
       strategy/study-bollinger
       options
       "bollinger-upcross")
      (tablecloth/select-rows strategy/is-above-or-below)
      (tablecloth/select-columns [:date :close :bb-lower :bb-upper :below  :above])
      (helper/pprint-all)
      ;(helper/pprint-dataset)
      info)

  (info "study calculation finished."))

(comment

  (task-bollinger-study)

 ; 
  )