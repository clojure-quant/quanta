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
              :mult-up 2.0
              :mult-down 2.0
              :forward-size 20
              })

(defn task-bollinger-study [& _]
  (info "running bollinger strategy with options: " options)
  (let [ds-study  (strategy/run-study "ETHUSD" "D" strategy/study-bollinger options)
        ds-events (strategy/study-bollinger-filter-events ds-study options)
        ds-forward (strategy/backtest-bollinger "ETHUSD" "D" options)
        ds-forward-grouped (strategy/backtest-grouper ds-forward)
        ]
    (info "Bollinger Events:")
    (-> ds-events
        (strategy/save-study  "ETHUSD" "D" "bollinger-upcross")
        (tablecloth/select-columns [:index :date :close
                                    :bb-lower :bb-upper
                                    :above :below
                                  ;:above-count :below-count
                                    ])
        (helper/pprint-all)
      ;(helper/pprint-dataset)
        info)
    (-> ds-forward
        (strategy/save-study  "ETHUSD" "D" "bollinger-forward")
        #_(tablecloth/select-columns [:index :date :close
                                    :bb-lower :bb-upper
                                    :above :below
                                  ;:above-count :below-count
                                    ])
        (helper/pprint-all)
      ;(helper/pprint-dataset)
        info)

    (-> ds-forward-grouped
        (helper/pprint-all)
        info)

    (-> ds-forward-grouped
        strategy/bollinger-goodness
        #_(tablecloth/select-columns [:index :date :close
                                      :bb-lower :bb-upper
                                      :above :below
                                  ;:above-count :below-count
                                      ])
        (helper/pprint-all)
      ;(helper/pprint-dataset)
        info)

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
                    :forward-size length}]
       (strategy/pipeline-bollinger-goodness "ETHUSD" "D" options)))
   (tablecloth/dataset)
   (tablecloth/select-columns [:sma-length
                               :down-count
                               :up-count
                               :goodness])
   (helper/pprint-all)
   info))

(comment

  (task-bollinger-study)

  (task-bollinger-optimizer)

 ; 
  )