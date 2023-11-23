(ns juan.push
   (:require
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warn error errorf]]
   [clojure.core.async :as async  :refer [<! <!! >! >!! put! chan go go-loop]]
   [modular.ws.core :refer [send! send-all! send-response connected-uids]]
   [juan.app :refer [get-realtime]]
    ))

(defn start-pusher! []
  (go-loop []
    (<! (async/timeout 5000)) ; 5 seconds
    (let [data (get-realtime)]
      (debug "sending juan table: " data)
      (send-all! [:juan/table data]))
    (recur)))


