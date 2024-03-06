(ns notebook.studies.asset-allocation-dynamic
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [ta.helper.print :refer [print-all]]
   [ta.helper.ds :refer [ds->str ds->map]]
   ;[ta.math.stats :refer [mean]]
   [ta.warehouse :as wh]
   ;[medley.core :as m]
   ;[clj-time.core :as t]
   ;[clj-time.coerce :as tc]
   ;[ta.series.indicator :as ind :refer [change-n]]
   ;[ta.model.trade :refer [set-ts get-ts trade]]
   ;[ta.model.rank :refer [rank]]
   ;[ta.model.stats :refer [gauntlet2]]
   ;[ta.calendar.compress :refer [compress group-month]]
   [ta.backtest.date :refer [month-begin? month-end? add-year-and-month-date-as-local-date]]
   [ta.series.indicator :refer [sma]]
   ;[ta.trade.signal :refer [buy-above]]
   [ta.trade.roundtrip-backtest :refer [run-backtest run-backtest-parameter-range]]
   [ta.trade.metrics.roundtrip-stats :refer [roundtrip-metrics backtests->performance-metrics]]
   [ta.trade.print]
   ))


(defn calc-rts-symbol [options]
  (-> (run-backtest trade-sma-monthly options)
      :ds-roundtrips
      (tc/select-rows (fn [{:keys [trade]}]
                        (= trade :buy)))))

(calc-rts-symbol {:w :stocks
                  :frequency "D"
                  :symbol "QQQ"
                  :sma-length 20
                  :entry-cols [:symbol :sma-r :year :month]})

;(tc/select-columns col-rt)

;; roundtrips for all symbols

(defn trade-sma-monthly-portfolio
  [algo {:keys [list max-pos sort-by where]
         :or {max-pos 0
              sort-by [:sma-r]
              where :middle}
         :as options}]
  (let [symbols (wh/load-list list)
        rt-seq (map (fn [s]
                      ;(println "calculating: " s)
                      (calc-rts-symbol (assoc options :symbol s))) symbols)
        ds-rts-all (-> (apply tc/concat rt-seq)
                       (tc/order-by [:year-month :symbol]))
        ds-rts (if (> max-pos 0)
                 (max-positions ds-rts-all max-pos sort-by where)
                 ds-rts-all)
        ds-rts (tc/set-dataset-name ds-rts (str "max-pos-" max-pos))]
    {:ds-roundtrips ds-rts}))
