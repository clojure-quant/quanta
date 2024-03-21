(ns ta.tradingview.goldly.algo.interaction
  (:require
   [promesa.core :as p]
   [goldly.service.core :refer [clj]]
   [ta.tradingview.goldly.algo.context :as c]
   [ta.tradingview.goldly.algo.indicator :as ind]
   [ta.tradingview.goldly.interact2 :as i]))

;; ALGO INDICATORS

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
                    (reset! current-indicator-ids entity-ids)))))))

(defn remove-indicators [_algo-ctx tv]
  (let [entity-ids @current-indicator-ids]
    (println "remove-indicators entity-ids: " entity-ids)
    (reset! current-indicator-ids [])
    (doall
     (map
      #(i/remove-entity tv %)
      entity-ids))))

;; SHAPES

(def current-shape-ids (atom []))

(defn remove-shapes [tv]
  (let [entity-ids @current-shape-ids]
    (println "remove-shape entity-ids: " entity-ids)
    (reset! current-shape-ids [])
    (doall
     (map
      #(i/remove-entity tv %)
      entity-ids))))

(defn add-shapes [tv shapes]
  (println "add-shapes # " (count shapes))
  (remove-shapes tv)
  (let [entity-ids (doall
                    (map #(i/add-shape tv (:points %) (:shape %)) shapes))]
    (println "shape ids: " entity-ids)
    (reset! current-shape-ids entity-ids)))

(defn get-shapes-for-window [algo-ctx tv epoch-start epoch-end]
  (println "get-shapes-for-window .. " epoch-start " - " epoch-end)
  (let [{:keys [algo opts]} (c/get-algo-input algo-ctx)
        {:keys [symbol frequency]} opts
        _ (println "get-algo-shapes algo: " algo "symbol: " symbol)
        rp (clj 'ta.algo.manager/algo-shapes algo opts epoch-start epoch-end)]
    (-> rp
        (p/then (fn [shapes]
                  (println "get-algo-shapes :received " (count shapes) " shapes")
                  (add-shapes tv shapes)
                  nil))
        (p/catch (fn [r]
                   (println "get-algo-shapes exception: " r)
                   nil)))))

(defn get-shapes-visible-window [algo-ctx tv]
  (println "get-shapes-visible window ..")
  (let [{:keys [from to]} (i/get-range tv)]
    (println "from: " from " to: " to)
    (get-shapes-for-window algo-ctx tv from to)))

;; AFTER LOAD FINISHED

(defonce after-load-finished-fn (atom nil))

(defn add-indicators-after-load [algo-ctx tv]
  (println "add-indicators-after-load..")
  (let [fun (fn []
              (add-indicators algo-ctx tv))]
    (reset! after-load-finished-fn fun)))

(defn on-load-finished [algo-ctx tv modus opts]
  (println "on-load-finished: modus:" modus " opts: " opts)
  ; indicators
  (if-let [fun @after-load-finished-fn]
    (do (println "after-load-add-indicators ..")
        (reset! after-load-finished-fn nil)
        (fun))
    (println "on-load-finished: no need to add indicators."))
  ; shapes
  (get-shapes-visible-window algo-ctx tv)
  nil)

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
      (remove-shapes tv)
      (add-indicators-after-load algo-ctx tv))

    (when symbol-changed
      (println "symbol changed to: " symbol)
      (remove-shapes tv))

    (when (or symbol-changed algo-changed)
      (println "resetting algo-ctx data..")
      (c/set-algo-data algo-ctx nil)
       ;(i/reset-data tv)
       ;(c/set-cache-needed)
      (println "switching tv symbol..")
      (i/set-symbol tv symbol "1D" #_frequency (fn [modus opts]
                                                 (println "ON-SET-SYMBOL FINISHED: modus: " modus " opts: " opts)
                                                 (let [modus nil
                                                       opts nil]
                                                   (on-load-finished algo-ctx tv modus opts)))))

    nil))

(defn track-interactions [algo-ctx tv]
  (let [input (c/get-algo-input-atom algo-ctx)]
    (println "add-watch to algo-ctx input ..")
    (add-watch input :algo-input
               (fn [key state old-value new-value]
                 (println "algo-ctx input changed to:" new-value)
                 (on-input-change algo-ctx tv old-value new-value)))))