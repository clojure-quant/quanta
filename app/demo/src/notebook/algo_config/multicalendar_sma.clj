(ns notebook.algo-config.multicalendar-sma)


 (def multi-calendar-algo-demo
  [{:type :multi-calendar
    :asset "EUR/USD"
    :feed :fx
    :label :multi-calendar}
   :us :h [{:trailing-n 100
            :sma 30}
           'ta.env.algo.trailing-window/trailing-window-load-bars
           'notebook.algo.sma3/bar-strategy]
   :us :m [{:trailing-n 60
            :sma 20}
           'ta.env.algo.trailing-window/trailing-window-load-bars
           'notebook.algo.sma3/bar-strategy]
    :* :*  ['ta.algo.ds/get-current-positions
            'ta.algo.ds/all-positions-agree]
   ])


 (comment
   (require '[modular.system])
   (def live (:live modular.system/system))
    
   (require '[ta.env.dsl.multi-calendar :as dsl])
 
   (dsl/add live multi-calendar-algo-demo)
   ; ("Y2yvV4" "lOZ_AU")
 
 (require '[ta.env.live-bargenerator :as env])
   
   (env/algos-matching live :label :multi-calendar)
 
  ; 
   )