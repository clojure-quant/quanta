(ns ta.tradingview.goldly.algo.interaction
  (:require
   [ta.tradingview.goldly.algo.context :as c]
   [ta.tradingview.goldly.interact2 :as i]
   ))

(defn symbol-changed? [old-value new-value]
  (let [old (get-in  old-value [:opts :symbol])
        new (get-in  new-value [:opts :symbol])]
    (println "symbol old: " old " new: " new)
    (if (= old new) 
      false
      true)))

(defn on-load-finished [& args]
  (println "on-load-finished: " args)
  )

(defn on-symbol-change [algo-ctx tv s] 
  (println "symbol changed to: " s)
  (let [{:keys [algo opts]} (c/get-algo-input algo-ctx)
        {:keys [symbol frequency]} opts]
    (c/set-algo-data algo-ctx nil)
    (i/set-symbol tv s frequency on-load-finished) 
    nil))

(defn track-interactions [algo-ctx tv]
  (let [input (c/get-algo-input-atom algo-ctx)]
    (println "add-watch to algo-ctx input ..")
    (add-watch input :algo-input
                (fn [key state old-value new-value]
                  (println "algo-ctx input changed to:" new-value)
                  (when (symbol-changed? old-value new-value)
                     (on-symbol-change algo-ctx tv (get-in new-value [:opts :symbol])))
                  
                  ))))