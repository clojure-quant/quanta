(ns demo.notebook
  (:require
   [reval.document.notebook :refer [eval-notebook load-notebook]]
   [goldly.scratchpad :refer [show! show-as clear!]]
   ;[demo.init] ; side effects
   ))



(eval-notebook "demo.warehouse.create-random")
(eval-notebook "demo.playground.dataset-group")


(-> ;(eval-notebook "demo.playground.cljplot")
 (eval-notebook "demo.warehouse.overview")
 show!)


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

(def ns-data ["demo.warehouse.create-random"
              "demo.warehouse.import-alphavantage"
              "demo.warehouse.import-bybit"])

(def ns-studies ["demo.studies.asset-allocation-dynamic"
                 "demo.studies.bollinger"
                 "demo.studies.bollinger-forward"
                 "demo.studies.buyhold"
                 "demo.studies.cluster-real"
                 "demo.studies.moon"
                 "demo.studies.sma"
                 "demo.studies.supertrend"
                    ;task.clj
                 ])

(def ns-data ["notebook.data.alphavantage"
              "notebook.data.warehouse-overview"
              "notebook.data.series"
              ;"demo.playground.symbollist"
              ;"demo.playground.ta4j"
              ;"demo.playground.throttle"
              ;"demo.playground.tmlviz"
              ])




(def ns-datascience ["notebook.datascience.plot_clj"
                     "notebook.datascience.plot_tml"
                     "notebook.datascience.correlation"
                     "notebook.datascience.dataset-group"
                     "notebook.datascience.dataset-meta"
                     "notebook.datascience.dataset-random"
                     "notebook.datascience.date"])


(eval-notebooks ns-misc)
(eval-notebooks ns-vega)

(eval-notebooks ns-playground)
(eval-notebooks ns-studies)

(eval-notebooks ns-datascience)
(eval-notebooks ns-data)


(eval-notebook "demo.warehouse.overview")

(load-notebook "demo.studies.buyhold")


(-> (eval-notebook "user.notebook.hello")
    :content
    count)

(->> (eval-notebook "user.notebook.hello")
     (show-as :p/notebook))

; demo.notebook.image is part of the reval demo notebooks
(->> (eval-notebook "user.notebook.image")
     (show-as :p/notebook))

