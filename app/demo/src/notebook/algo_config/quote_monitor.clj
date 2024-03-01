(ns notebook.algo-config.quote-monitor
  (:require
   [taoensso.timbre :refer [info warn error]]
   [modular.system]
   [tablecloth.api :as tc]
   [ta.algo.env.protocol :as algo]
   [ta.live.quote-manager :as qm]))

(defn get-quote-snapshot [_env _opts dt]
  (try
    (when-let [q (modular.system/system :quote-manager)]
      (when-let [quotes (qm/quote-snapshot q)]
        (when-not (empty? quotes)
          (tc/dataset quotes))))
    (catch Exception ex
      (error "exception get-quote-snapshot!")
      nil)))

(def algo-spec
  {:type :time
   :calendar [:crypto :m]
   :topic :admin-quote-monitor
   :class "table-head-fixed padding-sm table-red table-striped table-hover"
   :style {:width "50vw"
           :height "40vh"
           :border "3px solid green"}
   :columns [{:path :feed}
             {:path :asset}
             {:path :price}
             {:path :size}]
   :algo ['notebook.algo-config.quote-monitor/get-quote-snapshot
          'ta.viz.publish/publish-ds->table]})

(defn create-quote-monitor [algo-env & args]
  (try 
  (algo/add-algo algo-env algo-spec)
   (catch Exception ex
     (error "exception in adding quote monitor: " ex)
     
     )  
    )
  )



(comment

  (get-quote-snapshot nil nil nil)

  (require '[modular.system])
  (def env (modular.system/system :live))

  (create-quote-monitor env nil)
 ; 
  )