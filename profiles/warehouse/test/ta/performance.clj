(ns ta.performance
  (:require
   [clojure.test :refer :all]
   [ta.random :refer [random-ts]]
   [ta.warehouse :as wh]
   [ta.config :refer [w]]
   [ta.wh-test :refer[series-generate-save-reload]]
   ))





(defn performance-test
  [_]
  (time (series-generate-save-reload 2000 "small"))
  (time (series-generate-save-reload 20000 "big")) ; tradingview limit
  (time (series-generate-save-reload 200000 "huge"))
  (time (series-generate-save-reload 2000000 "gigantic")))


; (performance-test)
