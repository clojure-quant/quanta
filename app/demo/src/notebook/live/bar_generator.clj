(ns notebook.live.bar-generator
  (:require
   [ta.tickerplant.bar-generator :as bg]
   [manifold.stream :as s]))

;; 1. create bargenerator

(def bar-category [:us :m])

(def state (bg/bargenerator-start bar-category))
state

(s/consume bg/print-finished-bars (bg/bar-close-stream state))

;; 2. send quotes to bargenerator
(bg/process-tick state {:asset "MSFT" :price 98.20 :size 100})
(bg/process-tick state {:asset "EURUSD" :price 1.0910 :size 100})
(bg/process-tick state {:asset "EURUSD" :price 1.0920 :size 100})

;; 3. look at current bar state
;; if this is executed within the same minute of step 2, then
;; it will show the current bars. If it is called later, then 
;; it will not have any bars. 
(bg/current-bars state)


;; 4. stop bargenerator  
(bg/bargenerator-stop state)

