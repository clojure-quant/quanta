(ns notebook.playground.live.interact
  (:require [ta.interact.subscription :as sub]))

(def topic :crypto-watch)
(def topic :juan-fx)

(-> @sub/subscriptions-a keys)
(-> @sub/results-a keys)
(-> @sub/visualizations-a keys)


; subscribe
(sub/subscribe-live topic)

;; watch results..
(-> @sub/subscriptions-a topic)
(-> @sub/results-a topic)
(-> @sub/visualizations-a topic)


; (require '[notebook.strategy.sentiment-spread.vega :refer [calc-viz-vega]])
; (calc-viz-vega (-> @sub/results-a :sentiment-spread))

