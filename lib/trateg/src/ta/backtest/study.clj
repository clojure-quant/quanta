(ns ta.backtest.study
  (:require
   [tablecloth.api :as tc]
   [ta.warehouse :as wh]))

; study runner 

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

