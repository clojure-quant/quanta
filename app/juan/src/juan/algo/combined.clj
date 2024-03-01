(ns juan.algo.combined
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [ta.calendar.link :refer [link-bars link-bars2]]
   [ta.algo.ds :refer [all-positions-agree-ds]]
   [ta.trade.signal2 :refer [signal-keyword->signal-double]]
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


(defn daily-intraday-combined-impl [env spec daily-ds intraday-ds]
  (info "intraday-combined: daily# " (tc/row-count daily-ds) "intraday# " (tc/row-count intraday-ds))
  (let [pivot-max-diff (:pivot-max-diff spec)
        _ (assert pivot-max-diff "intraday-combined needs :max-diff for pivot calculation")
        daily-atr (link-bars2 intraday-ds daily-ds :atr 0.0)
        daily-close (link-bars2 intraday-ds daily-ds :close 0.0)
        daily-pivots (link-bars2 intraday-ds daily-ds :pivots-price nil)
        ;_ (info "calculating pivot nr..")
        daily-pivotnr (link-bars2 intraday-ds daily-ds :ppivotnr 0)
        ;_ (info "calculating date ..")
        ;daily-date (link-bars intraday-ds daily-ds :date (-> (t/now) t/instant))
        ;daily-date (link-date intraday-ds daily-ds)
        ;_ (info "calculating date .. finished!")
        ;_ (info "daily-date: " daily-date)
        ;_ (info "daily-atr: " daily-atr)
        ;_ (info "daily-close: " daily-close)
        ;_ (info "daily-close #: " (count daily-close))
        combined-ds (tc/add-columns intraday-ds {:daily-atr daily-atr
                                                 :daily-close daily-close
                                                 :daily-pivots daily-pivots
                                                 :daily-pivotnr daily-pivotnr
                                                 ;:daily-date daily-date
                                                 })
        intraday-spike (spike-signal spec combined-ds)
        spike-doji (all-positions-agree-ds [(:doji intraday-ds) intraday-spike])
        ]
    (tc/add-columns combined-ds {:spike intraday-spike
                                 :spike-doji spike-doji
                                 :spike-v (signal-keyword->signal-double intraday-spike)
                                 :doji-v (signal-keyword->signal-double (:doji intraday-ds))
                                 :spike-doji-v (signal-keyword->signal-double spike-doji)
                                 :long (long-pivots pivot-max-diff daily-pivots (:close intraday-ds))
                                 :short (short-pivots pivot-max-diff daily-pivots (:close intraday-ds))})))


(defn daily-intraday-combined [env spec daily-ds intraday-ds]
  (try 
    (if (and daily-ds intraday-ds)
       (daily-intraday-combined-impl env spec daily-ds intraday-ds)    
       (do (when-not daily-ds (error "juan-fx formula has no daily-ds input."))
           (when-not intraday-ds (error "juan-fx formula has no intraday-ds input."))
           nil))
    (catch Exception ex
      (error "exception in juan/combined spec: " spec ex)
      nil)))
  

 