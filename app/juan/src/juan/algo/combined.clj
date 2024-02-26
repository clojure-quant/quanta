(ns juan.algo.combined
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [ta.calendar.link :refer [link-bars]]
   [juan.algo.spike :refer [spike-signal]]
   [juan.algo.pivot-price-nearby :refer [nearby-pivots]]))

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

(defn link-date [intraday-ds daily-ds]
  (try
    (let [c (link-bars intraday-ds daily-ds :date (-> (t/now) t/inst))]
      (info "date-cols: " c)
      )

    (catch Exception ex
      (error "exception in linking bar-dates")
      (error ex)
      nil)))


(defn daily-intraday-combined [env spec daily-ds intraday-ds]
  (info "intraday-combined: daily# " (tc/row-count daily-ds) "intraday# " (tc/row-count intraday-ds))
  (let [pivot-max-diff (:pivot-max-diff spec)
        _ (assert pivot-max-diff "intraday-combined needs :max-diff for pivot calculation")
        daily-atr (link-bars intraday-ds daily-ds :atr 0.0)
        daily-close (link-bars intraday-ds daily-ds :close nil)
        daily-pivots (link-bars intraday-ds daily-ds :pivots-price nil)
        _ (info "calculating pivot nr..")
        daily-pivotnr (link-bars intraday-ds daily-ds :ppivotnr 0)
        _ (info "calculating date ..")
        ;daily-date (link-bars intraday-ds daily-ds :date (-> (t/now) t/instant))
        ;daily-date (link-date intraday-ds daily-ds)
        ;_ (info "calculating date .. finished!")
        ;_ (info "daily-date: " daily-date)
        combined-ds (tc/add-columns intraday-ds {:daily-atr daily-atr
                                                 :daily-close daily-close
                                                 :daily-pivots daily-pivots
                                                 :daily-pivotnr daily-pivotnr
                                                 ;:daily-date daily-date
                                                 })
        intraday-spike (spike-signal spec combined-ds)]
    (tc/add-columns combined-ds {:spike intraday-spike
                                 :long (long-pivots pivot-max-diff daily-pivots (:close intraday-ds))
                                 :short (short-pivots pivot-max-diff daily-pivots (:close intraday-ds))})))



 