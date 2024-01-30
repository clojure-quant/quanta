(ns ta.env.live.trailing-window-algo
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [ta.calendar.core :refer [trailing-window]]
   [ta.algo.core :refer [get-algo-calc]]
   [tick.core :as t]
   [tablecloth.api :as tc]))

(defn run-algo-safe [algo-calc ds-bars opts]
  (try 
     (algo-calc ds-bars opts)
     (catch Exception ex
       (error "exception in running algo.")
       (error "exception: " ex)
       {:error "Exception!"})))

(defn trailing-window-load-bars [env opts time]
  (info "trailing window load bars:" opts " time: " time)
    (let [{:keys [get-series]} env
          {:keys [bar-category trailing-n algo-calc asset]} opts
          [calendar interval] bar-category
          time-seq (trailing-window calendar interval trailing-n time)
          dend  (first time-seq)
          dstart (last time-seq)
          dend-instant (t/instant dend)
          dstart-instant (t/instant dstart)
          ds-bars (get-series bar-category asset dstart-instant dend-instant)]
      {:dstart dstart
       :dend dend
       :time time
       :opts opts
       :time-seq time-seq
       :ds-bars ds-bars}))

(defn trailing-window-algo-run [env opts time]
(let [data (trailing-window-load-bars env opts time)
      {:keys [algo-calc id topic]} opts
      ds-bars (:ds-bars data)
      result (if (> (tc/row-count ds-bars) 0)
                 (run-algo-safe algo-calc ds-bars opts)
                 :error/empty-bar-series
               )]
  (assoc data :result result :id id :topic topic)))

(defn trailing-window-algo [algo-opts]
  (let [algo-ns (:algo-ns algo-opts)
        algo-calc (get-algo-calc algo-ns)]
  {:algo trailing-window-algo-run
   :algo-opts (assoc algo-opts :algo-calc algo-calc)}))
 

(comment 
  (require '[modular.system])
  (def duckdb (modular.system/system :duckdb))
  duckdb
  (require '[ta.warehouse.duckdb :refer [get-bars-window get-bars-since]])
  
  (get-bars-window duckdb [:us :m] "EUR/USD"
                   "2024-01-26T19:35:00Z"
                     "2024-01-26T19:45:00Z")

  (def env {:get-series (partial get-bars-window duckdb)})


  (require '[ta.calendar.core :refer [current-close]])
  (def dt (current-close :us :m))
  dt

  (require '[tick.core :as t])
  (def time (t/instant "2024-01-26T20:00:00Z"))
  (get-bars-since duckdb [:us :m] "EUR/USD" time)
  
  
  (trailing-window-algo-run env {:bar-category [:us :m]
                                 :asset "EUR/USD"
                                 :trailing-n 5} time)

 (get-algo-calc 'demo.algo.sma3)
  
  
  (require '[ta.env.live-bargenerator :refer [calc-algo]])

  (calc-algo env 
             (trailing-window-algo {:algo-ns 'demo.algo.sma3
                                    :bar-category [:us :m]
                                    :asset "EUR/USD"
                                    :trailing-n 5
                                    :sma-length-st 2
                                    :sma-length-lt 3
                                    })
                                   dt)

 ; 
  )
