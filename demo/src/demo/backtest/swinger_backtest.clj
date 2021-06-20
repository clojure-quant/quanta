(ns demo.swinger-backtest
  (:require
   [clojure.pprint]
   [ta.date :refer [parse-date]]
   [ta.warehouse :as wh]
   [ta.backtest.core :refer [calc-xf pf-backtest]]
   [ta.swings.transduce :refer [xf-swings]]))



(def algo (xf-swings true 30))


(defn buy-rule [cur]
  (let [buy (filter (fn [{:keys [symbol data]}]
                      (when (and symbol data)
                        (let [{:keys [dir len prct]} data]
                          (and dir len prct (= dir :up) (> len 10) (> prct 3.0))))) cur)
            ;_ (println dt "buy: " buy)
        buy (sort-by (fn [x] (get-in x [:data :prct])) buy)
        buy (reverse buy)]
    buy))

;(def symbols ["MSFT" "XOM"])
(def symbols (wh/load-list  "fidelity-select"))
symbols

 (def p (pf-backtest {:start "2000-06-18" 
                  :end "2021-05-01"
                  :initial-equity 100000}
                 algo
                 buy-rule
                 symbols))

(spit "pf.txt"
      (with-out-str
        (clojure.pprint/print-table (:roundtrips p))))


(reduce + (map #(get-in % [:pl]) (:roundtrips p)))


(comment
  (def calc-until (calc-xf algo "XOM"))
  (calc-until (parse-date "2000-06-18"))
  (calc-until nil)

 ; 
  )
