(ns notebook.live.live-dummy-strategy
  (:require
   [ta.env.live-bargenerator :as env]
   ))

;; define a dummy algo
(defn algo-dummy [_env opts time]
  {:time time
   :v 42})

;; add two instruments with algo-dummy
;; this will trigger subscription of the assets

(defn run-dummy-strategy [live]
   (env/add live [:us :m] {:algo algo-dummy
                           :algo-opts {:asset "EUR/USD"}})

   (env/add live [:us :m] {:algo algo-dummy
                           :algo-opts {:asset "USD/JPY"}})
  
  
  
  )



