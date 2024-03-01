(ns notebook.algo-config.tickerplant-monitor
  (:require
   [taoensso.timbre :refer [info warn error]]
   [modular.system]
   [tablecloth.api :as tc]
   [ta.algo.env.protocol :as algo]
   [ta.live.tickerplant :refer [current-bars]]))

(defn get-bars-calendar [calendar]
  (let [t (modular.system/system :tickerplant)
        [exchange interval] calendar]
    (when t
      (->> (current-bars t calendar)
           (map #(assoc % :exchange exchange :interval interval))))))

(defn get-tickerplant-status [_env _opts dt]
  (try 
  (let [forex (get-bars-calendar [:forex :m])
        crypto (get-bars-calendar [:crypto :m])]
    (when (and forex crypto)
          (tc/dataset (concat forex crypto))
          ))
    (catch Exception ex 
      (error "excepting in get-tickerplant-status")
      nil)))

(def algo-spec
  {:type :time
   :calendar [:crypto :m]
   :topic :admin-tickerplant-monitor
   :class "table-head-fixed padding-sm table-red table-striped table-hover"
   :style {:width "50vw"
           :height "40vh"
           :border "3px solid green"}
   :columns [{:path :exchange}
             {:path :interval}
             {:path :asset}
             {:path :epoch}
             {:path :open}
             {:path :high}
             {:path :low}
             {:path :close}
             {:path :volume}
             {:path :ticks}]
   :algo ['notebook.algo-config.tickerplant-monitor/get-tickerplant-status
          'ta.viz.publish/publish-ds->table]})
 
(defn create-tickerplant-monitor [env & args]
   (algo/add-algo env algo-spec))
    


(comment
  t
(get-tickerplant-status nil nil nil)
  
  (require '[modular.system])
  (def env (modular.system/system :live))

  (create-tickerplant-monitor env nil)
 ; 
  )