(ns ta.config
  (:require
   [modular.config :as config]))

(def test-ta-config
  {:warehouse {:series  {:test-wh "/tmp/"}
               :list "../../app/resources/symbollist/"}})

(println "loading test config..")

(config/set! :ta test-ta-config)




