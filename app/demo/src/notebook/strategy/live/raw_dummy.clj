(ns notebook.strategy.live.raw-dummy)

;; define a dummy algo
(defn algo-dummy [_env opts time]
  {:time time
   :v (:v opts)})

;; add two instruments with algo-dummy
;; this will trigger subscription of the assets


;(def bar-category [:forex :m])
;(def bar-category [:crypto :m])
(def bar-category [:crypto :m])

(def raw-dummy-strategies 
  [{:algo algo-dummy
    :algo-opts {:bar-category bar-category
                :asset "EUR/USD"
                :v 42
                :label :dummy}}
   {:algo algo-dummy
    :algo-opts {:bar-category bar-category
                :asset "USD/JPY"
                :v 41
                :label :dummy
                }}])


(comment
  (require '[modular.system])
  (def live (:live modular.system/system))
  (require '[ta.env.live-bargenerator :as env])
  (env/add live raw-dummy-strategies)
  ;; ("T2bRn7" "G8oweV")

 ; 
  )
   





