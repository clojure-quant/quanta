(ns notebook.playground.live.quotemanager
  (:require
   [modular.system]
   [ta.live.quote-manager :as qm]))

;; create quote-manager from feeds

(def feed-fx (modular.system/system :feed-fx ))
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