(ns notebook.algo-config.quote-monitor
  (:require
   [modular.system]
   [tablecloth.api :as tc]
   [ta.algo.env.protocol :as algo]
   [ta.live.quote-manager :as qm]))

(def q (modular.system/system :quote-manager))

(defn get-quote-snapshot [_env _opts dt]
  (-> (qm/quote-snapshot q)
      (tc/dataset)))

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
 
(defn create-quote-monitor [env _]
   (algo/add-algo env algo-spec))
    


(comment
  
  (get-quote-snapshot nil nil nil)
  
  (require '[modular.system])
  (def env (modular.system/system :live))

  (create-quote-monitor env nil)
 ; 
  )