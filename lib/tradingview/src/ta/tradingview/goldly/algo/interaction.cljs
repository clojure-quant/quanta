(ns ta.tradingview.goldly.algo.interaction
  (:require
   [ta.tradingview.goldly.algo.context :as c]
   [ta.tradingview.goldly.interact2 :as i]
   ))

(defn on-load-finished [& args]
  (println "on-load-finished: " args))

(defn symbol-changed? [old-value new-value]
  (let [old (get-in  old-value [:opts :symbol])
        new (get-in  new-value [:opts :symbol])]
    (println "symbol old: " old " new: " new)
    (if (= old new) 
      false
      true)))

(defn algo-changed? [old-value new-value]
  (let [old (get-in  old-value [:algo])
        new (get-in  new-value [:algo])]
    (println "algo old: " old " new: " new)
    (if (= old new)
      false
      true)))

(defn on-input-change [algo-ctx tv old new]
  (let [{:keys [algo opts]} (c/get-algo-input algo-ctx)
        {:keys [symbol frequency]} opts
        symbol-changed (symbol-changed? old new) 
        algo-changed (algo-changed? old new)
        ]
    (when symbol-changed
       (println "symbol changed to: " symbol))
    (when algo-changed
      (println "algo changed to: " algo))

    (when (or symbol-changed algo-changed)
       (println "resetting algo-ctx data..")
       (c/set-algo-data algo-ctx nil)  
       (println "switching tv symbol..")
       (i/set-symbol tv symbol frequency on-load-finished))
    
    nil))

(defn track-interactions [algo-ctx tv]
  (let [input (c/get-algo-input-atom algo-ctx)]
    (println "add-watch to algo-ctx input ..")
    (add-watch input :algo-input
                (fn [key state old-value new-value]
                  (println "algo-ctx input changed to:" new-value)
                  (on-input-change algo-ctx tv old-value new-value))
                  )))