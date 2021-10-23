;;; # Dynamic Asset Allocation
;;; 
;;; Backtest of a sector rotation strategy 
;;; described in https://drive.google.com/open?id=1FB1IpTC6WzfQkNoScCL3mQmOUVqTSENy

(ns demo.studies.asset-allocation-dynamic
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [ta.helper.print :refer [print-all]]
   ;[ta.helper.stats :refer [mean]]
   [ta.warehouse :as wh]
   ;[medley.core :as m]
   ;[clj-time.core :as t]
   ;[clj-time.coerce :as tc]
   ;[ta.series.indicator :as ind :refer [change-n]]
   ;[ta.model.trade :refer [set-ts get-ts trade]]
   ;[ta.model.rank :refer [rank]]
   ;[ta.model.stats :refer [gauntlet2]]
   ;[ta.series.compress :refer [compress group-month]]
   [ta.backtest.date :refer [month-begin? month-end? add-year-and-month-date-as-local-date]]
   [ta.series.indicator :refer [sma]]
   ;[ta.backtest.signal :refer [buy-above]]
   [ta.backtest.roundtrip-backtest :refer [run-backtest]]
   [ta.backtest.roundtrip-stats :refer [roundtrip-performance-metrics]]
   [ta.backtest.print]))

;; column views

(def col-study [:date :close :sma-v  :signal
                   ; :signal2
                   ;:sma-r
                ])

(def col-rt
  (concat [:year-month :symbol :sma-r] ta.backtest.print/cols-rt))

;; single symbol

(defn long-during-month-when-above-ma [mb? me? close sma-v]
  (if me?
    :flat
    (if mb?
      (if (> close sma-v)
        :buy
        :hold))))

(defn trade-sma-monthly
  [ds-bars {:keys [sma-length] #_:as #_options
            :or {sma-length 30}}]
  (let [{:keys [date close]} ds-bars
        sma-v (sma sma-length close)
        sma-r (dfn// close sma-v)
        m-b (month-begin? date)
        m-e (month-end? date)]
    (-> ds-bars
        add-year-and-month-date-as-local-date
        (tc/add-columns {:sma-v sma-v
                         :sma-r sma-r
                         :m-b m-b
                         ;:signal1 (dtype/emap buy-above :object close sma-v)
                         :signal (dtype/emap long-during-month-when-above-ma :object m-b m-e close sma-v)}))))

(comment
  (-> (wh/load-symbol :stocks "D" "QQQ")
      (trade-sma-monthly {:sma-length 20})
      (tc/select-columns col-study))
;
  )

(defn calc-rts-symbol [options]
  (-> (run-backtest trade-sma-monthly options)
      :ds-roundtrips
      (tc/select-rows (fn [{:keys [trade]}]
                        (= trade :buy)))))

(comment
  (-> (calc-rts-symbol {:w :stocks
                        :frequency "D"
                        :symbol "QQQ"
                        :sma-length 20
                        :entry-cols [:symbol :sma-r :year :month]})
      (tc/select-columns col-rt))
;  
  )

;; roundtrips for all symbols

(defn take-max [ds max-pos]
  (-> ds
      (tc/order-by [:sma-r])
      (tc/select-rows (range (min max-pos (tc/row-count ds))))))

(defn max-positions [ds-rts max-pos]
  (as-> ds-rts x
    (tc/group-by x [:year-month])
    (:data x)
    (map #(take-max % max-pos) x)
    (apply tc/concat x)))

(defn trade-sma-monthly-portfolio
  [{:keys [list max-pos]
    :or {max-pos 0}
    :as options}]
  (let [symbols (wh/load-list list)
        rt-seq (map (fn [s]
                      ;(println "calculating: " s)
                      (calc-rts-symbol (assoc options :symbol s))) symbols)
        ds-rts-all (-> (apply tc/concat rt-seq)
                       (tc/order-by [:year-month :symbol]))
        ds-rts (if (> max-pos 0)
                 (max-positions ds-rts-all max-pos)
                 ds-rts-all)
        ds-rts (tc/set-dataset-name ds-rts (str "max-pos-" max-pos))]
    {:ds-roundtrips ds-rts}))

(comment

  (def o {:w :stocks
          :frequency "D"
          :list "fidelity-select"
          :max-pos 5
          :symbol ""
          :sma-length 60
          :entry-cols [:symbol :sma-r :year-month]})

  (def backtest-all (trade-sma-monthly-portfolio (assoc o :max-pos 0)))
  (def backtest-5 (trade-sma-monthly-portfolio (assoc o :max-pos 5)))

  (roundtrip-performance-metrics backtest-5)
  (roundtrip-performance-metrics backtest-all)

  (-> backtest-5
      :ds-roundtrips
      (tc/select-columns col-rt)
      print-all)

; 
  )
;; Use ROC instead of SMA
;; Exit trades also with exit-rank ?
;; (list-plot (:equity pf) :joined true :color "blue")
;; (gauntlet2 pf)

(def lists ["bonds"
            "commodity-sector"
            "commodity-industry"
            "equity-region"
            ;"equity-region-country" 
            "equity-sector"
            ;"equity-sector-industry" ; not yet finished
            "equity-style"
            "currency"])

;(def crisis [{:start (tc/to-long (t/date-time 2020 03 03))
;              :end (tc/to-long (t/date-time 2020 03 20))
;              :color "orange"}])
;crisis

