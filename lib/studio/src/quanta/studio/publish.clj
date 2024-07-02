(ns quanta.studio.publish
  (:require
   [de.otto.nom.core :as nom]
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warn error errorf]]
   [modular.ws.core :refer [send-all!]]
   [ta.viz.error :refer [error-render-spec]]))

;; result fns 

(defn push-viz-result [websocket template-id task-id result]
  (try
    (let [error? (nom/anomaly? result)
          result (if error?
                   (error-render-spec result)
                   result)]
      (info "pushing viz-result template-id: " template-id " task-id: " task-id (if error? " anomaly!" " success"))
      (send-all! websocket [:interact/subscription {:task-id task-id :result result}]))
    (catch Exception ex
      (error "push-viz-result exception: " ex)))
  ; make sure we never retrun something. result ends up in a javelin cell
  nil)