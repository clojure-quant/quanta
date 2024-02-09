(ns ta.env.dsl.multi-calendar
  (:require 
   [ta.env.dsl.chain :refer [make-chain]]
   [ta.env.live-bargenerator :as live-env]))

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
  (println "create-meta-algo ..")
  (let [f (first v)
        f? (map? f)
        _ (println "f: " f " f?: " f?)
        opts (if f? f {})
        v' (if f? (rest v) v)
        params (partition 3 v')]
    {:opts opts
     :chain (map (fn [[market interval chain]]
                   (parse-part market interval chain))
                 params)}))


(defn- create-part [opts {:keys [algo bar-category spec type]}]
   {:algo-opts (assoc opts :bar-category bar-category)
    :algo algo
    :type type
    :spec spec
    })

(defn- create [v]
  (let [{:keys [opts chain]} (parse v)] 
    (map #(create-part opts %) chain)))


(defn add [env v]
  (let [parts (create v)
        ids (map #(live-env/add env %) parts)]
    ids))




(comment
  (require '[tablecloth.api :as tc])
  
  (defn get-all-results [env opts last-result] 
    (concat @(:results env) last-result))
  
  (defn get-current-position [env opts all-ds-position]
    (map (comp :position tc/last) all-ds-position))
  
  (defn all-positions-agree [env opts all-position]
    (apply = all-position))
  

  (require '[notebook.bot.multicalendar-sma :refer [multi-calendar-algo-demo]])
  
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


                    
