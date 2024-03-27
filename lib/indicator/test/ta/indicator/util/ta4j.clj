(ns ta.indicator.util.ta4j
  (:require 
    [ta.indicator.ta4j.ta4j :as ta4j]))

(defn get-constructor [ind-kw]
  (if (namespace ind-kw) 
    (partial ta4j/ind-helper  (namespace ind-kw))
    ta4j/ind))

(defn get-name [ind-kw]
  (if (namespace ind-kw) 
    (-> ind-kw name keyword)
    ind-kw))

(defn calc-indicator [data ind-kw args]
  (let [ind (get-constructor ind-kw)
        ind-kw (get-name ind-kw)
        ;_ (println "ind-kw: " ind-kw)
        result (if (seq? args)
               (apply ind ind-kw data args)
               (ind ind-kw data))]
    (ta4j/ind-values result)))


(defn close [ds ind-kw & args]
  (let [close (ta4j/ds->ta4j-close ds)]
    (calc-indicator close ind-kw args)))

(defn bar [ds ind-kw & args]
  (let [bar (ta4j/ds->ta4j-ohlcv ds)]
    (calc-indicator bar ind-kw args)))


(comment 
  (require '[ta.indicator.util.data :refer [ds]])

  (namespace :ATR)
  (namespace :helpers/TR)
  (-> :helpers/TR name keyword)

  (close ds :SMA 5)
  (bar ds :ATR 5)
  (bar ds :helpers/TR)
  (bar ds :bollinger/TR)
 
;  
  )


