(ns ta.config
  (:require
   [modular.config :as config]))


(def test-ta-config
  {:warehouse {:series  {:test-wh "/tmp/"}
               :list "../resources/symbollist/"}})


(config/set! :ta test-ta-config)




