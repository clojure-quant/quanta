(ns ta.tradingview.goldly.algo.test
  (:require
   [ta.tradingview.goldly.algo.context :as c]
   [ta.tradingview.goldly.algo.indicator :as i]
   [ta.algo.manager :as am :refer [algo-run-browser]]))

(am/get-algo "moon")

(defn request-data [algo-ctx]
  (let [{:keys [algo opts]} (c/get-algo-input algo-ctx)]
    (c/set-algo-data algo-ctx (algo-run-browser algo opts))))

(def ctx (c/create-algo-context "moon" {:symbol "QQQ"}))

ctx
(request-data ctx)
(c/get-data ctx)

(-> ctx c/get-data keys)
;; => (:study-extra-cols :ds-roundtrips  :name :ds-study :tradingview :options :charts :stats)

(:charts (c/get-data ctx))
;; => [nil {:volume "column"}]

(c/get-pane-columns ctx 0)
(c/get-pane-columns ctx 1)
(c/get-pane-data ctx 1 100)

; this is marks:
(-> ctx c/get-data :tradingview)

(def PineJS nil)

(c/get-pane-data ctx 1 10)
(c/get-pane-data ctx 1 100)

(-> ctx c/get-chart-series first)
;:epoch 941414400
(-> ctx c/get-chart-series last)
;:epoch 1692921600

(-> (c/get-chart-series-window ctx 1652921600 1692921600)
    count)

(i/calc-chart-pane ctx 1 PineJS)

; studies - this gets used in tradingview custom-indicator-getter

(def studies
  (i/study-chart-studies ctx PineJS))

(-> studies count)

(let [c (-> studies first :constructor)
      o (c)
      main (:main o)
      tradingview-ctx nil
      tradingview-input-cb nil]
  (main tradingview-ctx tradingview-input-cb))


