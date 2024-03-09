(ns juan.algo.combined
  (:require
    [de.otto.nom.core :as nom]
   [taoensso.timbre :refer [trace debug info warn error]]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [ta.calendar.link :refer [link-bars2]]
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

(defn daily-intraday-combined-impl [env spec daily-ds intraday-ds]
  (info "intraday-combined: daily# " (tc/row-count daily-ds) "intraday# " (tc/row-count intraday-ds))
  (let [pivot-max-diff (:pivot-max-diff spec)
        _ (assert pivot-max-diff "intraday-combined needs :max-diff for pivot calculation")
        daily-atr (link-bars2 intraday-ds daily-ds :atr-band-atr 0.0)
        daily-atr-mid (link-bars2 intraday-ds daily-ds :atr-band-mid 0.0)
        daily-atr-upper (link-bars2 intraday-ds daily-ds :atr-band-upper  0.0)
        daily-atr-lower (link-bars2 intraday-ds daily-ds :atr-band-lower  0.0)
        daily-pivots (link-bars2 intraday-ds daily-ds :pivots-price nil)
        ;_ (info "calculating pivot nr..")
        daily-pivotnr (link-bars2 intraday-ds daily-ds :ppivotnr 0)
        ;_ (info "calculating date ..")
        ;daily-date (link-bars2 intraday-ds daily-ds :date (-> (t/now) t/instant))
        ;daily-date (link-date intraday-ds daily-ds)
        ;_ (info "calculating date .. finished!")
        ;_ (info "daily-date: " daily-date)
        ;_ (info "daily-atr: " daily-atr)
        ;_ (info "daily-close: " daily-close)
        ;_ (info "daily-close #: " (count daily-close))
        combined-ds (tc/add-columns intraday-ds {:daily-atr daily-atr
                                                 :daily-atr-mid daily-atr-mid
                                                 :daily-atr-upper daily-atr-upper
                                                 :daily-atr-lower daily-atr-lower
                                                 :daily-pivots daily-pivots
                                                 :daily-pivotnr daily-pivotnr
                                                 ;:daily-date daily-date
                                                 })
        intraday-spike (spike-signal combined-ds)
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
    (cond 
      (nom/anomaly? daily-ds)
      daily-ds

      (nom/anomaly? intraday-ds)
      intraday-ds

      (nil? daily-ds)
      (nom/fail ::algo-calc {:message "daily-ds is nil!"
                             :location :juan-algo-combined
                             :spec spec})
      
       (nil? intraday-ds)
       (nom/fail ::algo-calc {:message "intraday-ds is nil!"
                       :location :juan-algo-combined
                       :spec spec})
       :else 
       (daily-intraday-combined-impl env spec daily-ds intraday-ds)    )
    (catch Exception ex
      (error "exception in juan/combined spec: " spec ex)
      (nom/fail ::algo-calc {:message "algo calc exception!"
                             :location :juan-algo-combined
                             :spec spec}))))
  

 