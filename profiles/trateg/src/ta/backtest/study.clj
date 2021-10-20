(ns ta.backtest.study
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

(defn study-ds
  "algo has to create :position column
   creates roundtrips based on this column"
  [ds-bars algo algo-options]
  (let [ds-study (-> ds-bars
                     (algo algo-options))]
    {:ds-study ds-study}))

(defn run-study [algo {:keys [w symbol frequency] :as options}]
  (let [algo-options (dissoc options :w :symbol :frequency)
        ds-bars (wh/load-symbol w frequency symbol)]
    (study-ds ds-bars algo algo-options)))

