(ns juan.algo.combined
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [ta.calendar.link :refer [link-bars]]
   [juan.algo.spike :refer [spike-signal]]
   [juan.algo.pivot-price-nearby :refer [nearby-pivots]]
   ))

(defn long-pivot [max-diff pivot-ds close]
  (-> (nearby-pivots max-diff pivot-ds close)
      :pivot-long))

(defn long-pivots [max-diff daily-pivots intraday-close]
  (dtype/emap (partial long-pivot max-diff) :object daily-pivots intraday-close))

(defn short-pivot [max-diff pivot-ds close]
  (-> (nearby-pivots max-diff pivot-ds close)
      :pivot-short))

(defn short-pivots [max-diff daily-pivots intraday-close]
  (dtype/emap (partial short-pivot max-diff) :object daily-pivots intraday-close))


(defn daily-intraday-combined [env spec daily-ds intraday-ds]
  (info "intraday-combined: daily# " (tc/row-count daily-ds) "intraday# " (tc/row-count intraday-ds))
  (let [pivot-max-diff (:pivot-max-diff spec)
        _ (assert pivot-max-diff "intraday-combined needs :max-diff for pivot calculation")
        daily-atr (link-bars intraday-ds daily-ds :atr 0.0)
        daily-close (link-bars intraday-ds daily-ds :close 0.0)
        daily-pivots (link-bars intraday-ds daily-ds :pivots-price nil)
        daily-pivotnr (link-bars intraday-ds daily-ds :ppivotnr 0) 
        combined-ds (tc/add-columns intraday-ds {:daily-atr daily-atr
                                                 :daily-close daily-close
                                                 :daily-pivots daily-pivots
                                                 :daily-pivotnr daily-pivotnr})
        intraday-spike (spike-signal spec combined-ds)]
    (tc/add-columns combined-ds {:spike intraday-spike
                                 :long (long-pivots pivot-max-diff daily-pivots (:close intraday-ds))
                                 :short (short-pivots pivot-max-diff daily-pivots (:close intraday-ds))
                                 })))



 