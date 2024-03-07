(ns notebook.playground.live.interact
  (:require 
   [ta.interact.template :refer [get-options]]
   [ta.interact.subscription :as sub]))

(get-options :crypto-watch)

; subscribe
(def subscription-id 
  (sub/subscribe-live 
   :crypto-watch {:asset "ETHUSDT"}))

subscription-id

; check state (developer debugging)

(-> @sub/subscriptions-a keys)
(-> @sub/results-a keys)
(-> @sub/visualizations-a keys)


;; watch results..
(-> @sub/subscriptions-a (get subscription-id))
(-> @sub/results-a (get subscription-id))
(-> @sub/visualizations-a (get subscription-id))


; (require '[notebook.strategy.sentiment-spread.vega :refer [calc-viz-vega]])
; (calc-viz-vega (-> @sub/results-a :sentiment-spread))

