(ns notebook.main
  (:require
   [pinkgorilla.notebook-ui.app-bundel.app :refer [notebook-bundel-run!]]
   [gigasquid.utils]; bring to jar
   )
  (:gen-class))


(def notebook-config
  {:explorer {:client {:repositories [{:name "local"  :url "/api/explorer" :save true}
                                      #_{:name "public" :url "https://raw.githubusercontent.com/pink-gorilla/gorilla-explore/master/resources/list.json"}]}
              :server {:resource-root-path "notebooks-bad"
                       :roots  
                       {:ta "./notebooks"
                        :gorilla-plot "../../gorilla/gorilla-plot/notebooks"
                        ;:backtest "../../quant/backtest/notebooks"
                        ;:fquant "../../quant/clojureQuant/notebooks"
                        ;:gorilla-ui "../gorilla-ui/resources/notebooks"
                        }}}
   
})

(defn start-notebook! []
  (notebook-bundel-run! notebook-config))

(defn -main []
  (println "Running PinkGorilla Notebook")
  (start-notebook!))

(comment
  (start-notebook!)

  ;comment end
  )