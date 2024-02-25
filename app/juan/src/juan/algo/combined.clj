(ns juan.algo.combined
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [tablecloth.api :as tc]
   [juan.algo.spike :refer [spike-signal]]
   [ta.calendar.link :refer [link-bars]]))



(defn daily-intraday-combined [env spec daily-ds intraday-ds]
  (info "intraday-combined: daily# " (tc/row-count daily-ds) "intraday# " (tc/row-count intraday-ds))
  (let [daily-atr (link-bars intraday-ds daily-ds :atr 0.0)
        daily-close (link-bars intraday-ds daily-ds :close 0.0)
        combined-ds (tc/add-columns intraday-ds {:daily-atr daily-atr
                                                 :daily-close daily-close})
        intraday-spike (spike-signal spec combined-ds)]
    (tc/add-columns combined-ds {:spike intraday-spike})))



 