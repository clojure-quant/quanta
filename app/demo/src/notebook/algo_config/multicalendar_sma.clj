(ns notebook.algo-config.multicalendar-sma)


 (def multi-calendar-algo-demo
  [{:asset "EUR/USD"
    :feed :fx}
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


 