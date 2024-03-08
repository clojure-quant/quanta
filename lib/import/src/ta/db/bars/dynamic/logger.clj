(ns ta.db.bars.dynamic.logger
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]))

(def log-a (atom []))


(defn import-on-demand [opts window tasks]
  (when (not (empty? tasks))
  (let [req (merge (select-keys opts [:asset :import :calendar])
                   (select-keys window [:start :end]))
        req (assoc req :tasks tasks)]
    (swap! log-a conj req))))


(comment 
  @log-a
  
  
  
  ;
  )
