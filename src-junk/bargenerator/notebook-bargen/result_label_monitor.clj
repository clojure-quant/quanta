(ns notebook.playground.live.result-label-monitor
  (:require
   [modular.system]
   [ta.env.tools.label-monitor :refer [monitor-label snapshot]]
   [ta.algo.ds :refer [last-ds-row]]))
 

   ; 1. connect to result monitor
(def monitor (:label-monitor modular.system/system))
monitor

; 2. subscribe to label with result transformer
(monitor-label monitor :sma-crossover-1m last-ds-row)


; 3. get current transformed result.
; this could take a minute
(snapshot monitor :sma-crossover-1m)


(monitor-label monitor :dummy :result)


; 3. get current transformed result.
; this could take a minute
(snapshot monitor :dummy)








