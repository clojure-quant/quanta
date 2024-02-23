(ns ta.algo.parser.chain
  "executes a chained algo (similar to a threading macro,
   but passes in also env and opts). It is useful to 
   easily create calendar based calculations. Functions
   in the chain can be functions, or fully qualified
   symbols
   
   Benefits of chain:
   - It works like a threading macro, where the result is chained through
     multiple functions.
   - All fns get access to env and opts. (We do (partial env opts) on all fns.) 
   - supports symbols (so no requires)

   TODO:
   - It would be good to give all fns a subset of the opts; a opts-getter.
   ")
   

(defn- execute-next [env opts result f]
  (f env opts result))

(defn- make-chain-impl [chain-opts v]
  (fn [env opts time]
    (let [opts (merge opts chain-opts)]
      (reduce (partial execute-next env opts)
              time v))))

(defn- preprocess-fun [fun]
  (if (symbol? fun)
    (requiring-resolve fun)
    fun))

(defn make-chain
  "env - the environment (live/simulation)
   opts - opts for the algo chain
   v - a vector of functions that are threaded through;
       optionally can have a options map as the first 
       element in the vector
   returns a function that expects [env opts time/result]"
  [v]
  (println "make-chain: " v)
  (if (symbol? v)
    (preprocess-fun v)
    (let [has-opts (map? (first v))
          opts (if has-opts (first v) {})
          chain (if has-opts (rest v) v)
          chain (map preprocess-fun chain)]
      (make-chain-impl opts chain))))

(comment

  (defn store-time [_env _opts time]
    {:time time})

  (defn store-secret [_env opts result]
    (assoc result :big-question (:secret opts)))

  (defn store-opts [_env opts result]
    (assoc result :opts opts))

  (def chain-demo
    [{:secret 42
      :sma 30}
     store-time
     store-secret
     'ta.algo.parser.chain/store-opts])

  (def time-fun
    (make-chain chain-demo))

  (time-fun nil {:asset "EUR/USD"} :now)
  ;; => {:time :now, :big-question 42, :opts {:asset "EUR/USD", :secret 42, :sma 30}}

  (def chain-simple 
    'ta.algo.parser.chain/store-time)
  
  (def simple-fun
     (make-chain chain-simple))

  (simple-fun nil {:asset "EUR/USD"} :future)



;  
  )

