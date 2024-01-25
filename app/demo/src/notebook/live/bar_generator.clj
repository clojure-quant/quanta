(ns notebook.live.bar-generator
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [ta.tickerplant.bar-generator :as bg]))


;; 1. create bargenerator

(def state
  (bg/bargenerator-start [:us :m] bg/print-finished-bars))
  
state

;; 2. send quotes to bargenerator
(bg/process-tick state {:symbol "MSFT" :price 98.20 :size 100})
(bg/process-tick state {:symbol "EURUSD" :price 1.0910 :size 100})
(bg/process-tick state {:symbol "EURUSD" :price 1.0920 :size 100})

;; 3. look at current bar state
(bg/current-bars (:db state))

;; 4. stop bargenerator  
(bg/bargenerator-stop state)

