(ns notebook.live.live-sma-strategy
  (:require
   [ta.env.live-bargenerator :as env]))


(def bar-category [:us :m])

(def base {:algo-ns 'demo.algo.sma3
           :bar-category [:us :m]
           :asset "EUR/USD"
           :trailing-n 5
           :sma-length-st 2
           :sma-length-lt 3})
           
(def eurusd (assoc base :asset "EUR/USD"))

(def usdjpy (assoc base :asset "USD/JPY"))

(defn add-strategies [live strategies]
  (let [add (partial env/add-bar-strategy live)]
    (doall
      (map add strategies))))

(defn add-sma-strategy [live]
  (add-strategies live [eurusd usdjpy]))


(comment 
   (require '[modular.system])
  
  (def live (:live modular.system/system))
  live

  (add-sma-strategy live)

  
  
  
 ; 
  )

