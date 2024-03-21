(ns ta.tradingview.db.clip
  (:require
   [modular.system]))

(defn get-bar-db []
  (modular.system/system :bardb-dynamic))

(def charts-path "../../output/tradingview-charts/")

(def template-path "../resources/tradingview-templates/")

(def marks-path "../../data/")

(defn import-for-exchange [exchange]
  (cond
    (= exchange :crypto) {:import :bybit}
    :else {:import :kibot}))
