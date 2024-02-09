(ns notebook.algo-config.raw-dummy)

;; define a dummy algo
(defn algo-dummy [_env opts time]
  {:time time
   :v 42})

;; add two instruments with algo-dummy
;; this will trigger subscription of the assets


;(def bar-category [:forex :m])
;(def bar-category [:crypto :m])
(def bar-category [:us :m])

(def raw-dummy-strategies 
  [{:algo algo-dummy
    :algo-opts {:bar-category bar-category
                :asset "EUR/USD"}}
   {:algo algo-dummy
    :algo-opts {:bar-category bar-category
                :asset "USD/JPY"}}])

   



