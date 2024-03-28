(ns ta.indicator.util.ta4j
  (:require 
    [ta.indicator.ta4j.ta4j :as ta4j]))


(defn calc-indicator [ind-kw data args]
  (let [args (conj args data)
        _ (println "calc indicator " ind-kw " args: " (count args) (rest args))
        ind (ta4j/indicator ind-kw args)]
    (ta4j/ind-values ind)))

(defn close [ds ind-kw & args]
  (let [close (ta4j/ds->ta4j-close ds)]
    (calc-indicator ind-kw close args)))

(defn bar [ds ind-kw & args]
  (let [bar (ta4j/ds->ta4j-ohlcv ds)]
    (calc-indicator ind-kw bar args)))

;; bool indicator

(defn calc-indicator-bool [ind-kw data args]
  (let [args (conj args data)
        _ (println "calc indicator " ind-kw " args: " (count args) (rest args))
        ind (ta4j/indicator ind-kw args)]
    (ta4j/ind-values-bool ind)))

(defn bar-bool [ds ind-kw & args]
  (let [bar (ta4j/ds->ta4j-ohlcv ds)]
    (calc-indicator-bool ind-kw bar args)))


; facade

(defn- get-values-for [o name]
  (-> (ta4j/get-data o name '())
      (ta4j/ind-values)))

(defn- get-values-for-tuple [o method-name]
  (let [sname (if (keyword? method-name) (name method-name) method-name)]
    [method-name (get-values-for o sname)]))

(defn get-values-facade [o names]
  (let [series (map #(get-values-for-tuple o %) names)]
    (into {} series)))

(defn calc-facade [ind-kw cols data args]
  (let [args (conj args data)
        _ (println "calc facade " ind-kw " args: " (count args) (rest args))
        ind (ta4j/facade ind-kw args)]
    ;ind
    (get-values-facade ind cols)
    ))

(defn facade-bar [ds ind-kw cols & args]
  (let [bar (ta4j/ds->ta4j-ohlcv ds)]
    (calc-facade ind-kw cols bar args)))


(comment 
  (require '[ta.indicator.util.data :refer [ds]])

  (ta4j/ds->ta4j-ohlcv ds)
  (ta4j/ds->ta4j-close ds)

  (close ds :SMA 5)
  (bar ds :ATR 5)
  (bar ds :helpers/TR)
  
  (facade-bar ds :bollinger/BollingerBand [:middle :lower :upper] 20 2)

;  
  )


