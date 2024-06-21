(ns ta.algo.spec.parser.chain
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
   "
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [de.otto.nom.core :as nom]
   [ta.algo.compile :refer [compile-symbol]]))

(defn- execute-next [env opts result f]
  (f env opts result))

(defn- make-chain-impl [chain-opts v]
  (fn [env opts time]
    (let [opts (merge opts chain-opts)]
      (reduce (partial execute-next env opts)
              time v))))

(defn first-anomaly
  "returns the first anomaly in a chain or false"
  [chain]
  (reduce (fn [r fun]
            (cond
              (nom/anomaly? r) r
              (nom/anomaly? fun) fun
              :else r))
          false chain))

(defn make-chain
  "v - a vector of functions that are threaded through;
       optionally can have a options map as the first 
       element in the vector
   returns a function that expects [env opts time/result]
   on compile error returns an anomaly."
  [v]
  (info "make-chain: " v)
  (if (symbol? v)
    (compile-symbol v)
    (let [has-opts (map? (first v))
          opts (if has-opts (first v) {})
          chain (if has-opts (rest v) v)
          chain (map compile-symbol chain)
          err (first-anomaly chain)]
      (if err
        err
        (make-chain-impl opts chain)))))

