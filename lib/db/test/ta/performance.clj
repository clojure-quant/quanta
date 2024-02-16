(ns ta.performance
  (:require
   [ta.wh-test :refer [series-generate-save-reload]]
   [ta.config] ; side-effects
   ))

(defn performance-test
  [_]
  (time (series-generate-save-reload 2000 "small"))
  (time (series-generate-save-reload 20000 "big")) ; tradingview limit
  (time (series-generate-save-reload 200000 "huge"))
  (time (series-generate-save-reload 2000000 "gigantic")))

(comment
  (performance-test)
;   
  )

