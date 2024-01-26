(ns notebook.live.result-printer
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [manifold.stream :as s]
   [ta.env.live-bargenerator :as env]))

(defn print-result [result]
  (info ">>> result: " result))

(defn start-print-results [live]
  (s/consume print-result (env/get-result-stream live)))


(comment 
  (require '[modular.system])
  (def live (:live modular.system/system))
  live

  (start-print-results live)
  
;  
  )




