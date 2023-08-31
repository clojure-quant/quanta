(ns ta.tradingview.goldly.algo.interaction
  (:require
   [promesa.core :as p]
   [ta.tradingview.goldly.algo.context :as c]
   [ta.tradingview.goldly.algo.indicator :as ind]
   [ta.tradingview.goldly.interact2 :as i]))

(def current-indicator-ids (atom []))

(defn add-indicators [algo-ctx tv]
   (let [chart-spec (c/get-chart-spec algo-ctx)
         algo-name (c/get-algo-name algo-ctx) 
         studies (ind/get-indicator-names algo-name chart-spec)
         empty-arg (clj->js [])]
    (println "add-indicators studies: " studies)
    (let [load-ps (doall
                    (map
                      #(i/add-study tv % empty-arg)
                      studies))]
      (-> (p/all load-ps)
          (p/then (fn [entity-ids]
                    (println "add-indicator results: " entity-ids)
                    ;[Wu3CAo J2aYUI]
                    (reset! current-indicator-ids entity-ids)
                    )))
      
      )))

(defn remove-indicators [_algo-ctx tv]
  (let [entity-ids @current-indicator-ids]
    (println "remove-indicators entity-ids: " entity-ids)
    (reset! current-indicator-ids [])
    (doall
     (map
      #(i/remove-entity tv %)
      entity-ids))))

(defonce after-load-finished-fn (atom nil))

(defn add-indicators-after-load [algo-ctx tv]
  (println "add-indicators-after-load..")
  (let [fun (fn []
              (add-indicators algo-ctx tv))]
    (reset! after-load-finished-fn fun)))

(defn on-load-finished [& args]
  (println "on-load-finished: " args)
  (if-let [fun @after-load-finished-fn]
    (do (println "after-load-add-indicators ..")
        (reset! after-load-finished-fn nil)
        (fun))
    (println "on-load-finished: no need to add indicators.")
    ))

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
        algo-changed (algo-changed? old new)]
    
    (when algo-changed
       (println "algo changed to: " algo)
       (remove-indicators algo-ctx tv)
       (add-indicators-after-load algo-ctx tv))

    (when symbol-changed
       (println "symbol changed to: " symbol))

    (when (or symbol-changed algo-changed)
       (println "resetting algo-ctx data..")
       (c/set-algo-data algo-ctx nil)  
       (i/reset-data tv)
       (c/set-cache-needed)
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