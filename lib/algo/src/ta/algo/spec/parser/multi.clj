(ns ta.algo.spec.parser.multi
  (:require
   [ta.algo.parser.chain :refer [make-chain]]))

(defn- combined?  [[calendar interval]]
  (and (= calendar :*) (= interval :*)))

(defn- barcategory-combined? [{:keys [bar-category]}]
  (combined? bar-category))

;; PARSER

(defn- parse-part [market interval chain]
  (let [calendar [market interval]]
    {:bar-category calendar
     :algo (make-chain chain)
     :type :multi-calendar-chain
     :spec chain}))

(defn parse
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

(comment
  (combined? [:us :m])
  (combined? [:* :*])
  (combined? [:us :*])

  (require '[notebook.algo-config.multicalendar-sma :refer [multi-calendar-algo-demo]])

  (parse multi-calendar-algo-demo)
    ;; => {:opts {:asset "EUR/USD", :feed :fx}, :chain create-meta-algo-part ..
    ;;    ({:bar-category [:us :d], :algo #function[ta.env.chain/make-chain-impl/fn--118841]}create-meta-algo-part ..
    ;;     {:bar-category [:us :h], :algo #function[ta.env.chain/make-chain-impl/fn--118841]})}




 ; 
  )

