(ns notebook.algo-config.multicalendar-sma)


 (def multi-calendar-algo-demo
  [{:asset "EUR/USD"
    :feed :fx
    :topic :multi-calendar}
   :us :h [{:trailing-n 100
            :sma 30}
           'ta.env.live.trailing-window-algo/trailing-window-load-bars
           'notebook.algo.sma3/bar-strategy]
   :us :m [{:trailing-n 60
            :sma 20}
           'ta.env.live.trailing-window-algo/trailing-window-load-bars
           'notebook.algo.sma3/bar-strategy]
     ; :* :*  [get-current-position
     ;         all-positions-agree]
   ])


 (comment
   (require '[modular.system])
   (def live (:live modular.system/system))
    
   (require '[ta.env.dsl.multi-calendar :as dsl])
 
   (dsl/add live multi-calendar-algo-demo)
   ; ("Y2yvV4" "lOZ_AU")
 
 (require '[ta.env.live-bargenerator :as env])
   
   (env/algos-matching live :topic :multi-calendar)
 
  ; 
   )