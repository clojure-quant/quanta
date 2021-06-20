(ns demo.indicator-backtest
  (:require
   [clojure.pprint]
   [tech.v3.datatype.functional :as dfn]
   [ta.date :refer [parse-date]]
   [ta.warehouse :as wh]
   [ta.backtest.core :refer [calc-xf pf-backtest]]
   [ta.series.indicator :as ind :refer [field-xf sma-xf indicator  multiple-xf]]))


(def algo (comp (field-xf :close) (sma-xf 30)))

(defn pre-process [d]
  (let [sma30 (ind/sma 30 (d :close))
        sma200 (ind/sma 200 (d :close))]
    (-> d
        (assoc :sma30 sma30)
        (assoc :rsma30 (dfn// (d :close) sma30))
        (assoc :sma200 sma200)
        (assoc :rsma200 (dfn// (d :close) sma200)))))



(defn buy-rule [cur]
  ;(println "buy-rule: " cur)
  (if cur
  (let [buy (filter (fn [{:keys [symbol data]}]
                      (let [{:keys [rsma200 rsma30]} data]
                        (and rsma200 rsma30 (> rsma200 1.05) (< rsma30 0.95)))) 
                    cur)
            ;_ (println dt "buy: " buy)
        buy (sort-by (fn [x] (get-in x [:data :rsma30])) buy)
        buy (map (fn [{:keys [symbol data] :as x}]
                       (assoc x :data (dissoc data :open :high :low :volume)
                       )) buy)
        ;buy (reverse buy)
        ]
    ;(println "buys:" buy)
    buy)
    []
    ))

;(def symbols ["MSFT" "XOM"])
(def symbols (wh/load-list  "fidelity-select"))
symbols

(def p (pf-backtest {:start "2001-06-18"
                     :end "2021-05-01"
                     :initial-equity 100000
                     :pre-process pre-process
                     :buy-rule buy-rule
                     :trade-price-field :close}
                    symbols))

(spit "pf.txt"
      (with-out-str
        (clojure.pprint/print-table (:roundtrips p))))


(reduce + (map #(get-in % [:pl]) (:roundtrips p)))


(comment
  (def calc-until (calc-xf algo "XOM"))
  (calc-until (parse-date "2000-06-18"))
  (calc-until nil)

  (buy-rule [{:symbol "FSDCX" :data {:rsma200 1.20, :rsma30 0.95}} 
             {:symbol "FDCPX" :data {:rsma200 1.24, :rsma30 0.871}} 
             {:symbol "FSCSX" :data {:rsma200 1.12, :rsma30 1.01}}])


 ; 
  )
