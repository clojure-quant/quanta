(ns ta.env.dsl.multi-calendar
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [manifold.stream :as s]
   [ta.algo.spec.parser.chain :refer [make-chain]]
   [ta.algo.spec.parser.multi :as parser]
   [ta.env.live-bargenerator :as live-env]
   [ta.env.tools.stream-combiner :refer [stream-combiner]]))

;; COMBINER

(defn- add-algo-combiner [env v ids]
  (info "adding algo-combiner for algo ids: " ids "chain:" v)
  (let [chain-fn (make-chain v)
        rstreams (map #(live-env/get-algo-result-stream env %) ids)
        result-s (apply stream-combiner chain-fn rstreams)
        id (live-env/register-algo env chain-fn {:input ids})]
    (s/consume #(live-env/publish-algo-result env id %)
               result-s)
    id))


;; CREATE

(defn- create-part [opts {:keys [algo bar-category spec type]}]
  {:algo-opts (assoc opts :bar-category bar-category)
   :algo algo
   :type type
   :spec spec})

(defn- create [v]
  (let [{:keys [opts chain combined]} (parser/parse v)]
    {:calendar-algos (map #(create-part opts %) chain)
     :combined-algo combined}))

;; ADD

(defn add [env v]
  (let [{:keys [calendar-algos combined-algo]} (create v)
        ids (map #(live-env/add env %) calendar-algos)
        id-combined (when combined-algo
                      (add-algo-combiner env (:spec combined-algo) ids))
        ids (if id-combined
              (conj ids id-combined)
              ids)]
    ids))




(comment
 
  (create multi-calendar-algo-demo)
  ;; => ({:algo-opts {:asset "EUR/USD", :feed :fx, :bar-category [:us :d]},
  ;;      :algo #function[ta.env.chain/make-chain-impl/fn--118841]}
  ;;     {:algo-opts {:asset "EUR/USD", :feed :fx, :bar-category [:us :h]},
  ;;      :algo #function[ta.env.chain/make-chain-impl/fn--118841]})


  (require '[modular.system])
  (def live (modular.system/system :live))
  live

  (def algo1 (-> (create multi-calendar-algo-demo) first))
  algo1
  (live-env/add live algo1)
  ;; "fg2Lhq"

  (def algo2 (-> (create multi-calendar-algo-demo) second))
  algo2
  (live-env/add live algo2)
  ;; "64CLvN"


   ; Env (assoc env :results (last-msg-store))
      ;id (live-env/add env data)
       ;result-stream (env/result-stream id)
       ;store (Create-Last-msg-storage resultstream)

;  
  )


                    
