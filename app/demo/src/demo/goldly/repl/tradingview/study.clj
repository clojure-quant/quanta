(ns demo.goldly.repl.tradingview.study
  (:require
   [reval.cljs-eval :refer [eval-code!]]))

(eval-code!
 (+ 5 5))


(eval-code!
 (ta.tradingview.goldly.interact/add-study
  "Moving Average" [30 "close"]))

(eval-code!
 (ta.tradingview.goldly.interact/add-study 
  "MACD" [14 30 "close" 9]))

(eval-code!
 (ta.tradingview.goldly.interact/add-study 
  "Compare" ["open" "AAPL"]))
;Compare has 2 inputs: [dataSource, symbol]. 
;Supported dataSource values: ["close", "high", "low", "open"].

(eval-code!
 (ta.tradingview.goldly.interact/add-study 
  "CLJMAIN" ["close"]))

(eval-code!
 (tv/add-study "CLJ" ["volume"]))

(eval-code!
 (tv/add-study "CLJ" ["high"]))

(eval-code!
 (ta.tradingview.goldly.interact/study-list))


(eval-code!
 (ta.tradingview.goldly.interact/remove-all-studies))

;widget.activeChart () .getStudyById (id) .setVisible (false);

(eval-code!
 (tv/add-algo-studies [; main plot 
                       {:close "series"}]))
; adding plot  {:close series}  to:  CLJMAIN

(eval-code!
 (tv/add-algo-studies [nil ; no main plot
                       {:volume "column"}]))
; adding plot  {:volume column}  to:  CLJ
; adding col: :volume to:  CLJ

(eval-code!
 (tv/add-algo-studies [{:close "series"} ; main plot 
                       {:volume "column"}]))


