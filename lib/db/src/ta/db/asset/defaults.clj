(ns ta.db.asset.defaults
  (:require
   [ta.db.asset.db :as db]))

(defn default-exchange [asset]
  (let [{:keys [symbol name category]} (db/instrument-details asset)]
    (case category
      :crypto :crypto
      :equity :us
      :fx :fx
      :future :us
      :stocks :us
      :us)))

(defn default-import [asset frequency]
  (let [{:keys [symbol name category]} (db/instrument-details asset)]
    (case category
      :crypto :bybit
      :equity (when (= frequency :d) :kibot)
      :fx (when (= frequency :d) :kibot)
      :future (when (= frequency :d) :kibot)
      :stocks (when (= frequency :d) :kibot)
      nil)))

(comment
  (db/instrument-details "NG0")

  (default-exchange "BTCUSDT")
  (default-exchange "SPY")
  (default-exchange "EUR/USD")
  (default-exchange  "NG0")

   ; not existing:
  (default-exchange "BAD-XXXADSFASDF")

;   
  )