(ns demo.notebook
  (:require
   [reval.document.notebook :refer [eval-notebook load-notebook]]
   [goldly.scratchpad :refer [show! show-as clear!]]
   ;[demo.init] ; side effects
   ))



(eval-notebook "demo.warehouse.create-random")

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

(def ns-warehouse ["demo.warehouse.overview"
                   "demo.warehouse.create-random"
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



(def ns-playground ["demo.playground.alphavantage"
                    "demo.playground.cljplot"
                    "demo.playground.correlation"
                    "demo.playground.dataset-group"
                    "demo.playground.dataset-meta"
                    "demo.playground.dataset-random"
                    "demo.playground.date"
                    "demo.playground.series"
                    "demo.playground.symbollist"
                    "demo.playground.ta4j"
                    "demo.playground.throttle"
                    "demo.playground.tmlviz"])

(eval-notebooks ns-misc)
(eval-notebooks ns-vega)
(eval-notebooks ns-warehouse)
(eval-notebooks ns-studies)
(eval-notebooks ns-playground)

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

