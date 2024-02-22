(ns juan.algo.sentiment
  (:require
   [taoensso.timbre :refer [info warn error]]
   [juan.download.sentiment :refer [sentiment-dict]]
   [juan.data :refer [settings instruments]]))

(defn calc-sentiment [get-sentiment asset]
  (if-let [s (get-sentiment asset)]
    (let [long-prct (:long-prct s)
          sentiment-treshold (:sentiment-treshold settings)
          short-sentiment (when long-prct (>= long-prct sentiment-treshold))
          short-prct (- 100 long-prct)
          long-sentiment (when long-prct (>= short-prct sentiment-treshold))]
      {:sentiment-long-prct long-prct
       :sentiment-signal (cond
                           short-sentiment :short
                           long-sentiment :long
                           long-prct false
                           :else nil)})
    {:sentiment-long-prct "XXX"
     :sentiment-signal nil}))
