(ns ta.env.algo.trailing-window
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [ta.calendar.core :refer [trailing-window]]
   [tick.core :as t]))

(defn trailing-window-load-bars-full [env opts time]
  (info "trailing window load bars:" opts " time: " time)
  (let [{:keys [get-series]} env
        {:keys [bar-category trailing-n asset]} opts
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

(defn trailing-window-load-bars [env opts time]
  (-> (trailing-window-load-bars-full env opts time)
      :ds-bars))


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



 ; 
  )
