(ns ta.tradingview.goldly.helper)


(defn extract-period [period]
  (let [from (.-from period)
        to (.-to period)
        count-back (.-countBack period)
        first-request? (.-firstDataRequest period)]
    {:from from
     :to to
     :count-back count-back
     :first-request? first-request?}))
