(ns juan.realtime
  (:require
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warn error errorf]]
   [juan.data :refer [settings]]
   [juan.pivot :refer [pivot-trigger]]
   [clojure.edn :as edn]))


(defn pivot-trigger-safe [pivot symbol side price]
  (try 
    (pivot-trigger pivot symbol side price)
    (catch Exception e
      (error "error calculating pivot-triggers for: " symbol)
      nil)))


(defn calc-realtime-symbol [fxcm-last-price {:keys [symbol close atr sentiment-signal pivots] :as core}]
  (let [price (fxcm-last-price symbol)
        change (when (and price close)
                 (- price close))
        spike-atr-min-prct (:spike-atr-min-prct settings)
        change-atr-prct (when (and change atr)
                          (/ (* 100.0 change) atr))
        spike-signal (when (and change-atr-prct spike-atr-min-prct)
                       (cond
                         (> change-atr-prct spike-atr-min-prct) :short
                         (< change-atr-prct (- 0 spike-atr-min-prct)) :long
                         :else false))
        setup-signal (when (and (not (nil? spike-signal))
                                (not (nil? sentiment-signal)))
                       (cond
                         (and (= :long spike-signal) (= :long sentiment-signal)) :long
                         (and (= :short spike-signal) (= :short sentiment-signal)) :short
                         :else false))
        pivot-nearby (when setup-signal
                       (pivot-trigger pivots symbol setup-signal price))

        pivot-long (pivot-trigger-safe pivots symbol :long price)
        pivot-short (pivot-trigger-safe pivots symbol :short price)
        #_{:price 18.654,
           :name :p1-high,
           :diff 0.14602975463867196,
           :pip-diff 14.602975463867196}
        pivot-max-pip-distance (:pivot-max-pip-distance settings)
        pivot-pip-diff (when pivot-nearby
                         (:pip-diff pivot-nearby))
        pivot-signal (when pivot-nearby
                       (if (< pivot-pip-diff pivot-max-pip-distance)
                         setup-signal
                         false))]
    ;(println "pivot pip diff: " pivot-pip-diff)
    ;(println "pivot max pip distance: " pivot-max-pip-distance)
    (assoc core
           :price price
           :change change
           :change-atr-prct change-atr-prct
           :spike-signal spike-signal
           :setup-signal setup-signal
           :pivot-nearby pivot-nearby
           :pivot-signal pivot-signal
           :pivot-long pivot-long
           :pivot-short pivot-short)))


(defn calc-realtime [get-core fxcm-last-price]
  (map #(calc-realtime-symbol fxcm-last-price %) (get-core)))


(comment

  (require '[juan.app :refer [get-core fxcm-last-price]])
  (get-core)
  (fxcm-last-price "EURNOK")

  (calc-realtime get-core fxcm-last-price)

  (require '[clojure.pprint :refer [print-table]])

  (->> (calc-realtime get-core fxcm-last-price)
       (print-table [:symbol :close :change-atr-prct
                     :spike-signal :sentiment-signal :setup-signal :pivot-signal]))




 ; 
  )


