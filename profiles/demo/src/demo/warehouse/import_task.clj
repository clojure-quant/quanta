(ns demo.warehouse.import-task
  (:require
   [taoensso.timbre :refer [trace debug info infof warn error]]
   [demo.warehouse.import-bybit :as bimp]
   [webly.log]))

(webly.log/timbre-config!
 {:timbre-loglevel
  [[#{"pinkgorilla.nrepl.client.connection"} :info]
   [#{"org.eclipse.jetty.*"} :info]
   [#{"webly.*"} :info]
   [#{"*"} :info]]})

(defn task-bybit-import-initial [& _]
  (bimp/init-all-daily)
  (bimp/init-all-15))

(defn task-bybit-import-append [& _]
  (bimp/append-all-daily)
  (bimp/append-all-15))