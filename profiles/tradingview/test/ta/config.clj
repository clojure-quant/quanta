(ns ta.config
  (:require
   [modular.config :as config]))

(def test-ta-config
  {:warehouse {:series  {:test-wh "/tmp/"}
               :list "../resources/symbollist/"}
   :tradingview {:charts-path "/tmp/"
                 :template-path "src/ta/tradingview/chart/template/"}})

(config/set! :ta test-ta-config)

