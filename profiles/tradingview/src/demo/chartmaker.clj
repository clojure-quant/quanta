(ns demo.chartmaker
  (:require
   [demo.env.config] ; side effects
   [reval.persist.edn] ; side-effects
   [ta.tradingview.chartmaker :as cm]))


(defn make-ganns [& _]
  (cm/make-chart 777  "MSFT" "alex"
                 [;source-pitchfork
                  (cm/trendline {:symbol "MSFT"
                                 :a-p 40.20  :a-t 1519223400
                                 :b-p 35.88060448358687  :b-t 1521725400})
                  (cm/gann
                   {:symbol "MSFT"
                    :a-p 1517.0  :a-t 1511879400
                    :b-p 1794.0  :b-t 1515076200})
                  (cm/gann
                   {:symbol "MSFT"
                    :a-p 1300.0  :a-t 1511879400
                    :b-p 1517.0  :b-t 1515076200})]))

(defn make-gann-vert [& _]
  (cm/make-chart 555  "AMZN" "vert ganns"
                 (let [a-t 1511879400
                       d-t 3196800]
                   (concat (cm/gann-vertical 1000.0 200.0 20 a-t (+ a-t d-t))
                           (cm/gann-vertical 1000.0 400.0 10 a-t (+ a-t (* 2 d-t)))))))
