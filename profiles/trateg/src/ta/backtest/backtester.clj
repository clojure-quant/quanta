(ns ta.backtest.backtester
  (:require
   [tablecloth.api :as tc]
   [ta.warehouse :as wh]))

; study runner 

(defn make-filename [frequency symbol]
  (str symbol "-" frequency))

(defn make-study-filename [study-name frequency symbol]
  (str "study-" study-name "-" symbol "-" frequency))

(defn save-study [w ds-study symbol frequency study-name]
  (wh/save-ts w ds-study (make-study-filename study-name frequency symbol))
  (tc/write! ds-study (str "../db/study-" study-name "-" symbol "-" frequency ".csv"))
  ds-study ; important to be here, as save-study is used often in a threading macro
  )

(defn load-study [w symbol frequency study-name]
  (let [ds (wh/load-ts w (make-study-filename study-name frequency symbol))]
    ds))

(defn run-study [w symbol frequency algo options]
  (let [ds (wh/load-symbol w frequency symbol)
        ds-study (algo ds options)]
    ds-study))



