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
      (tablecloth/select-columns [:date :close
                                  :bb-lower :bb-upper
                                  :above :below
                                  ;:above-count :below-count
                                  ])
      (helper/pprint-all)
      ;(helper/pprint-dataset)
      info)
  (info "study calculation finished."))


(defn task-bollinger-optimizer [& _]
  (info "running bollinger strategy optimizer")
  (for [length (range 10 200 10)
      ; mult-up (range 0.5 3.5 0.5)
        ]
    (do
      (-> (strategy/run-study
           "ETHUSD" "D"
           strategy/study-bollinger
           {:sma-length length
            :stddev-length length
            :mult-up  1.5
            :mult-down 1.5}
           "bollinger-upcross")
          (strategy/goodness-event-count)
          (info "<== COUNT - " "l:" length)))))


(comment

  (task-bollinger-study)
  (task-bollinger-optimizer)

 ; 
  )