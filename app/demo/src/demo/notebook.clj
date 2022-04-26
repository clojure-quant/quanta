(ns demo.notebook
  (:require
   [reval.document.notebook :refer [eval-notebook load-notebook]]
   [goldly.scratchpad :refer [show! show-as clear!]]
   ;[demo.init] ; side effects
   ))

(defn eval-notebooks [ns-list]
  (map eval-notebook ns-list))

(def ns-misc ["demo.notebook.reval-image"])

(def ns-vega ["demo.notebook.vegalite-arrow"
              "demo.notebook.vegalite-bar"
              "demo.notebook.vegalite-multiline"
              "demo.notebook.vegalite-point"
              "demo.notebook.vegalite-zoom"
              "demo.notebook.vega-zoom"
              "demo.notebook.vega-test"
              "demo.notebook.vega-rect"
              "demo.notebook.gorillaplot-core"])

(def ns-datascience ["notebook.datascience.plot_clj"
                     "notebook.datascience.plot_tml"
                     "notebook.datascience.correlation"
                     "notebook.datascience.dataset-group"
                     "notebook.datascience.dataset-meta"
                     "notebook.datascience.dataset-random"
                     "notebook.datascience.date"])

(def ns-data ["notebook.data.alphavantage"
              "notebook.data.warehouse-overview"
              "notebook.data.series"])

(def ns-playground ["notebook.playground.svg"
              ;"notebook.plazground.ta4j"
              ;"notebook.plazground.throttle"
                    ])
(def ns-studies ["notebook.studies.astro"
                 "notebook.studies.asset-allocation-dynamic"
                 "notebook.studies.bollinger"
                 "notebook.studies.bollinger-forward"
                 "notebook.studies.buyhold"
                 "notebook.studies.cluster-real"
                 "notebook.studies.moon"
                 "notebook.studies.sma"
                 "notebook.studies.supertrend"
                    ;task.clj
                 ])

(load-notebook "notebook.studies.asset-allocation-dynamic")

(eval-notebook "demo.playground.dataset-group")

(-> ;(eval-notebook "demo.playground.cljplot")
 (eval-notebook "notebook.data.warehouse-overview")
 show!)

(eval-notebooks ns-misc)
(eval-notebooks ns-vega)
(eval-notebooks ns-datascience)
(eval-notebooks ns-playground)

(eval-notebooks ns-data)
(eval-notebooks ns-studies)


(-> (eval-notebook "user.notebook.hello")
    :content
    count)

(->> (eval-notebook "user.notebook.hello")
     (show-as :p/notebook))

; demo.notebook.image is part of the reval demo notebooks
(->> (eval-notebook "user.notebook.image")
     (show-as :p/notebook))

