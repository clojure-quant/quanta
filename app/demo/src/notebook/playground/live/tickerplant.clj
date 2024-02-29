(ns notebook.playground.live.tickerplant
  (:require
   [modular.system]
   [ta.live.tickerplant :refer [start-tickerplant current-bars]]
   [ta.algo.env.protocol :as algo]))

;; 1. get algo-env form clip

(def algo-env (modular.system/system :live))

;; 2. possibly start tickerplant (it is already started via clip)

(def quote-manager (modular.system/system :quote-manager))

(def t (start-tickerplant {:algo-env algo-env
                           :quote-manager quote-manager}))

(def t (modular.system/system :tickerplant))

t

;; define algo

(defn nil-algo [_env _opts bar-ds]
  bar-ds)

(def algo-spec
  [:eurusd-d {:type :trailing-bar
              :trailing-n 80
              :calendar [:forex :h]
              :asset "EUR/USD"
              :feed :fx
              :algo 'notebook.playground.live.tickerplant/nil-algo}
   :usdjpy-m {:type :trailing-bar
              :trailing-n 80
              :calendar [:forex :m]
              :asset "USD/JPY"
              :feed :fx
              :algo 'notebook.playground.live.tickerplant/nil-algo}
   :btc-m {:type :trailing-bar
           :trailing-n 100
           :calendar [:crypto :m]
           :asset "BTCUSDT"
           :feed :crypto
           :algo 'notebook.playground.live.tickerplant/nil-algo}
   :eth-m {:type :trailing-bar
           :trailing-n 100
           :calendar [:crypto :m]
           :asset "ETHUSDT"
           :feed :crypto
           :algo 'notebook.playground.live.tickerplant/nil-algo}

   :dummy {:type :trailing-bar
           :trailing-n 100
           :calendar [:us :m]
           :asset "WILLY-WONKER"
           ; note that no feed is defined, so no quotes will be subscribed,
           ; and no bars will be generated.
           :algo 'notebook.playground.live.tickerplant/nil-algo}])


(algo/add-algo algo-env algo-spec)

(current-bars t [:forex :m])
(current-bars t [:crypto :m])


;; => ({:asset "BTCUSD", :epoch 1}
;;     {:asset "ETHUSDT",
;;      :epoch 1,
;;      :open 3404.72,
;;      :high 3408.91,
;;      :low 3402.64,
;;      :close 3407.56,
;;      :volume 961.2800100000002,
;;      :ticks 1473})

;; => ({:asset "BTCUSD", :epoch 1}
;;     {:asset "ETHUSDT",
;;      :epoch 1,
;;      :open 3404.72,
;;      :high 3408.91,
;;      :low 3402.64,
;;      :close 3407.82,
;;      :volume 944.0061100000004,
;;      :ticks 1454})

