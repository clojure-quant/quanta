(ns notebook.playground.live.interact
  (:require 
   [ta.interact.template :refer [get-options]]
   [ta.interact.subscription :as sub]))

(-> :watch-crypto get-options )

(-> :juan-fx 
    get-options 
    ;:current
    )
;; => {:options
;;     [{:path [0 :asset],
;;       :name "asset",
;;       :spec
;;       ["EUR/USD"
;;        "USD/CHF"
;;        "GBP/USD"
;;        "USD/SEK"
;;        "USD/NOK"
;;        "USD/CAD"
;;        "USD/JPY"
;;        "AUD/USD"
;;        "NZD/USD"
;;        "USD/MXN"
;;        "USD/ZAR"
;;        "EUR/JPY"
;;        "EUR/CHF"
;;        "EUR/GBP"
;;        "GBP/JPY"]}
;;      {:path [2 :trailing-n], :name "DailyLoad#", :spec [2 5 10 20 30 50 80 100 120 150]}
;;      {:path [2 :atr-n], :name "dATR#", :spec [5 10 20 30]}
;;      {:path [2 :percentile], :name "dPercentile", :spec [10 20 30 40 50 60 70 80 90]}
;;      {:path [2 :step], :name "dStep", :spec [0.001 1.0E-4 4.0E-5]}
;;      {:path [4 :max-open-close-over-low-high], :name "doji-co/lh max", :spec [0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9]}],
;;     :current
;;     {[0 :asset] "USD/JPY",
;;      [2 :trailing-n] 120,
;;      [2 :atr-n] 10,
;;      [2 :percentile] 70,
;;      [2 :step] 1.0E-4,
;;      [4 :max-open-close-over-low-high] 0.3}}


; subscribe
(def subscription-id 
  (sub/subscribe-live 
   :crypto-watch {:asset "ETHUSDT"}))

subscription-id

; check state (developer debugging)

(-> @sub/subscriptions-a keys)
;; => ()

(-> @sub/results-a keys)
(-> @sub/visualizations-a keys)


;; watch results..
(def subscription-id "FOkpkS")

(-> @sub/subscriptions-a (get subscription-id))
(-> @sub/results-a (get subscription-id))
(-> @sub/visualizations-a (get subscription-id))


; (require '[notebook.strategy.sentiment-spread.vega :refer [calc-viz-vega]])
; (calc-viz-vega (-> @sub/results-a :sentiment-spread))

