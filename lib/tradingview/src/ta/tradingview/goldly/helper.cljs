(ns ta.tradingview.goldly.helper)

(defn extract-period [period]
  (let [from (.-from period)
        to (.-to period)
        count-back (.-countBack period)
        first-request? (.-firstDataRequest period)
        r {:from from
           :to to
           :count-back count-back
           :first? first-request?}]
    (println "extract-period result: " r)
    (set! (.-tvperiod js/window) period)

    r))
