(ns juan.algo.combined
  (:require
    [taoensso.timbre :refer [trace debug info warn error]]
    [tablecloth.api :as tc]
    [ta.calendar.link :refer [link-bars]]))



(defn daily-intraday-combined [env spec daily-ds intraday-ds]
  (info "intraday-combined: daily# " (tc/row-count daily-ds) "intraday# " (tc/row-count intraday-ds))
  (let [daily-atr (link-bars intraday-ds daily-ds :atr 0.0)]
     (tc/add-columns intraday-ds {:daily-atr daily-atr})

    )
  )



 