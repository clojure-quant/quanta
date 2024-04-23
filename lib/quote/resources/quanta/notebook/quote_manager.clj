(ns quanta.notebook.quote-manager
  (:require
   [modular.system]
   [ta.live.quote-manager :as qm]
   [clojure.pprint :refer [print-table]]))

;; create quote-manager from feeds

(def feed-fx (modular.system/system :feed-fx))
(def feed-bybit (modular.system/system :feed-bybit))
(def feeds {:fx feed-fx :crypto feed-bybit})
(def q (qm/create-quote-manager feeds))

;; use quote-manager from clip 
(def q (modular.system/system :quote-manager))

;; interact with quote-manager

(qm/subscribe q {:asset "EUR/USD" :feed :fx})
(qm/subscribe q {:asset "USD/JPY" :feed :fx})
(qm/subscribe q {:asset "BTCUSDT" :feed :crypto})
(qm/subscribe q {:asset "ETHUSDT" :feed :crypto})

(qm/quote-snapshot q)
;; => ({:asset "NZD/USD", :price 0.61053, :size 100.0, :feed :fx}
;;     {:asset "AUD/USD", :price 0.65303, :size 100.0, :feed :fx}
;;     {:asset "USD/CAD", :price 1.355, :size 100.0, :feed :fx}
;;     {:asset "EUR/JPY", :price 162.715, :size 100.0, :feed :fx}
;;     {:asset "USD/CHF", :price 0.88394, :size 100.0, :feed :fx}
;;     {:asset "USD/ZAR", :price 19.08905, :size 100.0, :feed :fx}
;;     {:asset "EUR/CHF", :price 0.95793, :size 100.0, :feed :fx}
;;     {:asset "GBP/JPY", :price 190.031, :size 100.0, :feed :fx}
;;     {:asset "USD/NOK", :price 10.5221, :size 100.0, :feed :fx}
;;     {:asset "USD/JPY", :price 150.145, :size 100.0, :feed :fx}
;;     {:asset "USD/MXN", :price 17.0115, :size 100.0, :feed :fx}
;;     {:asset "USD/SEK", :price 10.3217, :size 100.0, :feed :fx}
;;     {:asset "GBP/USD", :price 1.26564, :size 100.0, :feed :fx}
;;     {:asset "EUR/USD", :price 1.08372, :size 100.0, :feed :fx}
;;     {:asset "BTCUSDT", :price 61976.11, :size 0.001, :feed :bybit}
;;     {:asset "ETHUSDT", :price 3418.86, :size 0.05512, :feed :bybit}
;;     {:asset "EUR/GBP", :price 0.85629, :size 100.0, :feed :fx})

(-> (qm/quote-snapshot q)
    print-table)

