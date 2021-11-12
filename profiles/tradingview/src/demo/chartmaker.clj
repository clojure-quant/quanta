(ns demo.chartmaker
  (:require
   [demo.env.config] ; side effects
   [reval.persist.edn] ; side-effects
   [ta.tradingview.chartmaker :as cm]))


(defn make-ganns []
  (let [s "MSFT"
        chart-name (str "auto-gann-test " s)
        client-id 77
        user-id 77
        chart-id 100]
    (cm/make-chart client-id user-id chart-id s chart-name
                   [;source-pitchfork
                    (cm/trendline {:symbol s
                                   :a-p 300.20  :a-t 1519223400
                                   :b-p 330.88060448358687  :b-t 1521725400})
                    (cm/gann
                     {:symbol s
                      :a-p 1517.0  :a-t 1511879400
                      :b-p 1794.0  :b-t 1515076200})
                    (cm/gann
                     {:symbol s
                      :a-p 1300.0  :a-t 1511879400
                      :b-p 1517.0  :b-t 1515076200})])))

(defn make-gann-vert []
  (let [s "MSFT"
        chart-name (str "auto-gann-vert-test " s)
        client-id 77
        user-id 77
        chart-id 102]
    (cm/make-chart client-id user-id chart-id s chart-name
                   (let [a-t 1511879400
                         d-t 3196800]
                     (concat (cm/gann-vertical 1000.0 200.0 20 a-t (+ a-t d-t))
                             (cm/gann-vertical 1000.0 400.0 10 a-t (+ a-t (* 2 d-t))))))))


(comment

  (cm/make-chart 77 77 123 "AMZN" "test - empty chart" [])

  (make-ganns)
  (make-gann-vert)
  
;  
  )