(ns ta.env.dsl.multi-calendar
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [manifold.stream :as s]
   [ta.env.dsl.chain :refer [make-chain]]
   [ta.env.live-bargenerator :as live-env]
   [ta.env.tools.stream-combiner :refer [stream-combiner]]))

;; COMBINER

(defn- combined?  [[calendar interval]]
  (and (= calendar :*) (= interval :*)))

(defn- barcategory-combined? [{:keys [bar-category]}]
  (combined? bar-category))

(defn- add-algo-combiner [env v ids]
  (info "adding algo-combiner for algo ids: " ids "chain:" v)
  (let [chain-fn (make-chain v)
        rstreams (map #(live-env/get-algo-result-stream env %) ids)
        result-s (apply stream-combiner chain-fn rstreams)
        id (live-env/register-algo env chain-fn {:input ids})]
    (s/consume #(live-env/publish-algo-result env id %)
               result-s)
    id))

;; PARSER

(defn- parse-part [market interval chain]
  (let [calendar [market interval]]
    {:bar-category calendar
     :algo (make-chain chain)
     :type :multi-calendar-chain
     :spec chain}))

(defn- parse
  "parses a multi-calendar definition, and returns a 
   normalized datastructure that can be used to add 
   it to an environment."
  [v]
  ;(println "create-meta-algo ..")
  (let [f (first v)
        f? (map? f)
        ; _ (println "f: " f " f?: " f?)
        opts (if f? f {})
        v' (if f? (rest v) v)
        params (partition 3 v')
        chain (map (fn [[market interval chain]]
                     (parse-part market interval chain))
                   params)
        combined (-> (filter barcategory-combined? chain)
                     first)
        chain (remove barcategory-combined? chain)]
    {:opts opts
     :chain chain
     :combined combined}))


;; CREATE

(defn- create-part [opts {:keys [algo bar-category spec type]}]
  {:algo-opts (assoc opts :bar-category bar-category)
   :algo algo
   :type type
   :spec spec})

(defn- create [v]
  (let [{:keys [opts chain combined]} (parse v)]
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
  (combined? [:us :m])
  (combined? [:* :*])
  (combined? [:us :*])

  (require '[notebook.algo-config.multicalendar-sma :refer [multi-calendar-algo-demo]])

  (parse multi-calendar-algo-demo)
  ;; => {:opts {:asset "EUR/USD", :feed :fx}, :chain create-meta-algo-part ..
  ;;    ({:bar-category [:us :d], :algo #function[ta.env.chain/make-chain-impl/fn--118841]}create-meta-algo-part ..
  ;;     {:bar-category [:us :h], :algo #function[ta.env.chain/make-chain-impl/fn--118841]})}

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


                    
