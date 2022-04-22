(ns demo.goldly.repl.study
  (:require
   [goldly.scratchpad :refer [eval-code!]]))

(eval-code!
 (+ 5 5))

(eval-code!
 (tv/add-study "MACD" [14 30 "close" 9]))

(eval-code!
 (tv/add-study "Compare" ["open" "AAPL"]))
;Compare study has 2 inputs: [dataSource, symbol]. 
;Supported dataSource values are: ["close", "high", "low", "open"].

(eval-code!
 (tv/add-study "CLJMAIN" ["close"]))

(eval-code!
 (tv/add-study "CLJ" ["volume"]))

(eval-code!
 (tv/add-study "CLJ" ["high"]))

(eval-code!
 (study-list))

(eval-code!
 (tv/remove-all-studies))

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


