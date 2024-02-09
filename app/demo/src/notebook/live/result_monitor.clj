(ns notebook.live.result-monitor
  (:require
   [modular.system]
   [ta.env.tools.result-monitor :refer [monitor-topic snapshot last-ds-row]]
   ))
 

   ; 1. connnect to result monitor
(def monitor (:result-monitor modular.system/system))
monitor

; 2. subscribe to topic with result transformer
(monitor-topic monitor :sma-crossover-1m last-ds-row)

; 3. get current transformed result.
; this could take a minute
(snapshot monitor :sma-crossover-1m)




